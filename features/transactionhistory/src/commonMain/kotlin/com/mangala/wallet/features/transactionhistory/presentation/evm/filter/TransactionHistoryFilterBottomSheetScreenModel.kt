package com.mangala.wallet.features.transactionhistory.presentation.evm.filter

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.convertUtcMillisToLocal
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.koin.core.component.KoinComponent

class TransactionHistoryFilterBottomSheetScreenModel(
    typeFilter: TransactionType?,
    statusFilter: TransactionStatus?,
    startDateFilter: Instant?,
    endDateFilter: Instant?,
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(
        TransactionHistoryFilterBottomSheetScreenUiModel(
            selectedType = typeFilter,
            selectedStatus = statusFilter,
            selectedStartDate = startDateFilter,
            selectedEndDate = endDateFilter,
        )
    )
    val uiState get() = _uiState.asStateFlow()

    private val _startDate : MutableStateFlow<Long?> = MutableStateFlow(_uiState.value.initialDatePickerTime)
    val startDate get() = _startDate.asStateFlow()
    private val _endDate : MutableStateFlow<Long?> = MutableStateFlow(_uiState.value.initialDatePickerTimeEnd)
    val endDate get() = _endDate.asStateFlow()

    fun updateStartDate(date: Long) {
        _startDate.value = date
    }

    fun updateEndDate(date: Long) {
        _endDate.value = date
    }

    fun onTypeSelected(type: TransactionType?) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onStatusSelected(status: TransactionStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
    }

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
        currentState: TransactionHistoryFilterBottomSheetScreenUiModel,
        timeZone: TimeZone
    ): TransactionHistoryFilterBottomSheetScreenUiModel {
        val selectedTimeInstant =
            convertUtcMillisToLocal(
                epochMillis = selectedTime,
                localTimeHour = 0,
                localTimeMinute = 0
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
        currentState: TransactionHistoryFilterBottomSheetScreenUiModel,
        timeZone: TimeZone
    ): TransactionHistoryFilterBottomSheetScreenUiModel {
        val selectedTimeInstant = convertUtcMillisToLocal(
            epochMillis = selectedTime,
            localTimeHour = 23,
            localTimeMinute = 59,
            localTimeSecond = 59,
            localTimeNanoSeconds = 999999999
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
                selectedType = null,
                selectedStatus = null,
                selectedStartDate = null,
                selectedEndDate = null,
            )
        }
    }
}