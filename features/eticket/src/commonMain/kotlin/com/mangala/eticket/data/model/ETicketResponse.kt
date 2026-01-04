package com.mangala.eticket.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ETicketResponse<T> (
    val status: Int,
    val timestamp: String,
    val data: T?
)