package com.mangala.eticket.data.model.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventMediaResponse(
    val id: String? = null,
    val url: String? = null,
    val type: String? = null,
    @SerialName("event_id")
    val eventId: String? = null,
    val position: Int
)