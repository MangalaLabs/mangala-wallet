package com.mangala.eticket.data.local.event

import app.cash.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import com.mangala.eticket.data.local.ETicketDatabaseWrapper
import commangalaeticketdatabase.EventListEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class EventListLocalDataSourceImpl(
    databaseWrapper: ETicketDatabaseWrapper
): EventListLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.eTicketDatabaseQueries

    override fun insertEvents(events: List<EventListEntity>) {
        dbQuery.transaction {
            events.forEach {
                insertEvent(it)
            }
        }
    }

    override fun search(
        eventTitle: String?,
        categoriesId: MutableList<String>?,
        eventVenue: String?,
        eventStartTime: Long?
    ): PagingSource<Int, EventListEntity> = QueryPagingSource(
        countQuery = dbQuery.countEvents(
            title = eventTitle,
            startTime = eventStartTime,
            venue = eventVenue,
            categoriesId = categoriesId?.joinToString(separator = ", ")
        ),
        transacter = dbQuery,
        context = Dispatchers.IO,
        queryProvider = { limit, offset ->
            dbQuery.searchEvents(
                title = eventTitle,
                startTime = eventStartTime,
                venue = eventVenue,
                categoriesId = categoriesId?.joinToString(separator = ", "),
                limit = limit,
                offset = offset
            )
        }
    )

    override fun clearAll() {
        dbQuery.transaction {
            dbQuery.clearAllEventListEntities()
        }
    }
    
    private fun insertEvent(event: EventListEntity) {
        with(event) {
            dbQuery.insertOrReplaceEvents(
                id = event.id,
                title = event.title,
                startTime = event.startTime,
                endTime = event.endTime,
                venue = event.venue,
                thumb = event.thumb,
                categoriesId = categoriesId
            )
        }
    }
}