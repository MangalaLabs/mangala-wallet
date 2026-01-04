package com.mangala.eticket.domain.repository.ticket

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.ticket.PurchasingTicketRequest
import com.mangala.eticket.data.model.ticket.TicketPreparePurchaseRequest
import com.mangala.eticket.data.model.ticket.TicketPreparePurchaseResponse
import com.mangala.eticket.data.model.ticket.TicketPurchaseResponse
import com.mangala.eticket.data.remote.TicketPurchaseDataSource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

class TicketPurchaseRepositoryImpl(
    private val dataSource: TicketPurchaseDataSource,
) : TicketPurchaseRepository {

    override suspend fun preparePurchaseTicket(
        id: String,
        body: TicketPreparePurchaseRequest,
    ): ApiResponse<ETicketResponse<TicketPreparePurchaseResponse>, CustomError> {
        return dataSource.preparePurchaseTicket(id, body)
    }

    override suspend fun purchaseTicket(
        eventId: String,
        ticketId: String,
        purchasingTicketRequest: PurchasingTicketRequest,
    ): ApiResponse<ETicketResponse<TicketPurchaseResponse>, CustomError> {
        return dataSource.purchaseTicket(eventId, ticketId, purchasingTicketRequest)
    }
}