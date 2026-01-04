package com.mangala.eticket.domain.usecases.ticket

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.ticket.TicketPreparePurchaseRequest
import com.mangala.eticket.data.model.ticket.TicketPreparePurchaseResponse
import com.mangala.eticket.domain.repository.ticket.TicketPurchaseRepository
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

class TicketPreparePurchaseUseCase(private val repository: TicketPurchaseRepository) {
    suspend operator fun invoke(
        eventId: String,
        body: TicketPreparePurchaseRequest,
    ): ApiResponse<ETicketResponse<TicketPreparePurchaseResponse>, CustomError> {
        return repository.preparePurchaseTicket(eventId, body)
    }
}