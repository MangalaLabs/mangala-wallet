package com.mangala.eticket.data.model.ticket

import kotlinx.serialization.Serializable

@Serializable
data class TicketPurchaseResponse(
    val id: String = "",
    val purchaseTime: Long = 0,
    val purchasePrice: Double = 0.0,
    val purchaseCurrencyId: String = "",
    val ticketId: String = "",
    val buyerId: String = "",
)