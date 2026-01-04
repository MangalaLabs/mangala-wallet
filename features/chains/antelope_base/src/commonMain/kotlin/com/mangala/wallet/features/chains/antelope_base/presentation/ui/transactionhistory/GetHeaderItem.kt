package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import com.mangala.wallet.utils.ext.parseUtcDateTimeToInstantOrNull
import com.mangala.wallet.utils.formatDate
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

fun String.getHeaderItem(): TransactionHistoryItemAntelope.HeaderItem {
    val isoString = if (this.endsWith("Z") || this.contains("+")) this else "${this}Z"
    val dateTime = Instant.parse(isoString)

    val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val yesterday = currentDate.minus(1, DateTimeUnit.DAY)

    val localDateTime = dateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val date = localDateTime.date

    return when (date) {
        currentDate -> TransactionHistoryItemAntelope.HeaderItem.Today
        yesterday -> TransactionHistoryItemAntelope.HeaderItem.Yesterday
        else -> TransactionHistoryItemAntelope.HeaderItem.Date(
            localDateTime.formatDate(
                TimeZone.currentSystemDefault()
            )
        )
    }
}