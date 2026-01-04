package com.mangala.wallet.features.transactionhistory.presentation.evm.filter

import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TransactionHistoryFilterBottomSheetScreenUiModel(
    val selectedType: TransactionType? = null,
    val selectedStatus: TransactionStatus? = null,
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