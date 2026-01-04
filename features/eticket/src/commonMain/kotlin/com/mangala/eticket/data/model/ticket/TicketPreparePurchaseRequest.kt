package com.mangala.eticket.data.model.ticket

import kotlinx.serialization.Serializable


@Serializable
data class TicketPreparePurchaseRequest(
    val ticketTypes: List<TicketTypePurchaseRequest> = emptyList(),
)