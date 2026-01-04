package com.mangala.eticket.domain.repository.event


import app.cash.paging.PagingData
import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.EventsResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.event.EventDetailResponse
import com.mangala.eticket.data.model.event.EventListResponse
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import commangalaeticketdatabase.EventListEntity
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    suspend fun getEvents(
        eventTitle: String?,
        categoriesId: MutableList<String>?,
        pageNumber: Int?,
        pageSize: Int?,
        eventVenue: String?,
        eventStartTime: Long?
    ): ApiResponse<ETicketResponse<PageResponse<EventListResponse>>, CustomError>
    suspend fun getEvent(id: String): ApiResponse<ETicketResponse<EventDetailResponse>, CustomError>

    suspend fun getEventsWithAppCash(eventTitle: String?,
                          categoriesId: MutableList<String>?,
                          eventVenue: String?,
                          eventStartTime: Long?
    ): Flow<PagingData<EventListEntity>>

}