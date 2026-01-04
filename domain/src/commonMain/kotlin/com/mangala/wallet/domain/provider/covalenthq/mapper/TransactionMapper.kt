package com.mangala.wallet.domain.provider.covalenthq.mapper

import com.mangala.wallet.domain.provider.moralis.mapper.toErc20Transfer
import com.mangala.wallet.domain.provider.moralis.mapper.toNativeTransfer
import com.mangala.wallet.domain.provider.moralis.mapper.toNftTransfer
import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.model.provider.covalenthq.GetPaginatedCovalenthqTransactionsForAddressResponse
import com.mangala.wallet.model.provider.moralis.Erc20Transfer
import com.mangala.wallet.model.provider.moralis.NativeTransfer
import com.mangala.wallet.model.provider.moralis.NftTransfer
import com.mangala.wallet.utils.ext.toDoubleOrZero
import com.mangala.wallet.utils.ext.toInstant
import com.mangala.wallet.utils.ext.toLong
import com.mangala.wallet.utils.ext.toLongOrZero
import commangalawalletdatabase.TransactionsEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
@param [address]: address of the account querying the transaction info. Used to determine transaction type
 */
fun GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.toTransaction(address: String, accountId: String): Transaction {
    return this.let {
        Transaction(
            blockHeight = it.blockHeight ?: 0,
            blockSignedAt = it.blockSignedAt.toInstant(),
            feesPaid = it.feesPaid ?: "",
            fromAddress = it.fromAddress ?: "",
            fromAddressLabel = it.fromAddressLabel ?: "",
            gasMetadata = Transaction.GasMetadata(
                contractAddress = it.gasMetadata?.contractAddress ?: "",
                contractDecimals = it.gasMetadata?.contractDecimals ?: 0,
                contractName = it.gasMetadata?.contractName ?: "",
                contractTickerSymbol = it.gasMetadata?.contractTickerSymbol ?: "",
                logoUrl = it.gasMetadata?.logoUrl ?: "",
            ),
            gasOffered = it.gasOffered ?: 0,
            gasPrice = it.gasPrice ?: 0L,
            gasQuote = it.gasQuote ?: 0.0,
            gasQuoteRate = it.gasQuoteRate ?: 0.0,
            gasSpent = it.gasSpent ?: 0,
            minerAddress = it.minerAddress ?: "",
            prettyGasQuote = it.prettyGasQuote ?: "",
            prettyValueQuote = it.prettyValueQuote ?: "",
            status = when (it.successful) {
                true -> TransactionStatus.SUCCESS
                false -> TransactionStatus.FAILED
                else -> TransactionStatus.PENDING
            },
            toAddress = it.toAddress ?: "",
            toAddressLabel = it.toAddressLabel ?: "",
            txHash = it.txHash ?: "",
            txOffset = it.txOffset ?: 0,
            value = it.value ?: "",
            valueQuote = it.valueQuote ?: 0.0,
            transactionType = getTransactionType(address),
            logEvents = emptyList(), // TODO: Map
            accountId = accountId,
        )
    }
}

/**
@param [address]: address of the account querying the transaction info. Used to determine transaction type
 */
fun GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.toTransactionEntity(
    accountId: String,
    address: String,
    blockchainUid: String
): TransactionsEntity {
    return TransactionsEntity(
        blockHeight = blockHeight?.toLong() ?: 0L,
        blockSignedAt = if (blockSignedAt != null && blockSignedAt!!.isNotBlank()) Instant.parse(blockSignedAt!!).toEpochMilliseconds() else 0L,
        feesPaid = feesPaid ?: "",
        fromAddress = fromAddress ?: "",
        fromAddressLabel = fromAddressLabel ?: "",
        gasContractAddress = gasMetadata?.contractAddress ?: "",
        gasContractDecimals = gasMetadata?.contractDecimals?.toLong(),
        gasContractName = gasMetadata?.contractName ?: "",
        gasContractTickerSymbol = gasMetadata?.contractTickerSymbol ?: "",
        gasLogoUrl = gasMetadata?.logoUrl ?: "",
        gasOffered = gasOffered ?: 0,
        gasPrice = gasPrice?.toString() ?: "",
        gasQuote = gasQuote?.toString() ?: "",
        gasQuoteRate = gasQuoteRate?.toString() ?: "",
        gasSpent = gasSpent?.toString() ?: "",
        minerAddress = minerAddress ?: "",
        prettyGasQuote = prettyGasQuote ?: "",
        prettyValueQuote = prettyValueQuote ?: "",
        status = if (successful == true) TransactionStatus.SUCCESS.name else TransactionStatus.FAILED.name,
        toAddress = toAddress ?: "",
        toAddressLabel = toAddressLabel ?: "",
        transactionHash = txHash ?: "",
        txOffset = txOffset?.toLong() ?: 0,
        value_ = value ?: "",
        valueQuote = valueQuote?.toString() ?: "",
        accountId = accountId,
        transactionType = getTransactionType(address).name,
        logEvents = logEvents?.let { Json.encodeToString(logEvents) },
        blockchain_uid = blockchainUid,
        is_nft_transaction = false.toLong(),
        nft_transfers = null,
        erc20_transfers = null,
        native_transfers = null
    )
}

fun TransactionsEntity.entityToTransaction(): Transaction {
    return Transaction(
        blockHeight = blockHeight.toInt(),
        blockSignedAt = Instant.fromEpochMilliseconds(blockSignedAt),
        feesPaid = feesPaid,
        fromAddress = fromAddress,
        fromAddressLabel = fromAddressLabel,
        gasMetadata = Transaction.GasMetadata(
            contractAddress = gasContractAddress,
            contractDecimals = gasContractDecimals?.toInt(),
            contractName = gasContractName,
            contractTickerSymbol = gasContractTickerSymbol,
            logoUrl = gasLogoUrl,
        ),
        gasOffered = gasOffered,
        gasPrice = gasPrice.toLongOrZero(),
        gasQuote = gasQuote.toDoubleOrZero(),
        gasQuoteRate = gasQuoteRate.toDoubleOrZero(),
        gasSpent = gasSpent.toLongOrZero(),
        minerAddress = minerAddress,
        prettyGasQuote = prettyGasQuote,
        prettyValueQuote = prettyValueQuote,
        status = TransactionStatus.valueOf(status),
        toAddress = toAddress,
        toAddressLabel = toAddressLabel,
        txHash = transactionHash,
        txOffset = txOffset.toInt(),
        value = value_,
        valueQuote = valueQuote.toDoubleOrZero(),
        transactionType = TransactionType.valueOf(transactionType),
        logEvents = logEvents?.let {
            Json.decodeFromString<List<GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.LogEvent?>?>(logEvents.orEmpty())?.map {
                Transaction.LogEvent(
                    blockHeight = it?.blockHeight,
                    blockSignedAt = it?.blockSignedAt,
                    decoded = Transaction.LogEvent.Decoded(
                        name = it?.decoded?.name,
                        params = it?.decoded?.params?.map {
                            Transaction.LogEvent.Decoded.Param(
                                decoded = it?.decoded,
                                indexed = it?.indexed,
                                name = it?.name,
                                type = it?.type,
                                value = when (it?.value) {
                                    is JsonPrimitive -> Transaction.LogEvent.Decoded.ParamValue.Primitive(
                                        it.value!!.jsonPrimitive.content
                                    )

                                    is JsonArray -> {
                                        Transaction.LogEvent.Decoded.ParamValue.Array(
                                            (it.value as? JsonArray)?.map {
                                                Transaction.LogEvent.Decoded.ParamValue.Array.ArrayElement(
                                                    value = it.jsonObject["value"]?.jsonPrimitive?.content
                                                        ?: "",
                                                    bitSize = it.jsonObject["bitSize"]?.jsonPrimitive?.content?.toInt()
                                                        ?: 0,
                                                    typeAsString = it.jsonObject["typeAsString"]?.jsonPrimitive?.content
                                                        ?: ""
                                                )
                                            } ?: emptyList()
                                        )
                                    }

                                    is JsonObject -> Transaction.LogEvent.Decoded.ParamValue.Unknown(
                                        it.toString()
                                    )

                                    null -> null
                                }
                            )
                        },
                        signature = it?.decoded?.signature
                    ),
                    logOffset = it?.logOffset,
                    rawLogData = it?.rawLogData,
                    rawLogTopics = it?.rawLogTopics,
                    senderAddress = it?.senderAddress,
                    senderAddressLabel = it?.senderAddressLabel,
                    senderContractDecimals = it?.senderContractDecimals,
                    senderContractTickerSymbol = it?.senderContractTickerSymbol,
                    senderLogoUrl = it?.senderLogoUrl,
                    senderName = it?.senderName,
                    txHash = it?.txHash,
                    txOffset = it?.txOffset
                ) // TODO: Extract map function
            } // Since the generated class from SQLDelight doesn't have support for serialization, we need to map back to remote DTO and serialize from there
        },
        accountId = accountId,
        isNftTransaction = is_nft_transaction == 1L,
        erc20Transfers = erc20_transfers?.let {
            Json.decodeFromString<List<Erc20Transfer>>(it).map { it.toErc20Transfer() }
        },
        nativeTransfers = native_transfers?.let {
            Json.decodeFromString<List<NativeTransfer>>(it).map { it.toNativeTransfer() }
        },
        nftTransfers = nft_transfers?.let {
            Json.decodeFromString<List<NftTransfer>>(it).map { it.toNftTransfer() }
        },
    )
}

@OptIn(ExperimentalSerializationApi::class)
fun Transaction.toTransactionEntity(accountId: String, blockchainUid: String) = TransactionsEntity(
    blockHeight = blockHeight.toLong(),
    blockSignedAt = blockSignedAt.toEpochMilliseconds(),
    feesPaid = feesPaid,
    fromAddress = fromAddress,
    fromAddressLabel = fromAddressLabel,
    gasContractAddress = gasMetadata.contractAddress,
    gasContractDecimals = gasMetadata.contractDecimals?.toLong(),
    gasContractName = gasMetadata.contractName,
    gasContractTickerSymbol = gasMetadata.contractTickerSymbol,
    gasLogoUrl = gasMetadata.logoUrl,
    gasOffered = gasOffered,
    gasPrice = gasPrice.toString(),
    gasQuote = gasQuote.toString(),
    gasQuoteRate = gasQuoteRate.toString(),
    gasSpent = gasSpent.toString(),
    minerAddress = minerAddress,
    prettyGasQuote = prettyGasQuote,
    prettyValueQuote = prettyValueQuote,
    status = status.name,
    toAddress = toAddress,
    toAddressLabel = toAddressLabel,
    transactionHash = txHash,
    txOffset = txOffset.toLong(),
    value_ = value,
    valueQuote = valueQuote.toString(),
    accountId = accountId,
    transactionType = transactionType.name,
    logEvents = logEvents?.let {
        val logEvents = it.map {
            GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.LogEvent(
                blockHeight = it.blockHeight,
                blockSignedAt = it.blockSignedAt,
                decoded = GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.LogEvent.Decoded(
                    name = it.decoded?.name,
                    params = it.decoded?.params?.map {
                        GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.LogEvent.Decoded.Param(
                            decoded = it?.decoded,
                            indexed = it?.indexed,
                            name = it?.name,
                            type = it?.type,
                            value = when (it?.value) {
                                is Transaction.LogEvent.Decoded.ParamValue.Primitive -> JsonPrimitive(
                                    it.value.value
                                )

                                is Transaction.LogEvent.Decoded.ParamValue.Array -> buildJsonArray {
                                    it.value.values.forEach {
                                        add(
                                            JsonObject(
                                                mapOf(
                                                    "value" to JsonPrimitive(it.value),
                                                    "bitSize" to JsonPrimitive(it.bitSize),
                                                    "typeAsString" to JsonPrimitive(it.typeAsString)
                                                )
                                            )
                                        )
                                    }
                                }

                                else -> JsonPrimitive(it?.value.toString())
                            }
                        )
                    },
                    signature = it.decoded?.signature
                ),
                logOffset = it.logOffset,
                rawLogData = it.rawLogData,
                rawLogTopics = it.rawLogTopics,
                senderAddress = it.senderAddress,
                senderAddressLabel = it.senderAddressLabel,
                senderContractDecimals = it.senderContractDecimals,
                senderContractTickerSymbol = it.senderContractTickerSymbol,
                senderLogoUrl = it.senderLogoUrl,
                senderName = it.senderName,
                txHash = it.txHash,
                txOffset = it.txOffset
            ) // TODO: Extract map function
        } // Since domain model doesn't have support for serialization, we need to map back to remote DTO and serialize from there

        Json.encodeToString(logEvents)
    },
    blockchain_uid = blockchainUid,
    is_nft_transaction = isNftTransaction.toLong(),
    nft_transfers = Json.encodeToString(nftTransfers?.map {
        NftTransfer(
            tokenId = it.tokenSymbol,
            amount = it.amountFormatted,
            direction = it.direction
        )
    }),
    erc20_transfers = Json.encodeToString(erc20Transfers?.map {
        Erc20Transfer(
            tokenName = it.tokenName,
            tokenSymbol = it.tokenSymbol,
            direction = it.direction,
            valueFormatted = it.amountFormatted
        )
    }),
    native_transfers = Json.encodeToString(nativeTransfers?.map {
        NativeTransfer(
            tokenSymbol = it.tokenSymbol,
            direction = it.direction,
            valueFormatted = it.amountFormatted
        )
    })
)

fun GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.getTransactionType(address: String): TransactionType =
    if (address == fromAddress) {
        val eventNames = logEvents?.map { it?.decoded?.name }

        if (eventNames?.contains("Swap") == true) {
            TransactionType.SWAP
        } else if (toAddress.isNullOrBlank()) {
            TransactionType.CONTRACT_DEPLOYMENT
        } else if (eventNames?.isEmpty() == true || eventNames?.lastOrNull()?.contains("Transfer") == true) {
            TransactionType.SEND
        } else {
            TransactionType.CONTRACT_CALL
        }
    } else {
        TransactionType.RECEIVE
    }