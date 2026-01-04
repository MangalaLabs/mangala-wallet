package com.mangala.eticket.data.model.ticket

import kotlinx.serialization.Serializable


@Serializable
data class PurchasingTicketRequest(
    val ticketId: String,
    val price: Double,
    val currencyId: String,
)