package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import cafe.adriel.voyager.core.concurrent.AtomicInt32
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.core.hdwallet.domain.model.HDKey
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.domain.transaction.history.usecases.SaveTransactionHistoryUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.BlockSyncer
import com.mangala.wallet.features.chains.erc20.contract.TransferMethod
import com.mangala.wallet.features.chains.evmcompatible.core.TransactionBuilder
import com.mangala.wallet.features.chains.evmcompatible.core.amountToBigInt
import com.mangala.wallet.features.chains.evmcompatible.core.hexStringToLongOrNull
import com.mangala.wallet.features.chains.evmcompatible.core.signer.Signer
import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlin.random.Random

class SendTokenUseCase(
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase,
    private val getNonceUseCase: GetNonceUseCase,
    private val signAndSendTransactionDataUseCase: SignAndSendTransactionDataUseCase,
    private val saveTransactionHistoryUseCase: SaveTransactionHistoryUseCase,
    private val blockSyncer: BlockSyncer,
    private val parsingJson: Json
) {

    private val maxGasLimit: Long = 2_000_000

    private val currentId = AtomicInt32(Random.nextInt(100))

    suspend fun sendCoin(
        accountId: String,
        blockchainType: BlockchainType,
        addressType: AddressType,
        to: String,
        amount: String,
        gasPrice: GasPrice,
        rpcUrl: String,
        gasLimit: Long?,
        coinSymbol: String
    ): String? {
        return prepareAndSend(
            accountId = accountId,
            blockchainType = blockchainType,
            addressType = addressType,
            rpcUrl = rpcUrl
        ) { fromAddress, signer, transactionBuilder, nonce ->
            val txHash = send(
                rpcUrl,
                signer,
                transactionBuilder,
                to,
                amount,
                gasPrice,
                gasLimit,
                nonce
            )

            saveTransactionHistoryUseCase(
                accountId = accountId,
                blockchainUid = blockchainType.uid,
                Transaction(
                    blockHeight = 0,
                    blockSignedAt = Clock.System.now(),
                    feesPaid = "",
                    fromAddress = fromAddress,
                    fromAddressLabel = "",
                    gasMetadata = Transaction.GasMetadata(
                        contractTickerSymbol = coinSymbol
                    ),
                    gasOffered = gasLimit ?: 0, // TODO: Fill in gas data from transaction
                    gasPrice = gasPrice.maxGas,
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
                    value = amount,
                    valueQuote = 0.0,
                    logEvents = null,
                    transactionType = TransactionType.SEND,
                    accountId = accountId
                )
            )

            blockSyncer.startSyncIfNotRunning(rpcUrl, Chain.fromBlockchainType(blockchainType))

            txHash
        }
    }

    suspend fun sendToken(
        accountId: String,
        blockchainType: BlockchainType,
        addressType: AddressType,
        contractAddress: String?,
        to: String,
        amount: String,
        gasPrice: GasPrice,
        rpcUrl: String,
        gasLimit: Long?,
        tokenSymbol: String
    ): String? {
        if (contractAddress.isNullOrEmpty()) return null

        return prepareAndSend(
            accountId = accountId,
            blockchainType = blockchainType,
            addressType = addressType,
            rpcUrl = rpcUrl
        ) { fromAddress, signer, transactionBuilder, nonce ->

            val txHash = sendERC20(
                rpcUrl,
                signer,
                transactionBuilder,
                contractAddress,
                to,
                amount,
                gasPrice,
                gasLimit,
                nonce
            )

            saveTransactionHistoryUseCase(
                accountId = accountId,
                blockchainUid = blockchainType.uid,
                Transaction(
                    blockHeight = 0,
                    blockSignedAt = Clock.System.now(),
                    feesPaid = "",
                    fromAddress = fromAddress,
                    fromAddressLabel = "",
                    gasMetadata = Transaction.GasMetadata(),
                    gasOffered = gasLimit ?: 0, // TODO: Fill in gas data from transaction
                    gasPrice = 0,
                    gasQuote = 0.0,
                    gasQuoteRate = 0.0,
                    gasSpent = 0,
                    minerAddress = "",
                    prettyGasQuote = "",
                    prettyValueQuote = "",
                    status = if (txHash.isNullOrBlank()) TransactionStatus.FAILED else TransactionStatus.PENDING, // TODO: Check if failed based on sendResult
                    toAddress = contractAddress,
                    toAddressLabel = "",
                    txHash = txHash.orEmpty(),
                    txOffset = 0,
                    value = amount,
                    valueQuote = 0.0,
                    logEvents = constructSendTokenLogEvents(fromAddress, to, amount, tokenSymbol),
                    transactionType = TransactionType.SEND,
                    accountId = accountId
                )
            )

            txHash
        }
    }

    private fun constructSendTokenLogEvents(
        fromAddress: String,
        to: String,
        amount: String,
        tokenSymbol: String
    ) = listOf(
        Transaction.LogEvent(
            senderContractTickerSymbol = tokenSymbol,
            decoded = Transaction.LogEvent.Decoded(
                "Transfer", listOf(
                    Transaction.LogEvent.Decoded.Param(
                        name = "from",
                        value = Transaction.LogEvent.Decoded.ParamValue.Primitive(fromAddress)
                    ),
                    Transaction.LogEvent.Decoded.Param(
                        name = "to",
                        value = Transaction.LogEvent.Decoded.ParamValue.Primitive(to)
                    ),
                    Transaction.LogEvent.Decoded.Param(
                        name = "value",
                        value = Transaction.LogEvent.Decoded.ParamValue.Primitive(amount)
                    ),
                )
            )
        )
    )

    suspend operator fun invoke(
        hdKey: HDKey,
        chain: Chain,
        contractAddress: String,
        from: Address,
        to: String,
        amount: String,
        isERC20: Boolean,
        gasPrice: GasPrice,
        rpcUrl: String,
        gas: Long?
    ) {

        val nonce = getNonce(rpcUrl, from)

        val signer = Signer.getInstance(hdKey.privateKey, from, chain)
        val transactionBuilder = TransactionBuilder(from, chain.id)

        if (isERC20) {
            sendERC20(
                rpcUrl,
                signer,
                transactionBuilder,
                contractAddress,
                to,
                amount,
                gasPrice,
                gas,
                nonce
            )
        } else {
            send(
                rpcUrl,
                signer,
                transactionBuilder,
                to,
                amount,
                gasPrice,
                gas,
                nonce
            )
        }
    }

    private suspend fun prepareAndSend(
        accountId: String,
        blockchainType: BlockchainType,
        addressType: AddressType,
        rpcUrl: String,
        sendFunction: suspend (
            fromAddress: String,
            signer: Signer,
            transactionBuilder: TransactionBuilder,
            nonce: Long?
        ) -> String?
    ): String? {
        val selectedWallet = getSelectedWalletUseCase() ?: throw IllegalStateException("No wallet selected")
        val account = getAccountByIdUseCase(accountId)
        val hdKey = getHDKey(
            Blockchain(blockchainType, "", null),
            addressType,
            selectedWallet.words,
            account.derivationPathIndex
        )
        val wrappedAddress = Address(account.bip44Address) // chains not EVM-compatible may need different address type
        val nonce = getNonce(rpcUrl, wrappedAddress)

        val chain = Chain.fromBlockchainType(blockchainType)
        val signer = Signer.getInstance(hdKey.privateKey, wrappedAddress, chain)
        val transactionBuilder = TransactionBuilder(wrappedAddress, chain.id)

        return sendFunction(
            account.bip44Address,
            signer,
            transactionBuilder,
            nonce
        )
    }

    suspend fun sendRawDataNow(
        hdKey: HDKey,
        from: Address,
        chain: Chain,
        rpcUrl: String,
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gas: Long?
    ): String? {

        val nonce = getNonce(rpcUrl, from)

        val signer = Signer.getInstance(hdKey.privateKey, from, chain)
        val transactionBuilder = TransactionBuilder(from, chain.id)
        return signAndSendTransactionDataUseCase.signAndSendData(
            rpcUrl,
            signer,
            transactionBuilder,
            transactionData,
            gasPrice,
            gas,
            nonce
        )
    }

    suspend fun send(
        rpcUrl: String,
        signer: Signer,
        transactionBuilder: TransactionBuilder,
        to: String,
        amount: String,
        gasPrice: GasPrice,
        gasLimit: Long?,
        nonce: Long?,
    ): String? {
        val address = Address(to)
        val amountBigInt = amount.amountToBigInt()
        val transactionData = etherTransferTransactionData(address, amountBigInt)

        return signAndSendTransactionDataUseCase.signAndSendData(
            rpcUrl,
            signer,
            transactionBuilder,
            transactionData,
            gasPrice,
            gasLimit,
            nonce
        )
    }

    private suspend fun sendERC20(
        rpcUrl: String,
        signer: Signer,
        transactionBuilder: TransactionBuilder,
        contractAddress: String,
        to: String,
        amount: String,
        gasPrice: GasPrice,
        gasLimit: Long?,
        nonce: Long?,
    ): String? {

        val address = Address(to)
        val amountBigInt = amount.amountToBigInt()
        val transactionData =
            buildTransferTransactionData(Address(contractAddress), address, amountBigInt)

        return signAndSendTransactionDataUseCase.signAndSendData(
            rpcUrl,
            signer,
            transactionBuilder,
            transactionData,
            gasPrice,
            gasLimit,
            nonce
        )
    }

    private fun etherTransferTransactionData(address: Address, value: BigInteger): TransactionData {
        return TransactionData(address, value, byteArrayOf())
    }

    private fun getHDKey(
        blockchain: Blockchain,
        addressType: AddressType,
        defaultsWords: String,
        derivationPathIndex: Int
    ): HDKey {
        val words = defaultsWords.split(" ")
        return generateHDKeyUseCase.invoke(
            seedPhrase = words,
            blockchain = blockchain,
            addressType = addressType,
            derivationPathIndex = derivationPathIndex
        )
    }

    private fun buildTransferTransactionData(
        contractAddress: Address,
        to: Address,
        value: BigInteger
    ): TransactionData {
        return TransactionData(
            to = contractAddress,
            value = BigInteger.ZERO,
            TransferMethod(to, value).encodedABI()
        )
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

}