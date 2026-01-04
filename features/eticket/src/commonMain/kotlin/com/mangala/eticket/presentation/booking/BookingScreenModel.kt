package com.mangala.eticket.presentation.booking

import com.mangala.eticket.presentation.event.EventScreenUiModel
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BookingScreenModel(
    private val ticketTypes: List<EventScreenUiModel.TicketTypeUiModel>,
) : BaseScreenModel() {
    private val _uiState: MutableStateFlow<BookingUiState> =
        MutableStateFlow(BookingUiState.Loading)
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        updateUiState()
    }

    fun updateTicketQuantity(ticketType: EventScreenUiModel.TicketTypeUiModel, newQuantity: Int) {
        val currentUiModel = (_uiState.value as? BookingUiState.Success)?.data
        val updatedSelections = currentUiModel?.ticketSelections.orEmpty().toMutableList().apply {
            removeAll { it.name == ticketType.name }
            if (newQuantity > 0) {
                add(TicketSelection(ticketType.id, ticketType.name, newQuantity, ticketType.price))
            }
        }
        val updatedTotalPrice = updatedSelections.sumOf { it.quantity * it.price }
        updateUiState(updatedSelections, updatedTotalPrice)
    }

    private fun updateUiState(
        ticketSelections: List<TicketSelection> = emptyList(),
        totalMoney: Double = 0.0,
    ) {
        _uiState.value = BookingUiState.Success(
            BookingUiModel(
                ticketTypes = ticketTypes,
                ticketSelections = ticketSelections,
                totalMoney = totalMoney,
                isButtonEnabled = ticketSelections.isNotEmpty(),
                totalMoneyStr = "Total: $totalMoney"
            )
        )
    }
}