package com.mangala.eticket.data.model.ticket

import kotlinx.serialization.Serializable


@Serializable
data class TicketPreparePurchaseResponse(
    val ticketIds: List<String> = emptyList(),
)