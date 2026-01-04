package com.mangala.eticket.presentation.booking

import com.mangala.eticket.domain.usecases.ticket.TicketPreparePurchaseUseCase
import com.mangala.eticket.domain.usecases.ticket.TicketPurchaseUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfirmationScreenModel(
    selectedTickets: List<TicketSelection>,
    totalAmount: Double,
    isSendAnotherWallet: Boolean,
    walletAnotherAddress: String,
    private val ticketPurchaseUseCase: TicketPurchaseUseCase,
    private val ticketPreparePurchaseUseCase: TicketPreparePurchaseUseCase,
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<ConfirmationUiState> =
        MutableStateFlow(ConfirmationUiState.Loading)
    val uiState: StateFlow<ConfirmationUiState> get() = _uiState.asStateFlow()


    init {
        loadData(selectedTickets, totalAmount, isSendAnotherWallet, walletAnotherAddress)
    }

    private fun loadData(
        selectedTickets: List<TicketSelection>,
        totalAmount: Double,
        isSendAnotherWallet: Boolean,
        walletAnotherAddress: String,
    ) {
        _uiState.value = ConfirmationUiState.Success(
            ConfirmationUiModel(
                selectedTickets = selectedTickets,
                totalAmount = totalAmount,
                isSendAnotherWallet = isSendAnotherWallet,
                walletAnotherAddress = walletAnotherAddress
            )
        )
    }

    fun onPaymentCompleted() {
    }
}