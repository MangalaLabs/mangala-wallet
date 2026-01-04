package com.mangala.wallet.domain.provider.eosEVM.mapper

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.model.provider.covalenthq.GetPaginatedCovalenthqTransactionsForAddressResponse
import com.mangala.wallet.model.provider.eosEVM.GetPaginatedEosEvmTokenTransferForAddressResponse
import com.mangala.wallet.model.provider.eosEVM.GetPaginatedEosEvmTransactionsForAddressResponse
import com.mangala.wallet.utils.ext.toLong
import com.mangala.wallet.utils.secondsTimestampToMillisecondTimestamp
import com.mangala.wallet.utils.toBigDecimalOrNull
import commangalawalletdatabase.TransactionsEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

fun GetPaginatedEosEvmTransactionsForAddressResponse.Result.toTransaction(accountId: String, address: String): Transaction {
    return this.let {
        Transaction(
            blockHeight = it.blockNumber?.toInt() ?: 0,
            blockSignedAt = Instant.fromEpochMilliseconds(it.timeStamp?.toLongOrNull().secondsTimestampToMillisecondTimestamp()),
            feesPaid = calculateFeesPaidInWeiByGasPriceAndGasUsed(gasPrice = gasPrice, gasUsed = gasUsed),
            fromAddress = it.from ?: "",
            fromAddressLabel = "",
            gasMetadata = Transaction.GasMetadata(
                contractAddress = "",
                contractDecimals = 18,
                contractName = "EOS",
                contractTickerSymbol = "EOS",
                logoUrl = "",
            ),
            gasOffered = it.gas?.toLong() ?: 0,
            gasPrice = it.gasPrice?.toLong() ?: 0L,
            gasQuote = 0.0,
            gasQuoteRate = 0.0,
            gasSpent = it.gasUsed?.toLong() ?: 0,
            minerAddress = "",
            prettyGasQuote = "",
            prettyValueQuote = "",
            status = when {
//                TODO: Add more logic here to get the transaction status
                isError == "1" -> TransactionStatus.FAILED
                true -> TransactionStatus.SUCCESS
                else -> TransactionStatus.PENDING
            },
            toAddress = "",
            toAddressLabel = "",
            txHash = it.hash ?: "",
            txOffset = 0,
            value = it.value ?: "",
            valueQuote = 0.0,
            transactionType = getTransactionType(address),
            logEvents = emptyList(), // TODO: Map
            accountId = accountId,
        )
    }
}

fun GetPaginatedEosEvmTransactionsForAddressResponse.Result.toTransactionEntity(
    accountId: String,
    blockchainUid: String,
    address: String
): TransactionsEntity {
    return TransactionsEntity(
        blockHeight = blockNumber?.toLong() ?: 0L,
        blockSignedAt = timeStamp?.toLongOrNull().secondsTimestampToMillisecondTimestamp(),
        feesPaid = calculateFeesPaidInWeiByGasPriceAndGasUsed(gasPrice = gasPrice, gasUsed = gasUsed),
        fromAddress = from ?: "",
        fromAddressLabel = "",
        gasContractAddress = contractAddress ?: "",
        gasContractDecimals = 18,
        gasContractName = "EOS",
        gasContractTickerSymbol = "EOS",
        gasLogoUrl = "",
        gasOffered = gas?.toLong() ?: 0,
        gasPrice = gasPrice ?: "",
        gasQuote = "",
        gasQuoteRate = "",
        gasSpent = gasUsed ?: "",
        minerAddress = "",
        prettyGasQuote = "",
        prettyValueQuote = "",
        status = if (isError == "0") TransactionStatus.SUCCESS.name else TransactionStatus.FAILED.name,
        toAddress = to ?: "",
        toAddressLabel = "",
        transactionHash = hash ?: "",
        txOffset = 0,
        value_ = value ?: "",
        valueQuote = "",
        accountId = accountId,
        transactionType = getTransactionType(address).name,
        logEvents = null,
        blockchain_uid = blockchainUid,
        is_nft_transaction = false.toLong(),
        nft_transfers = null,
        erc20_transfers = null,
        native_transfers = null
    )
}

fun GetPaginatedEosEvmTokenTransferForAddressResponse.Result.toTransactionEntity(
    accountId: String,
    address: String,
    blockchainUid: String
): TransactionsEntity {
    val logEvents = listOf(
        GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.LogEvent(
            senderContractTickerSymbol = tokenSymbol,
            senderContractDecimals = tokenDecimal?.toIntOrNull() ?: 18,
            decoded = GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.LogEvent.Decoded(
                "Transfer", listOf(
                    GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.LogEvent.Decoded.Param(
                        name = "from",
                        value = JsonPrimitive(from)
                    ),
                    GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.LogEvent.Decoded.Param(
                        name = "to",
                        value = JsonPrimitive(to)
                    ),
                    GetPaginatedCovalenthqTransactionsForAddressResponse.Data.Item.LogEvent.Decoded.Param(
                        name = "value",
                        value = JsonPrimitive(value)
                    ),
                )
            )
        )
    )
    return TransactionsEntity(
        blockHeight = blockNumber?.toLongOrNull() ?: 0L,
        blockSignedAt = timeStamp?.toLongOrNull().secondsTimestampToMillisecondTimestamp(),
        feesPaid = calculateFeesPaidInWeiByGasPriceAndGasUsed(gasPrice = gasPrice, gasUsed = gasUsed),
        fromAddress = from ?: "",
        fromAddressLabel = "",
        gasContractAddress = contractAddress ?: "",
        gasContractDecimals = 18,
        gasContractName = "EOS",
        gasContractTickerSymbol = "EOS",
        gasLogoUrl = "",
        gasOffered = gas?.toLongOrNull() ?: 0,
        gasPrice = gasPrice ?: "",
        gasQuote = "",
        gasQuoteRate = "",
        gasSpent = gasUsed ?: "",
        minerAddress = "",
        prettyGasQuote = "",
        prettyValueQuote = "",
        status = TransactionStatus.SUCCESS.name,
        toAddress = to ?: "",
        toAddressLabel = "",
        transactionHash = hash ?: "",
        txOffset = 0,
        value_ = value ?: "",
        valueQuote = "",
        accountId = accountId,
        transactionType = if (to?.equals(address) == true) TransactionType.RECEIVE.name else TransactionType.SEND.name,
        logEvents = Json.encodeToString(logEvents),
        blockchain_uid = blockchainUid,
        is_nft_transaction = false.toLong(),
        nft_transfers = null,
        erc20_transfers = null,
        native_transfers = null
    )
}

fun GetPaginatedEosEvmTransactionsForAddressResponse.Result.getTransactionType(address: String): TransactionType =
        when {
            to.isNullOrBlank() -> TransactionType.CONTRACT_DEPLOYMENT
            to == address -> TransactionType.RECEIVE
            input != "0x" -> TransactionType.CONTRACT_CALL
            value?.isNotBlank() == true -> TransactionType.SEND
            else -> TransactionType.RECEIVE
        }

private fun calculateFeesPaidInWeiByGasPriceAndGasUsed(gasPrice: String?, gasUsed: String?): String {
    val gasPriceInBigDecimal = gasPrice?.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val gasUsedInBigDecimal = gasUsed?.toBigDecimalOrNull() ?: BigDecimal.ZERO
    return (gasPriceInBigDecimal * gasUsedInBigDecimal).toString()
}
