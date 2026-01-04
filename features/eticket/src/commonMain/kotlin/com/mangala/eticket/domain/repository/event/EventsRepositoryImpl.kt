package com.mangala.eticket.domain.repository.event


import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.mangala.eticket.data.local.cache.RemotePagingKeyLocalDataSource
import com.mangala.eticket.data.local.event.EventListLocalDataSource
import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.event.EventDetailResponse
import com.mangala.eticket.data.model.event.EventListResponse
import com.mangala.eticket.data.remote.EventsDataSource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import commangalaeticketdatabase.EventListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventsRepositoryImpl(
    private val dataSource: EventsDataSource,
    private val remotePagingKeyLocalDataSource: RemotePagingKeyLocalDataSource,
    private val eventListLocalDataSource: EventListLocalDataSource
) : EventsRepository {
    override suspend fun getEvents(eventTitle: String?,
                                    categoriesId: MutableList<String>?,
                                    pageNumber: Int?,
                                    pageSize: Int?,
                                    eventVenue: String?,
                                    eventStartTime: Long?
    ): ApiResponse<ETicketResponse<PageResponse<EventListResponse>>, CustomError> {
        return dataSource.getEvents(
            eventTitle,
            categoriesId,
            pageNumber,
            pageSize,
            eventVenue,
            eventStartTime
        )
    }

    override suspend fun getEvent(id: String):
            ApiResponse<ETicketResponse<EventDetailResponse>, CustomError> {
        return dataSource.getEvent(id)
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getEventsWithAppCash(
        eventTitle: String?,
        categoriesId: MutableList<String>?,
        eventVenue: String?,
        eventStartTime: Long?
    ): Flow<PagingData<EventListEntity>> {
        println("getEventsWithAppCash: $categoriesId")
        return Pager (
            config = PagingConfig(pageSize = 10),
            remoteMediator = EventsRemoteMediator(
                eventTitle = eventTitle,
                categoriesId = categoriesId,
                eventVenue = eventVenue,
                eventStartTime = eventStartTime,

                remotePagingKeyLocalDataSource = remotePagingKeyLocalDataSource,
                dataSource = dataSource,
                eventListLocalDataSource = eventListLocalDataSource
            )
        ) {
            eventListLocalDataSource.search(
                eventTitle, categoriesId, eventVenue, eventStartTime
            )
        }.flow
    }

}