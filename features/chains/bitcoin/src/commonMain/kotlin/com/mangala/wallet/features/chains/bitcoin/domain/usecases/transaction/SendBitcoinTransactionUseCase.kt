package com.mangala.wallet.features.chains.bitcoin.domain.usecases.transaction

import com.mangala.wallet.core.hdwallet.domain.model.HDKey
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction
import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.features.chains.bitcoin.domain.repository.transaction.BitcoinTransactionRepository
import com.mangala.wallet.features.chains.bitcoin.domain.utils.BitcoinConstants
import com.mangala.wallet.features.chains.bitcoin.domain.utils.getChainHash
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import fr.acinq.bitcoin.Bitcoin.addressToPublicKeyScript
import fr.acinq.bitcoin.ByteVector
import fr.acinq.bitcoin.ByteVector32
import fr.acinq.bitcoin.Crypto
import fr.acinq.bitcoin.OP_PUSHDATA
import fr.acinq.bitcoin.OutPoint
import fr.acinq.bitcoin.PrivateKey
import fr.acinq.bitcoin.Satoshi
import fr.acinq.bitcoin.Script
import fr.acinq.bitcoin.ScriptWitness
import fr.acinq.bitcoin.SigHash.SIGHASH_ALL
import fr.acinq.bitcoin.XonlyPublicKey
import fr.acinq.bitcoin.Transaction
import fr.acinq.bitcoin.TxId
import fr.acinq.bitcoin.TxIn
import fr.acinq.bitcoin.TxOut
import fr.acinq.bitcoin.io.ByteArrayOutput
import fr.acinq.bitcoin.sat
import fr.acinq.bitcoin.utils.Either
import fr.acinq.lightning.blockchain.fee.FeeratePerKw
import fr.acinq.lightning.transactions.Transactions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SendBitcoinTransactionUseCase(
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase,
    private val bitcoinTransactionRepository: BitcoinTransactionRepository
) {

    suspend operator fun invoke(
        accountId: String,
        recipientAddress: String,
        amount: Satoshi,
        feeRate: FeeratePerKw,
        utxos: List<BitcoinUtxo>,
        changeAddress: String,
        blockchainType: BlockchainType
    ): Either<String, String> = withContext(Dispatchers.Default) {
        val selectedWallet =
            getSelectedWalletUseCase() ?: throw IllegalStateException("No wallet selected")
        val account = getAccountByIdUseCase.invokeSuspend(accountId)

        val seed = selectedWallet.words.split(" ")

        val hdKey = generateHDKeyUseCase.invoke(
            seed,
            "",
            Blockchain(blockchainType, blockchainType.uid, ""),
            AddressType.Bip84, // TODO: Must use the correct type of address based on the address passed in
            derivationPathIndex = account.derivationPathIndex
        )

        try {
            val selectedUtxos = selectOptimalUtxos(utxos, amount, feeRate, recipientAddress, changeAddress)
            println("SendBitcoinTransactionUseCase selected utxos $selectedUtxos")
            val unsigned = createUnsignedTransaction(
                blockchainType,
                selectedUtxos,
                recipientAddress,
                amount,
                feeRate,
                changeAddress
            )

            val signed = signTransaction(unsigned, selectedUtxos, hdKey, blockchainType)

            println("SendBitcoinTransactionUseCase signed transaction weight ${signed.weight()}")

            val txId = broadcastTransaction(signed, blockchainType)

            Either.Right(txId)
        } catch (e: Exception) {
            println("SendBitcoinTransactionUseCase exception $e")
            Either.Left("SendBitcoinTransactionUseCase Transaction failed: ${e.message}")
        }
    }

    private fun selectOptimalUtxos(
        utxos: List<BitcoinUtxo>,
        targetAmount: Satoshi,
        feeRate: FeeratePerKw,
        recipientAddress: String,
        changeAddress: String
    ): List<BitcoinUtxo> {
        val sortedUtxos = utxos.sortedByDescending { it.amountInSatoshis.sat() }

        val selectedUtxos = mutableListOf<BitcoinUtxo>()
        var totalSelected = Satoshi(0)

        // Simple coin selection strategy: select UTXOs until we have enough
        // A more sophisticated algorithm could be implemented here
        for (utxo in sortedUtxos) {
            selectedUtxos.add(utxo)
            totalSelected += utxo.amountInSatoshis.sat()

            // Determine if we're using SegWit by analyzing the address formats
            val segwitAddressPattern = "^(bc1|tb1)".toRegex()
            val isSegwit = segwitAddressPattern.containsMatchIn(recipientAddress) ||
                          (changeAddress.isNotEmpty() && segwitAddressPattern.containsMatchIn(changeAddress))

            val currentTotal = totalSelected

            val estimatedWeightOneOutput = estimateTransactionWeight(
                numInputs = selectedUtxos.size,
                numOutputs = 1,
                isSegwit = isSegwit
            )
            val estimatedFeeOneOutput = Transactions.weight2fee(feerate = feeRate, weight = estimatedWeightOneOutput)

            val potentialChange = currentTotal - targetAmount - estimatedFeeOneOutput
            val wouldHaveChange = potentialChange.sat > BitcoinConstants.DUST_SATS_AMOUNT

            val outputCount = if (wouldHaveChange) 2 else 1
            val estimatedWeight = estimateTransactionWeight(
                numInputs = selectedUtxos.size,
                numOutputs = outputCount,
                isSegwit = isSegwit
            )
            val estimatedFee = Transactions.weight2fee(feerate = feeRate, weight = estimatedWeight)

            // Check if we have enough funds
            if (totalSelected.sat >= targetAmount.sat + estimatedFee.sat) {
                break
            }
        }

        if (totalSelected.sat < targetAmount.sat) {
            throw InsufficientFundsException("Not enough funds to create transaction")
        }

        return selectedUtxos
    }

    private fun createUnsignedTransaction(
        blockchainType: BlockchainType,
        selectedUtxos: List<BitcoinUtxo>,
        recipientAddress: String,
        amount: Satoshi,
        feeRate: FeeratePerKw,
        changeAddress: String
    ): Transaction {
        val inputs = selectedUtxos.map { utxo ->
            TxIn(
                outPoint = OutPoint(TxId(utxo.txId), utxo.vout.toLong()),
                signatureScript = ByteVector.empty, // Empty script for now, will be filled during signing
                sequence = TxIn.SEQUENCE_FINAL,
                witness = ScriptWitness.empty
            )
        }

        val outputs = mutableListOf<TxOut>()

        val recipientScript = addressToPublicKeyScript(getChainHash(blockchainType), recipientAddress).right
                ?: throw IllegalStateException("Invalid recipient address")
        outputs.add(TxOut(amount, recipientScript))

        // Calculate total input amount
        val totalInput = selectedUtxos.sumOf { it.amountInSatoshis }.sat()

        // Determine if we're using SegWit by analyzing the address formats
        val segwitAddressPattern = "^(bc1|tb1)".toRegex()
        val isSegwit = segwitAddressPattern.containsMatchIn(recipientAddress) ||
                       segwitAddressPattern.containsMatchIn(changeAddress)

        // First calculate fee assuming only one output (recipient)
        val estimatedWeightOneOutput = estimateTransactionWeight(
            numInputs = inputs.size,
            numOutputs = 1,
            isSegwit = isSegwit
        )
        val feeOneOutput = Transactions.weight2fee(feerate = feeRate, weight = estimatedWeightOneOutput)

        // Check if potential change would be dust
        val potentialChangeAmount = totalInput - amount - feeOneOutput
        val wouldHaveChange = potentialChangeAmount.sat > BitcoinConstants.DUST_SATS_AMOUNT

        // Use the appropriate output count for final fee estimation
        val outputCount = if (wouldHaveChange) 2 else 1
        val estimatedWeight = estimateTransactionWeight(
            numInputs = inputs.size,
            numOutputs = outputCount,
            isSegwit = isSegwit
        )
        println("SendBitcoinTransactionUseCase estimated weight $estimatedWeight for $outputCount outputs")
        val fee = Transactions.weight2fee(feerate = feeRate, weight = estimatedWeight)

        // Calculate change amount
        val changeAmount = totalInput - amount - fee

        // Add change output if not dust
        if (changeAmount.sat > BitcoinConstants.DUST_SATS_AMOUNT) {
//            val changeScript = addressToScript(changeAddress)
            // TODO: Dynamically determine change address for privacy
            val changeScript =
                addressToPublicKeyScript(getChainHash(blockchainType), changeAddress).right
                    ?: throw IllegalStateException("Invalid change address")
            outputs.add(TxOut(changeAmount, changeScript))
        }

        // Create the transaction
        return Transaction(
            version = 2,
            txIn = inputs,
            txOut = outputs,
            lockTime = 0
        )
    }

    private suspend fun signTransaction(
        transaction: Transaction,
        utxos: List<BitcoinUtxo>,
        hdKey: HDKey,
        blockchainType: BlockchainType
    ): Transaction {
        val ecKey = PrivateKey(ByteVector(hdKey.privateKey))
        val publicKey = ecKey.publicKey()
        
        var signedTx = transaction
        
        utxos.forEachIndexed { index, utxo ->
            val scriptPubKey = getScriptPubKeyForUtxo(utxo.txId, utxo.vout.toLong(), blockchainType)
            
            val signatureVersion = when {
                Script.isPay2pkh(scriptPubKey.toByteArray()) || Script.isPay2sh(scriptPubKey.toByteArray()) -> 0
                Script.isPay2wpkh(scriptPubKey.toByteArray()) || Script.isPay2wsh(scriptPubKey.toByteArray()) -> 1
                Script.isPay2tr(scriptPubKey.toByteArray()) -> 0
                else -> throw IllegalArgumentException("Unsupported script type")
            }
            
            when {
                Script.isPay2wpkh(scriptPubKey.toByteArray()) -> {
                    val sig = Transaction.signInput(
                        tx = signedTx,
                        inputIndex = index,
                        previousOutputScript = Script.pay2pkh(publicKey),
                        sighashType = SIGHASH_ALL,
                        amount = utxo.amountInSatoshis.sat(),
                        privateKey = ecKey,
                        signatureVersion = signatureVersion
                    )
                    
                    val witness = ScriptWitness(
                        listOf(
                            ByteVector(sig),
                            publicKey.value
                        )
                    )
                    
                    signedTx = signedTx.updateWitness(index, witness)
                }
                
                Script.isPay2pkh(scriptPubKey.toByteArray()) -> {
                    val sig = Transaction.signInput(
                        tx = signedTx,
                        inputIndex = index,
                        previousOutputScript = scriptPubKey.toByteArray(),
                        sighashType = SIGHASH_ALL,
                        privateKey = ecKey
                    )
                    
                    // Create a signature script with DER-encoded signature and public key
                    // P2PKH script format is: <sig> <pubkey>
                    val sigScriptElements = listOf(
                        OP_PUSHDATA(ByteVector(sig)),
                        OP_PUSHDATA(publicKey.value)
                    )
                    
                    // Write the script elements to a byte array and update the input
                    signedTx = signedTx.updateSigScript(index, Script.write(sigScriptElements))
                }
                
                // Nested SegWit: P2SH-P2WPKH (Pay-to-Script-Hash containing a P2WPKH script)
                Script.isPay2sh(scriptPubKey.toByteArray()) -> {
                    try {
                        // For P2SH-P2WPKH: Create the redeem script which is the P2WPKH script
                        val redeemScript = Script.write(Script.pay2wpkh(publicKey.hash160()))
                        
                        // 1. Create the signature using SegWit signing
                        val sig = Transaction.signInput(
                            tx = signedTx,
                            inputIndex = index,
                            previousOutputScript = Script.pay2pkh(publicKey),
                            sighashType = SIGHASH_ALL,
                            amount = utxo.amountInSatoshis.sat(),
                            privateKey = ecKey,
                            signatureVersion = 1 // SegWit
                        )
                        
                        // 2. Create the witness stack (same as P2WPKH)
                        val witness = ScriptWitness(
                            listOf(
                                ByteVector(sig),
                                publicKey.value
                            )
                        )
                        
                        // 3. Create the scriptSig that just pushes the redeem script
                        val sigScript = Script.write(listOf(
                            OP_PUSHDATA(ByteVector(redeemScript))
                        ))
                        
                        // 4. Update both signature script and witness
                        signedTx = signedTx.updateSigScript(index, sigScript)
                        signedTx = signedTx.updateWitness(index, witness)
                    } catch (e: Exception) {
                        throw IllegalArgumentException("Error signing P2SH input: ${e.message}")
                    }
                }
                
                // Pay-to-Witness-Script-Hash (P2WSH) - Limited support for single-key
                Script.isPay2wsh(scriptPubKey.toByteArray()) -> {
                    // Note: Full P2WSH support requires knowing the complete witness script
                    // This implementation handles only the simple case where we know our key is the only one
                    
                    // Create a simple witness script (single-key)
                    val witnessScript = Script.write(Script.pay2pkh(publicKey))
                    
                    // Sign the input
                    val sig = Transaction.signInput(
                        tx = signedTx,
                        inputIndex = index,
                        previousOutputScript = witnessScript,
                        sighashType = SIGHASH_ALL,
                        amount = utxo.amountInSatoshis.sat(),
                        privateKey = ecKey,
                        signatureVersion = 1 // SegWit
                    )
                    
                    // Witness for P2WSH is: 0 <sig> <pubkey> <witnessScript>
                    val witness = ScriptWitness(
                        listOf(
                            ByteVector.empty,  // OP_0 to solve the multisig dummy element issue
                            ByteVector(sig),
                            publicKey.value,
                            ByteVector(witnessScript)
                        )
                    )
                    
                    signedTx = signedTx.updateWitness(index, witness)
                }
                
                // Pay-to-Taproot (P2TR)
                Script.isPay2tr(scriptPubKey.toByteArray()) -> {
                    // Note: This implements key-path spending only (not script path)
                    // We're using BIP340/341 Schnorr signatures
                    
                    // Get the 32-byte x-only pubkey for Taproot
                    val xOnlyPubKey = XonlyPublicKey(ByteVector32(publicKey.value.drop(1)))
                    
                    // Create a Schnorr signature
                    val sigHash = Transaction.hashForSigningTaprootKeyPath(
                        inputIndex = index,
                        inputs = utxos.map { 
                            TxOut(it.amountInSatoshis.sat(), ByteVector.empty) 
                        },
                        tx = signedTx,
                        sighashType = SIGHASH_ALL
                    )
                    
                    // Create signature using Schnorr
                    val signature = Crypto.signSchnorr(
                        data = sigHash,
                        privateKey = ecKey,
                        auxrand32 = null,
                        schnorrTweak = Crypto.SchnorrTweak.NoTweak // TODO: Determine tweak setting
                    )
                    
                    // For Taproot key-path, witness is just the signature
                    val witness = if (SIGHASH_ALL == 0) {
                        // Default sighash doesn't need a suffix
                        ScriptWitness(listOf(signature))
                    } else {
                        // Non-default sighash needs a suffix byte
                        ScriptWitness(listOf(signature.concat(SIGHASH_ALL.toByte())))
                    }
                    
                    signedTx = signedTx.updateWitness(index, witness)
                }
                
                else -> throw IllegalArgumentException("Unsupported script type: ${utxo}")
            }
        }
        
        return signedTx
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun broadcastTransaction(
        transaction: Transaction,
        blockchainType: BlockchainType
    ): String {
        val serialized = serializeTransaction(transaction)

        val txMessage = createTransactionMessage(serialized)

        val response = sendToNode(txMessage.toHexString(), blockchainType)

        val exception = response.exceptionOrNull()

        if (exception != null) throw exception

        return response.getOrNull() ?: throw IllegalStateException("No txId returned")
    }

    private fun serializeTransaction(transaction: Transaction): ByteArray {
        val output = ByteArrayOutput()
        Transaction.write(transaction, output)
        return output.toByteArray()
    }

    private fun createTransactionMessage(serializedTx: ByteArray): ByteArray {
        // TODO: Simplified - in a real implementation, you would need to format
        // according to the Bitcoin P2P protocol specifications
        return serializedTx
    }

    private suspend fun sendToNode(
        txMessage: String,
        blockchainType: BlockchainType
    ): Result<String> {
        return bitcoinTransactionRepository.sendTransaction(
            transactionHex = txMessage,
            blockchainType
        )
    }

    private suspend fun getScriptPubKeyForUtxo(
        txid: String,
        vout: Long,
        blockchainType: BlockchainType
    ): ByteVector {
        val txResponse = getTransactionById(txid, blockchainType).getOrNull()

        val outputData = txResponse?.vout?.getOrNull(vout.toInt())
            ?: throw IllegalStateException("Output $vout not found in transaction $txid")

        // Convert hex string to ByteVector
        return ByteVector.fromHex(outputData.scriptpubkey)
    }

    private suspend fun getTransactionById(
        txId: String,
        blockchainType: BlockchainType
    ): Result<BitcoinTransaction> {
        return bitcoinTransactionRepository.getTransactionLatestInfo(
            txId = txId,
            blockchainType = blockchainType,
        )
    }

    /**
     * Accurately estimates the transaction weight by creating a dummy transaction
     * with the correct structure and using bitcoin-kmp's weight calculation.
     * 
     * Bitcoin transaction weight is calculated as:
     * - Legacy (non-SegWit) transactions: 4 * size in bytes
     * - SegWit transactions: (3 * base size) + (1 * witness size)
     * 
     * This approach creates a realistic dummy transaction matching the actual
     * transaction we'll create, including proper input/output structure and
     * witness data for SegWit transactions.
     */
    private fun estimateTransactionWeight(
        numInputs: Int,
        numOutputs: Int,
        isSegwit: Boolean = true
    ): Int {
        // Create dummy inputs with appropriate structure
        val inputs = List(numInputs) {
            TxIn(
                outPoint = OutPoint(TxId(ByteVector32.Zeroes), 0),
                signatureScript = ByteVector.empty,
                sequence = TxIn.SEQUENCE_FINAL,
                witness = if (isSegwit) {
                    // P2WPKH witness has a signature (71-73 bytes) and a pubkey (33 bytes)
                    ScriptWitness(listOf(
                        ByteVector(ByteArray(72)), // Typical signature size with sighash
                        ByteVector(ByteArray(33))  // Compressed pubkey
                    ))
                } else {
                    ScriptWitness.empty
                }
            )
        }

        // Create dummy outputs with appropriate P2WPKH scripts
        val outputs = List(numOutputs) {
            TxOut(
                amount = 1000L.sat(),
                publicKeyScript = Script.pay2wpkh(ByteArray(20)) // 20-byte pubkey hash
            )
        }

        val dummyTx = Transaction(
            version = 2L,
            txIn = inputs,
            txOut = outputs,
            lockTime = 0L
        )
        
        return dummyTx.weight()
    }
}

class InsufficientFundsException(message: String) : Exception(message)