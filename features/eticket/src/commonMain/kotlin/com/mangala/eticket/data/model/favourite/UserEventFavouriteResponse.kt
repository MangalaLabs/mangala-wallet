package com.mangala.eticket.data.model.favourite

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserEventFavouriteResponse(
    val id: String,
    val title: String,
    @SerialName("start_time")
    val startTime: Long,
    @SerialName("end_time")
    val endTime: Long,
    val venue: String,
    val status: Int,
    @SerialName("thumb_url")
    val thumbUrl: String?
)