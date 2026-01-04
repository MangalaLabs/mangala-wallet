package com.mangala.eticket.data.remote

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.event.EventDetailResponse
import com.mangala.eticket.data.model.event.EventListResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface EventsApi {

    @GET("v1/events")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun getEvents(
        @Query("event_title") eventTitle: String?,
        @Query("categories_id") categoriesId: List<String>?,
        @Query("page_number") pageNumber: Int?,
        @Query("page_size") pageSize: Int?,
        @Query("event_venue") eventVenue: String?,
        @Query("event_start_time") eventStartTime: Long?,
    ): ETicketResponse<PageResponse<EventListResponse>>

    @GET("v1/events/{id}")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun getEvent(
        @Path("id") id: String
    ): ETicketResponse<EventDetailResponse>
}