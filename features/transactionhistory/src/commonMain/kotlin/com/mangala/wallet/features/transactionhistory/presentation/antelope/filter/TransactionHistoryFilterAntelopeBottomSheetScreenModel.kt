package com.mangala.wallet.features.transactionhistory.presentation.antelope.filter

import cafe.adriel.voyager.core.model.ScreenModel
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.convertUtcMillisToLocal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class TransactionHistoryFilterAntelopeBottomSheetScreenModel(
    startDateFilter: Instant?,
    endDateFilter: Instant?,
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(
        TransactionHistoryFilterAntelopeBottomSheetScreenUiModel(
            selectedStartDate = startDateFilter,
            selectedEndDate = endDateFilter,
        )
    )
    val uiState get() = _uiState.asStateFlow()

    fun checkDateValidator(): (Long) -> Boolean {
        val startDateValidator: (Long) -> Boolean = { selectedDate ->
            val endDate = _uiState.value.selectedEndDate?.toEpochMilliseconds()
            endDate?.let {
                selectedDate <= it
            } ?: true
        }

        val endDateValidator: (Long) -> Boolean = { selectedDate ->
            val startDate = _uiState.value.selectedStartDate?.toEpochMilliseconds()
            startDate?.let {
                selectedDate >= it
            } ?: true
        }

        return if (_uiState.value.isPickingStartDate == true) {
            startDateValidator
        } else {
            endDateValidator
        }
    }


    fun onConfirmPickDate(selectedTime: Long) {
        _uiState.update { currentState ->
            val timeZone = TimeZone.currentSystemDefault()
            val newState = if (currentState.isPickingStartDate == true) {
                handleStartDateSelection(selectedTime, currentState, timeZone)
            } else {
                handleEndDateSelection(selectedTime, currentState, timeZone)
            }
            newState
        }
    }

    private fun handleStartDateSelection(
        selectedTime: Long,
        currentState: TransactionHistoryFilterAntelopeBottomSheetScreenUiModel,
        timeZone: TimeZone
    ): TransactionHistoryFilterAntelopeBottomSheetScreenUiModel {
        val selectedTimeInstant =
            convertUtcMillisToLocal(
                epochMillis = selectedTime,
                localTimeHour = 0,
                localTimeMinute = 0,
                localTimeSecond = 0,
                localTimeNanoSeconds = 0,
                )
        val endDateMilliseconds = currentState.selectedEndDate?.toEpochMilliseconds()
        return if (endDateMilliseconds != null && selectedTime > endDateMilliseconds) {
            currentState.copy(
                isDatePickerDialogVisible = false,
                isPickingStartDate = false,
                isPickingEndDate = false,
                selectedStartDate = null,
            )
        } else {
            currentState.copy(
                isDatePickerDialogVisible = false,
                isPickingStartDate = false,
                isPickingEndDate = false,
                selectedStartDate = selectedTimeInstant.toInstant(timeZone)
            )
        }
    }

    private fun handleEndDateSelection(
        selectedTime: Long,
        currentState: TransactionHistoryFilterAntelopeBottomSheetScreenUiModel,
        timeZone: TimeZone
    ): TransactionHistoryFilterAntelopeBottomSheetScreenUiModel {
        val selectedTimeInstant = convertUtcMillisToLocal(
            epochMillis = selectedTime,
            localTimeHour = 23,
            localTimeMinute = 59,
            localTimeSecond = 59,
            localTimeNanoSeconds = 0,
            )
        val startDateMilliseconds = currentState.selectedStartDate?.toEpochMilliseconds()
        return if (startDateMilliseconds != null && selectedTime < startDateMilliseconds) {
            currentState.copy(
                isDatePickerDialogVisible = false,
                isPickingStartDate = false,
                isPickingEndDate = false,
                selectedEndDate = null,
            )
        } else {
            currentState.copy(
                isDatePickerDialogVisible = false,
                isPickingStartDate = false,
                isPickingEndDate = false,
                selectedEndDate = selectedTimeInstant.toInstant(timeZone)
            )
        }
    }

    fun onClickPickStartDate() {
        _uiState.update {
            it.copy(
                isDatePickerDialogVisible = true,
                isPickingStartDate = true,
                isPickingEndDate = false
            )
        }
    }

    fun onClickPickEndDate() {
        _uiState.update {
            it.copy(
                isDatePickerDialogVisible = true,
                isPickingEndDate = true,
                isPickingStartDate = false
            )
        }
    }

    fun onDismissDatePickerDialog() {
        _uiState.update {
            it.copy(
                isDatePickerDialogVisible = false,
                isPickingStartDate = false,
                isPickingEndDate = false,
            )
        }
    }

    fun onResetFilters() {
        _uiState.update {
            it.copy(
                selectedStartDate = null,
                selectedEndDate = null,
            )
        }
    }
}