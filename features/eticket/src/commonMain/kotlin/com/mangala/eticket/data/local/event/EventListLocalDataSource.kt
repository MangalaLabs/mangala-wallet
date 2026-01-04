package com.mangala.eticket.data.local.event

import app.cash.paging.PagingSource
import commangalaeticketdatabase.EventListEntity

interface EventListLocalDataSource {
    fun insertEvents(events: List<EventListEntity>)
    fun search(
        eventTitle: String?,
        categoriesId: MutableList<String>?,
        eventVenue: String?,
        eventStartTime: Long?
    ): PagingSource<Int, EventListEntity>
    fun clearAll()
}