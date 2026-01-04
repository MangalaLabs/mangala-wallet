package com.mangala.eticket.presentation.booking


sealed class ConfirmationUiState {
    object Loading : ConfirmationUiState()
    data class Success(val data: ConfirmationUiModel) : ConfirmationUiState()
    data class Error(val message: String) : ConfirmationUiState()
}

data class ConfirmationUiModel(
    val selectedTickets: List<TicketSelection>,
    val totalAmount: Double,
    val isSendAnotherWallet: Boolean,
    val walletAnotherAddress: String,
)

data class TicketSelection(val id: String, val name: String, val quantity: Int, val price: Double)