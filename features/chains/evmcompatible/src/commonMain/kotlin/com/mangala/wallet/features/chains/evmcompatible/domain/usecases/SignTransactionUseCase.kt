package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import cafe.adriel.voyager.core.concurrent.AtomicInt32
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.core.hdwallet.domain.model.HDKey
import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.domain.transaction.history.usecases.SaveTransactionHistoryUseCase
import com.mangala.wallet.features.chains.BlockSyncer
import com.mangala.wallet.features.chains.evmcompatible.core.TransactionBuilder
import com.mangala.wallet.features.chains.evmcompatible.core.amountToBigInt
import com.mangala.wallet.features.chains.evmcompatible.core.hexStringToLongOrNull
import com.mangala.wallet.features.chains.evmcompatible.core.signer.Signer
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import com.mangala.wallet.features.chains.evmcompatible.model.*
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlin.random.Random

class SignTransactionUseCase(
    private val getNonceUseCase: GetNonceUseCase,
    private val sendRawTransactionUseCase: SendRawTransactionUseCase,
    private val saveTransactionHistoryUseCase: SaveTransactionHistoryUseCase,
    private val parseNodeResponseUseCase: ParseNodeResponseUseCase,
    private val constructTempLogEventsUseCase: ConstructTempLogEventsUseCase,
    private val blockSyncer: BlockSyncer,
    private val parsingJson: Json
) {

    private val maxGasLimit: Long = 2_000_000

    private val currentId = AtomicInt32(Random.nextInt(100))

    suspend operator fun invoke(
        accountId: String,
        hdKey: HDKey,
        chain: Chain,
        isLegacyTransaction: Boolean,
        from: Address,
        to: String,
        amount: String,
        data: ByteArray, //payload
        gasPrice: GasPrice,
        gasLimit: Long?,
        rpcUrl: String,
        nonceInit: Long,
        coinDecimals: Long // for transaction history logging
    ): String? {
        val signer = Signer.getInstance(hdKey.privateKey, from, chain)
        val transactionBuilder = TransactionBuilder(from, chain.id)

        val nonce = if(nonceInit == -1L){
            getNonce(rpcUrl, from)
        }else{
            nonceInit
        }
       return send(
           accountId,
           chain.toBlockchainType().uid,
           from.hex,
           rpcUrl,
           signer,
           transactionBuilder,
           to,
           amount,
           gasPrice,
           gasLimit,
           nonce,
           data,
           coinDecimals
       )
    }

    suspend fun createContract(
        hdKey: HDKey,
        chain: Chain,
        from: Address,
        data: ByteArray, //payload
        gasPrice: GasPrice,
        gasLimit: Long?,
        rpcUrl: String,
        nonceInit: Long,
        amount: String,
    ): String? {
        val signer = Signer.getInstance(hdKey.privateKey, from, chain)
        val transactionBuilder = TransactionBuilder(from, chain.id)

        val nonce = if(nonceInit == -1L){
            getNonce(rpcUrl, from)
        }else{
            nonceInit
        }
//        val address = Address(to)
        val amountBigInt = amount.amountToBigInt()
//        val transactionData = TransactionData(address, value, data)

        if (gasLimit != null && nonce != null) {
//            val rawTransaction = rawTransaction(transactionData, gasPrice, gasLimit, nonce)
            val rawTransaction = RawTransaction(gasPrice, gasLimit, Address("0x0000000000000000000000000000000000000000"), amountBigInt, nonce, data, isDeploySmartContract = true)

            val signature: Signature = signer.signature(rawTransaction)
            val transaction = transactionBuilder.transaction(rawTransaction, signature)
            val encoded = transactionBuilder.encode(rawTransaction, signature)
            return sendRawTransaction(rpcUrl, encoded)
        }else{
            return null
        }
//        return null
    }


    suspend fun send(
        accountId: String,
        blockchainUid: String,
        fromAddress: String,
        rpcUrl: String,
        signer: Signer,
        transactionBuilder: TransactionBuilder,
        to: String,
        amount: String,
        gasPrice: GasPrice,
        gasLimit: Long?,
        nonce: Long?,
        data: ByteArray,
        coinDecimals: Long // for transaction history logging
    ): String? {
        val address = Address(to)
        val amountBigInt = amount.amountToBigInt()
        val transactionData = etherTransferTransactionData(address, amountBigInt, data)

        val sendResult = signAndSendData(
            rpcUrl,
            signer,
            transactionBuilder,
            transactionData,
            gasPrice,
            gasLimit,
            nonce
        )

        val txHash = sendResult?.let { parseNodeResponseUseCase(it) }

        // TODO: Query database? to check symbol for the crypto being sent

        val transaction = Transaction(
            blockHeight = 0,
            blockSignedAt = Clock.System.now(),
            feesPaid = "",
            fromAddress = fromAddress,
            fromAddressLabel = "",
            gasMetadata = Transaction.GasMetadata(),
            gasOffered = 0, // TODO: Fill in gas data from transaction
            gasPrice = 0,
            gasQuote = 0.0,
            gasQuoteRate = 0.0,
            gasSpent = 0,
            minerAddress = "",
            prettyGasQuote = "",
            prettyValueQuote = "",
            status = if (txHash.isNullOrBlank()) TransactionStatus.FAILED else TransactionStatus.PENDING, // TODO: Check if failed based on sendResult
            toAddress = to,
            toAddressLabel = "",
            txHash = txHash.orEmpty(),
            txOffset = 0,
            value = amount, // TODO: Handle decimals for native coin transfer
            valueQuote = 0.0,
            logEvents = constructTempLogEventsUseCase(fromAddress, to, data.toHexString(), coinDecimals),
            transactionType = TransactionType.SEND, // TODO: Distinguish between send and swap
            accountId = accountId
        )

        saveTransactionHistoryUseCase(
            accountId = accountId,
            blockchainUid = blockchainUid,
            transaction = transaction
        )

        blockSyncer.startSyncIfNotRunning(rpcUrl, Chain.fromBlockchainType(BlockchainType.fromUid(blockchainUid)))

        return sendResult
    }

    private suspend fun signAndSendData(
        rpcUrl: String,
        signer: Signer,
        transactionBuilder: TransactionBuilder,
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gasLimit: Long?,
        nonce: Long?
    ):String? {
        if (gasLimit != null && nonce != null) {
            val rawTransaction = rawTransaction(transactionData, gasPrice, gasLimit, nonce)
            rawTransaction?.let {
                val signature: Signature = signer.signature(rawTransaction)
                val transaction = transactionBuilder.transaction(rawTransaction, signature)
                val encoded = transactionBuilder.encode(rawTransaction, signature)
                return sendRawTransaction(rpcUrl, encoded)
            }
            return null
        }else{
            return null
        }
    }

    private fun etherTransferTransactionData(
        address: Address,
        value: BigInteger,
        input: ByteArray
    ): TransactionData {
        return TransactionData(address, value, input)
    }

    private fun rawTransaction(
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gasLimit: Long,
        nonce: Long
    ): RawTransaction? {

        if( transactionData.to != null){
            return RawTransaction(gasPrice, gasLimit, transactionData.to, transactionData.value, nonce, transactionData.input)
        }else{
            return null
        }
    }

    private suspend fun getNonce(rpcUrl: String, address: Address): Long? {
//        coroutineScope.launch {
        val data = getNonceUseCase.invoke(
            rpcUrl,
            currentId.getAndIncrement(),
            address,
            DefaultBlockParameter.Pending
        )
        val jsonRpcNodeResponse = parsingJson.decodeFromString(JsonRpcNodeResponse.serializer(), data)
        val nonce = jsonRpcNodeResponse.result?.hexStringToLongOrNull()

//        _getNonce.value = nonce
        return nonce
//        }
    }

    private suspend fun sendRawTransaction(rpcUrl: String, signedTransaction: ByteArray): String {
        return sendRawTransactionUseCase.invoke(
            rpcUrl,
            currentId.getAndIncrement(),
            signedTransaction
        )
    }

}