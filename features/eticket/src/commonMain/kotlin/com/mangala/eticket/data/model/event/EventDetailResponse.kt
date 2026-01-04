package com.mangala.eticket.data.model.event

import com.mangala.eticket.data.model.category.CategoryResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDetailResponse(
    val title: String,
    val description: String,
    @SerialName("start_time")
    val startTime: Long,
    @SerialName("end_time")
    val endTime: Long,
    val venue: String,
    @SerialName("refund_policy")
    val refundPolicy: String,
    val status: Int,
    val categories: List<CategoryResponse>,
    val medias: List<EventMediaResponse>,
    @SerialName("ticket_types")
    val ticketTypes: List<EventTicketTypeResponse>
)