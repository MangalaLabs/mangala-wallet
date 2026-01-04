package com.mangala.eticket.domain.usecases.ticket

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.ticket.PurchasingTicketRequest
import com.mangala.eticket.data.model.ticket.TicketPurchaseResponse
import com.mangala.eticket.domain.repository.ticket.TicketPurchaseRepository
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

class TicketPurchaseUseCase(private val repository: TicketPurchaseRepository) {
    suspend operator fun invoke(
        eventId: String,
        ticketId: String,
        purchasingTicketRequest: PurchasingTicketRequest,
    ): ApiResponse<ETicketResponse<TicketPurchaseResponse>, CustomError> {
        return repository.purchaseTicket(
            eventId,
            ticketId,
            purchasingTicketRequest
        )
    }
}