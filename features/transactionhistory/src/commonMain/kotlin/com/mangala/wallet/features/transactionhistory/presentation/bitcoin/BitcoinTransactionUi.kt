package com.mangala.wallet.features.transactionhistory.presentation.bitcoin

import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction
import com.mangala.wallet.utils.ext.formatToHourMinute
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * UI model for Bitcoin transactions in the transaction history screen
 */
data class BitcoinTransactionUi(
    val transactionType: TransactionType,
    val address: String,
    val txid: String,
    val time: Instant,
    val amount: String,
    val fee: String,
    val confirmed: Boolean,
    val rawTransaction: BitcoinTransaction
) {
    val formattedDate: String
        get() = time.toLocalDateTime(TimeZone.currentSystemDefault()).formatToHourMinute()

    // Format Bitcoin address for display (show first 8 and last 8 characters)
    val formattedAddress: String
        get() = if (address.length > 16) {
            address.take(8) + "..." + address.takeLast(8)
        } else {
            address
        }
}