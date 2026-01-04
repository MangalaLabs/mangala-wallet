package com.mangala.eticket.data.remote

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.ticket.PurchasingTicketRequest
import com.mangala.eticket.data.model.ticket.TicketPreparePurchaseRequest
import com.mangala.eticket.data.model.ticket.TicketPreparePurchaseResponse
import com.mangala.eticket.data.model.ticket.TicketPurchaseResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface TicketPurchaseApi {
    @POST("v1/events/{id}/tickets/prepare-purchase")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun preparePurchaseTicket(
        @Path("id") id: String,
        @Body body: TicketPreparePurchaseRequest,
    ): ETicketResponse<TicketPreparePurchaseResponse>


    @POST("v1/events/{event_id}/tickets/{ticket_id}/purchases")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun purchaseTicket(
        @Path("event_id") eventId: String,
        @Path("ticket_id") ticketId: String,
        @Body body: PurchasingTicketRequest,
    ): ETicketResponse<TicketPurchaseResponse>
}