package com.mangala.eticket.data.model

data class EventResponse(
    val status: Int?,
    val timestamp: String?,
    val data: Event?
){
    data class Event(
        val id: String?,
        val title: String?,
        val description: String?,
        val start_time: Long?,
        val end_time: Long?,
        val venue: String?,
        val refund_policy: String?,
        val status: Int?,
        val categories: List<Category>?,
        val medias: List<Media>?,
        val ticket_types: List<TicketType>?
    )

    data class Category(
        val id: String?,
        val name: String?,
        val description: String?,
        val maximum_events: Int?
    )

    data class Media(
        val id: String?,
        val url: String?,
        val type: String?,
        val event_id: String?,
        val position: Int?
    )

    data class TicketType(
        val name: String?,
        val amount: Int?,
        val price: Double?
    )
}

