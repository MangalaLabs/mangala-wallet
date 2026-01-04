package com.mangala.eticket.data.model

data class EventsResponse(
    val status: Int?,
    val timestamp: String?,
    val data: Data?
) {

    data class Data(
        val totalPages: Int?,
        val totalElements: Int?,
        val pageable: Pageable?,
        val size: Int?,
        val content: List<ContentItem>?,
        val number: Int?,
        val sort: Sort?,
        val numberOfElements: Int?,
        val first: Boolean?,
        val last: Boolean?,
        val empty: Boolean?
    )

    data class Pageable(
        val pageNumber: Int?,
        val pageSize: Int?,
        val offset: Int?,
        val sort: Sort?,
        val unpaged: Boolean?,
        val paged: Boolean?
    )

    data class Sort(
        val sorted: Boolean?,
        val empty: Boolean?,
        val unsorted: Boolean?
    )

    data class ContentItem(
        val id: String?,
        val title: String?,
        val start_time: Long?,
        val end_time: Long?,
        val venue: String?,
        val thumb_url: String?
    )
}