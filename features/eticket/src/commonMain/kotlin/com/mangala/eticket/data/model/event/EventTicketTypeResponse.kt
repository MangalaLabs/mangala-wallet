package com.mangala.eticket.data.model.event

import kotlinx.serialization.Serializable


@Serializable
data class EventTicketTypeResponse(
    val id: String? = null,
    val name: String? = null,
    val amount: Int? = null,
    val price: Double? = null,
)