package com.mangala.eticket.data.model.category

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    @SerialName("maximum_events")
    val maximumEvents: Long? = null
)