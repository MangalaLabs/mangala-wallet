package com.mangala.eticket.data.remote

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.ticket.PurchasingTicketRequest
import com.mangala.eticket.data.model.ticket.TicketPreparePurchaseRequest
import com.mangala.eticket.data.model.ticket.TicketPreparePurchaseResponse
import com.mangala.eticket.data.model.ticket.TicketPurchaseResponse
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError

class TicketPurchaseDataSource(private val api: TicketPurchaseApi) {
    suspend fun preparePurchaseTicket(
        eventId: String,
        ticketPreparePurchaseRequest: TicketPreparePurchaseRequest,
    ): ApiResponse<ETicketResponse<TicketPreparePurchaseResponse>, CustomError> {
        return safeApiCall {
            api.preparePurchaseTicket(
                eventId,
                ticketPreparePurchaseRequest
            )
        }
    }


    suspend fun purchaseTicket(
        eventId: String,
        ticketId: String,
        purchasingTicketRequest: PurchasingTicketRequest,
    ): ApiResponse<ETicketResponse<TicketPurchaseResponse>, CustomError> {
        return safeApiCall {
            api.purchaseTicket(
                eventId,
                ticketId,
                purchasingTicketRequest
            )
        }
    }
}