package com.mangala.eticket.data.model.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EventListResponse(
    val id: String,
    val title: String,
    @SerialName("start_time")
    val startTime: Long,
    @SerialName("end_time")
    val endTime: Long,
    val venue: String,
    @SerialName("thumb_url")
    val thumbUrl: String
)