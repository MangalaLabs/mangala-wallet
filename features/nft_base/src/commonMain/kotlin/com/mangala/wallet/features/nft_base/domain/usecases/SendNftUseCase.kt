package com.mangala.wallet.features.nft_base.domain.usecases

import cafe.adriel.voyager.core.concurrent.AtomicInt32
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.core.hdwallet.domain.model.HDKey
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.domain.transaction.history.usecases.SaveTransactionHistoryUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.BlockSyncer
import com.mangala.wallet.features.chains.erc1155.contract.Eip1155SafeTransferFromMethod
import com.mangala.wallet.features.chains.erc721.contract.Eip721SafeTransferFromMethod
import com.mangala.wallet.features.chains.evmcompatible.core.TransactionBuilder
import com.mangala.wallet.features.chains.evmcompatible.core.amountToBigInt
import com.mangala.wallet.features.chains.evmcompatible.core.hexStringToLongOrNull
import com.mangala.wallet.features.chains.evmcompatible.core.signer.Signer
import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EstimateGasUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendRawTransactionUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.RawTransaction
import com.mangala.wallet.features.chains.evmcompatible.model.Signature
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.features.nft_base.domain.model.NftType
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlin.random.Random

class SendNftUseCase(
    private val estimateGasUseCase: EstimateGasUseCase,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase,
    private val getNonceUseCase: GetNonceUseCase,
    private val sendRawTransactionUseCase: SendRawTransactionUseCase,
    private val saveTransactionHistoryUseCase: SaveTransactionHistoryUseCase,
    private val blockSyncer: BlockSyncer,
    private val parsingJson: Json
) {
    private val maxGasLimit: Long = 2_000_000

    private val currentId = AtomicInt32(Random.nextInt(100))

    suspend operator fun invoke(
        accountId: String,
        blockchainType: BlockchainType,
        addressType: AddressType,
        to: String,
        gasPrice: GasPrice,
        rpcUrl: String,
        gasLimit: Long?,
        nftCollection: NftCollection
    ): String? {
        if (nftCollection.contractAddress.isEmpty()) return null
        if (nftCollection.nft.size != 1) return null

        val nft = nftCollection.nft.first()

        return prepareAndSend(
            accountId = accountId,
            blockchainType = blockchainType,
            addressType = addressType,
            rpcUrl = rpcUrl
        ) { fromAddress, signer, transactionBuilder, nonce ->
            val txHash = when (nftCollection.type) {
                NftType.ERC721 -> sendERC721(
                    rpcUrl = rpcUrl,
                    signer = signer,
                    transactionBuilder = transactionBuilder,
                    contractAddress = nftCollection.contractAddress,
                    from = fromAddress,
                    to = to,
                    tokenId = nft.tokenId,
                    gasPrice = gasPrice,
                    gasLimit = gasLimit,
                    nonce = nonce
                )
                NftType.ERC1155 -> sendERC1155(
                    rpcUrl = rpcUrl,
                    signer = signer,
                    transactionBuilder = transactionBuilder,
                    contractAddress = nftCollection.contractAddress,
                    from = fromAddress,
                    to = to,
                    tokenId = nft.tokenId,
                    gasPrice = gasPrice,
                    gasLimit = gasLimit,
                    nonce = nonce
                )
            }

            if (txHash.isNullOrBlank()) return@prepareAndSend txHash

            val logEvents = buildSendNftLogEvents(
                fromAddress = fromAddress,
                to = to,
                tokenId = nft.tokenId,
                tokenSymbol = nftCollection.contractTickerSymbol
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
                    gasOffered = gasLimit ?: 0,
                    gasPrice = gasPrice.maxGas,
                    gasQuote = 0.0,
                    gasQuoteRate = 0.0,
                    gasSpent = 0L,
                    minerAddress = "",
                    prettyGasQuote = "",
                    prettyValueQuote = "",
                    status = if (txHash.isBlank()) TransactionStatus.FAILED else TransactionStatus.PENDING, // TODO: Check if failed based on sendResult
                    toAddress = nftCollection.contractAddress,
                    toAddressLabel = "",
                    txHash = txHash,
                    txOffset = 0,
                    value = "0",
                    valueQuote = 0.0,
                    logEvents = logEvents,
                    transactionType = TransactionType.SEND,
                    accountId = accountId,
                    isNftTransaction = true
                )
            )

            blockSyncer.startSyncIfNotRunning(rpcUrl, Chain.fromBlockchainType(blockchainType))

            txHash
        }
    }

    suspend fun estimateGas(
        rpcUrl: String,
        nftCollection: NftCollection,
        fromAddress: String,
        toAddress: String,
        preferredGasPrice: GasPrice
    ): Long? {
        val transactionData = buildTransactionData(nftCollection, fromAddress, toAddress) ?: return null
        val contractAddress = nftCollection.contractAddress
        val from = Address(fromAddress)

        return when (nftCollection.type) {
            NftType.ERC721 -> {
                estimateGasUseCase.invoke(
                    url = rpcUrl,
                    id = currentId.getAndIncrement(),
                    from = Address(fromAddress),
                    to = Address(contractAddress),
                    amount = BigInteger.ZERO,
                    gasPrice = preferredGasPrice,
                    transactionData = transactionData
                )
            }
            NftType.ERC1155 -> {
                estimateGasUseCase.invoke(
                    url = rpcUrl,
                    id = currentId.getAndIncrement(),
                    from = from,
                    to = Address(contractAddress),
                    amount = BigInteger.ZERO,
                    gasPrice = preferredGasPrice,
                    transactionData = transactionData
                )
            }
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

        return signAndSendData(
            rpcUrl,
            signer,
            transactionBuilder,
            transactionData,
            gasPrice,
            gasLimit,
            nonce
        )
    }

    private suspend fun sendERC721(
        rpcUrl: String,
        signer: Signer,
        transactionBuilder: TransactionBuilder,
        contractAddress: String,
        from: String,
        to: String,
        tokenId: String,
        gasPrice: GasPrice,
        gasLimit: Long?,
        nonce: Long?,
    ): String? {
        val fromAddress = Address(from)
        val toAddress = Address(to)
        val transactionData =
            buildErc721SafeTransferFromTransactionData(Address(contractAddress), fromAddress, toAddress, tokenId.toBigInteger())

        return signAndSendData(
            rpcUrl,
            signer,
            transactionBuilder,
            transactionData,
            gasPrice,
            gasLimit,
            nonce
        )
    }

    private suspend fun sendERC1155(
        rpcUrl: String,
        signer: Signer,
        transactionBuilder: TransactionBuilder,
        contractAddress: String,
        from: String,
        to: String,
        tokenId: String,
        gasPrice: GasPrice,
        gasLimit: Long?,
        nonce: Long?,
    ): String? {
        val fromAddress = Address(from)
        val toAddress = Address(to)
        val transactionData = buildErc1155SafeTransferFromTransactionData(
            Address(contractAddress),
            fromAddress,
            toAddress,
            tokenId.toBigInteger()
        )

        return signAndSendData(
            rpcUrl,
            signer,
            transactionBuilder,
            transactionData,
            gasPrice,
            gasLimit,
            nonce
        )
    }

    private suspend fun signAndSendData(
        rpcUrl: String,
        signer: Signer,
        transactionBuilder: TransactionBuilder,
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gasLimit: Long?,
        nonce: Long?
    ): String? {
        if(gasLimit != null && nonce != null) {
            val rawTransaction = rawTransaction(transactionData, gasPrice, gasLimit, nonce)
            val signature: Signature = signer.signature(rawTransaction)
            val transaction = transactionBuilder.transaction(rawTransaction, signature)
            val encoded = transactionBuilder.encode(rawTransaction, signature)
            return sendRawTransaction(rpcUrl, encoded)
        }
        return null
    }

    private fun etherTransferTransactionData(address: Address, value: BigInteger): TransactionData {
        return TransactionData(address, value, byteArrayOf())
    }

    private fun rawTransaction(
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gasLimit: Long,
        nonce: Long
    ): RawTransaction {
        return rawTransaction(
            address = transactionData.to ?: Address(""),
            value = transactionData.value,
            transactionInput = transactionData.input,
            gasPrice = gasPrice,
            gasLimit = gasLimit,
            nonce = nonce
        )
    }

    private fun rawTransaction(
        address: Address,
        value: BigInteger,
        transactionInput: ByteArray = byteArrayOf(),
        gasPrice: GasPrice,
        gasLimit: Long,
        nonce: Long
    ): RawTransaction {
        return RawTransaction(gasPrice, gasLimit, address, value, nonce, transactionInput)
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

    private fun buildSendNftLogEvents(
        fromAddress: String,
        to: String,
        tokenId: String,
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
                        name = "tokenId",
                        value = Transaction.LogEvent.Decoded.ParamValue.Primitive(tokenId)
                    ),
                )
            )
        )
    )

    fun buildTransactionData(
        nftCollection: NftCollection,
        fromAddress: String,
        toAddress: String
    ): TransactionData? {
        if (nftCollection.contractAddress.isEmpty()) return null
        if (nftCollection.nft.size != 1) return null

        val nft = nftCollection.nft.first()
        val contractAddress = nftCollection.contractAddress

        val tokenId = nft.tokenId.toBigInteger()
        val from = Address(fromAddress)
        val to = Address(toAddress)

        return when (nftCollection.type) {
            NftType.ERC721 -> {
                buildErc721SafeTransferFromTransactionData(
                    Address(contractAddress),
                    from,
                    to,
                    tokenId
                )
            }
            NftType.ERC1155 -> {
                buildErc1155SafeTransferFromTransactionData(
                    Address(contractAddress),
                    from,
                    to,
                    tokenId
                )
            }
        }
    }

    private fun buildErc721SafeTransferFromTransactionData(
        contractAddress: Address,
        from: Address,
        to: Address,
        tokenId: BigInteger
    ): TransactionData {
        return TransactionData(
            to = contractAddress,
            value = BigInteger.ZERO,
            Eip721SafeTransferFromMethod(from, to, tokenId).encodedABI()
        )
    }

    private fun buildErc1155SafeTransferFromTransactionData(
        contractAddress: Address,
        from: Address,
        to: Address,
        tokenId: BigInteger,
    ): TransactionData {
        return TransactionData(
            to = contractAddress,
            value = BigInteger.ZERO,
            Eip1155SafeTransferFromMethod(
                from,
                to,
                tokenId,
                BigInteger.ONE,
                byteArrayOf()
            ).encodedABI()
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

    /**
     * @return Transaction hash
     */
    private suspend fun sendRawTransaction(rpcUrl: String, signedTransaction: ByteArray): String {
        // TODO: Refactor to share this with SendTokenUseCase
        val response = sendRawTransactionUseCase.invoke(
            rpcUrl,
            currentId.getAndIncrement(),
            signedTransaction
        )
        val jsonRpcNodeResponse = try {
            // TODO: Upgrade to Result wrapper for more robust error handling
            parsingJson.decodeFromString(JsonRpcNodeResponse.serializer(), response)
        } catch (e: Exception) {
            return ""
        }
        return jsonRpcNodeResponse.result.orEmpty() // TODO: Check fail case
    }
}