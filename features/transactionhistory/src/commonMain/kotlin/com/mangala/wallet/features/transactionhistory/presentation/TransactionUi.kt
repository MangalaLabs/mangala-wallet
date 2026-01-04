package com.mangala.wallet.features.transactionhistory.presentation

import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.utils.ext.formatToHourMinute
import kotlinx.datetime.LocalDateTime

data class TransactionUi(
    val transactionType: TransactionType,
    val address: String,
    val time: LocalDateTime,
    val amount: String, // TODO: Split this up into amount string and token symbol
    val txHash: String, // TODO: Remove after debug complete
    val transaction: Transaction // TODO: Remove after debug complete
) {
    val formattedDate: String
        get() = time.formatToHourMinute()

    val formattedAddress = Address(address).eip55.take(8) + "..." + Address(address).eip55.takeLast(8)
}