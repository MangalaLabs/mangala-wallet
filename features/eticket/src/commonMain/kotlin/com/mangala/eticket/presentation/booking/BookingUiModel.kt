package com.mangala.eticket.presentation.booking

import com.mangala.eticket.presentation.event.EventScreenUiModel


sealed class BookingUiState {
    object Loading : BookingUiState()
    data class Success(
        val data: BookingUiModel,
    ) : BookingUiState()

    data class Error(val message: String) : ConfirmationUiState()
}

data class BookingUiModel(
    val ticketTypes: List<EventScreenUiModel.TicketTypeUiModel>,
    val ticketSelections: List<TicketSelection> = listOf(),
    val totalMoney: Double = 0.0,
    val isButtonEnabled: Boolean,
    val totalMoneyStr: String,
)