package com.mangala.eticket.data.model.ticket

import kotlinx.serialization.Serializable

@Serializable
data class TicketTypePurchaseRequest(
    val ticketTypeId: String,
    val quantity: Int,
)