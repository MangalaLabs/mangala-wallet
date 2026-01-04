package com.mangala.wallet.features.transactionhistory.presentation.evm.info

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.utils.FormatStyle
import com.mangala.wallet.utils.ext.formatFiat
import com.mangala.wallet.utils.formatDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TransactionInfoUi(
    val status: TransactionStatus,
    val type: TransactionType,
    val amount: BigDecimal,
    val symbol: String,
    val fiatValue: BigDecimal,
    val fiatCurrencySymbol: String, // TODO: Handle currency symbol
    val network: String,
    val transactionId: String,
    val address: String,
    val gasFee: BigDecimal,
    val gasFeeFiatValue: BigDecimal,
    val gasFeeSymbol: String,
    val date: Instant,
    val blockExplorerUrl: String
) {
    val formattedTransactionValue: String get() {
        val sign = when {
            type == TransactionType.SEND && amount != BigDecimal.ZERO -> "-"
            else -> ""
        }
        return "$sign${amount.toPlainString()} $symbol" // TODO: Format large value
    }
    val formattedFiatValue: String = "~ ${fiatValue.formatFiat(fiatCurrencySymbol)}" // TODO: Format large value
    val formattedTransactionId = transactionId.take(8) + "..." + transactionId.takeLast(8)
    val formattedAddress = Address(address).eip55.take(8) + "..." + Address(address).eip55.takeLast(8)
    val formattedTime = date
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .formatDateTime(
            TimeZone.currentSystemDefault(),
            dateStyle = FormatStyle.SHORT,
            timeStyle = FormatStyle.MEDIUM
        )
    val formattedGasFeeValue: String = "${gasFee.toPlainString()} $gasFeeSymbol (${gasFeeFiatValue.formatFiat(fiatCurrencySymbol)})"
}