package com.mangala.eticket.data.remote

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.EventsResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.event.EventDetailResponse
import com.mangala.eticket.data.model.event.EventListResponse
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError


class EventsDataSource(private val api: EventsApi) {

    suspend fun getEvents(
        eventTitle: String? = null,
        categoriesId: List<String>? = mutableListOf(),
        pageNumber: Int? = 0,
        pageSize: Int? = 5,
        eventVenue: String? = null,
        eventStartTime: Long? = null
    ): ApiResponse<ETicketResponse<PageResponse<EventListResponse>>, CustomError> {
        return safeApiCall {
            api.getEvents(
                eventTitle,
                categoriesId,
                pageNumber,
                pageSize,
                eventVenue,
                eventStartTime
            )
        }
    }

    suspend fun getEvent(id: String): ApiResponse<ETicketResponse<EventDetailResponse>, CustomError> {
        return safeApiCall {
            api.getEvent(id)
        }
    }
}