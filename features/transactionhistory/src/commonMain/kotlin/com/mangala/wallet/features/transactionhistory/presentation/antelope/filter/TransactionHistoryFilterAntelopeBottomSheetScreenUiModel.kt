package com.mangala.wallet.features.transactionhistory.presentation.antelope.filter

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TransactionHistoryFilterAntelopeBottomSheetScreenUiModel(
    val selectedStartDate: Instant? = null,
    val selectedEndDate: Instant? = null,
    val isDatePickerDialogVisible: Boolean = false,
    val isPickingStartDate: Boolean? = null,
    val isPickingEndDate: Boolean? = null
) {
    private val currentEpochMilliseconds: Long = Clock.System.now().toEpochMilliseconds()

    val initialDatePickerTime: Long
        get() = if (isPickingStartDate == true) selectedStartDate?.toEpochMilliseconds() ?: currentEpochMilliseconds
        else selectedStartDate?.toEpochMilliseconds() ?: currentEpochMilliseconds

    val initialDatePickerTimeEnd: Long
        get() = if (isPickingEndDate == true) selectedEndDate?.toEpochMilliseconds() ?: currentEpochMilliseconds
        else selectedEndDate?.toEpochMilliseconds() ?: currentEpochMilliseconds
}