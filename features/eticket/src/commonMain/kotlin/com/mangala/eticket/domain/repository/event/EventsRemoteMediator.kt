package com.mangala.eticket.domain.repository.event

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorInitializeAction
import app.cash.paging.RemoteMediatorMediatorResult
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import com.mangala.eticket.data.local.cache.RemotePagingKeyLocalDataSource
import com.mangala.eticket.data.local.event.EventListLocalDataSource
import com.mangala.eticket.data.remote.EventsDataSource
import com.mangala.eticket.domain.repository.RepositoryKeyConstants.EVENT_LIST_KEY
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.utils.currentTimeInMillis
import com.mangala.wallet.utils.ext.orZero
import commangalaeticketdatabase.EventListEntity
import commangalaeticketdatabase.RemotePagingKeyEntity

@OptIn(ExperimentalPagingApi::class)
class EventsRemoteMediator(
    private val eventTitle: String? = null,
    private val categoriesId: MutableList<String>? = null,
    private val eventVenue: String? = null,
    private val eventStartTime: Long? = null,

    private val remotePagingKeyLocalDataSource: RemotePagingKeyLocalDataSource,
    private val dataSource: EventsDataSource,
    private val eventListLocalDataSource: EventListLocalDataSource
): RemoteMediator<Int, EventListEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventListEntity>
    ): RemoteMediatorMediatorResult {
        return try {
            val key = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND ->
                    return RemoteMediatorMediatorResultSuccess(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val nextPage = remotePagingKeyLocalDataSource.findById(EVENT_LIST_KEY)?.lastRequestedPage

                    if (nextPage == null) {
                        return RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached = true
                        )
                    }

                    nextPage
                }
            }

            val response = if (key == null) {
                dataSource.getEvents(
                    eventTitle,
                    categoriesId,
                    pageSize = state.config.pageSize,
                    eventVenue = eventVenue,
                    eventStartTime = eventStartTime
                )
            } else {
                dataSource.getEvents(
                    eventTitle = eventTitle,
                    categoriesId = categoriesId,
                    pageNumber = key?.toInt() ?: 0,
                    pageSize = state.config.pageSize,
                    eventVenue,
                    eventStartTime
                )
            }

            if (loadType == LoadType.REFRESH) {
                remotePagingKeyLocalDataSource.deleteById(EVENT_LIST_KEY)
            }

            if (response is ApiResponse.Success) {
                val prev = response.body.data?.pageable?.pageNumber ?: 0
                val nextKey = if (prev < response.body?.data?.totalPages ?: 0) prev?.plus(1) else null

                remotePagingKeyLocalDataSource.insertOrReplace(
                    RemotePagingKeyEntity(EVENT_LIST_KEY, nextKey?.toLong() ?: 0, currentTimeInMillis())
                )

                val data = response.body.data?.content?.mapNotNull {
                    EventListEntity(
                        id = it.id,
                        title = it.title,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        venue = it.venue,
                        thumb = it.thumbUrl,
                        categoriesId = categoriesId?.joinToString(separator = ", ")
                    )
                } ?: emptyList()
                eventListLocalDataSource.insertEvents(data)

                RemoteMediatorMediatorResultSuccess(
                    endOfPaginationReached = nextKey == null
                )
            } else {
                RemoteMediatorMediatorResultError(Exception("Network error in loading transactions $response"))
            }
        } catch (e: Exception) {
            RemoteMediatorMediatorResultError(e)
        }
    }

    override suspend fun initialize(): RemoteMediatorInitializeAction {
        val timeNow = currentTimeInMillis()
        val lastSyncedTimestamp = remotePagingKeyLocalDataSource.findById(EVENT_LIST_KEY)?.lastSyncedTimestamp.orZero()
        val shouldRefresh = timeNow - lastSyncedTimestamp > CACHE_TIMEOUT_MILLIS

        return if (shouldRefresh) {
            RemoteMediatorInitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            RemoteMediatorInitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    companion object {
        // 10
        private const val CACHE_TIMEOUT_MILLIS = 2 * 60 * 1000 // 2 minutes
    }
}