package com.mangala.eticket.domain.usecases.event

import app.cash.paging.PagingData
import com.mangala.eticket.domain.repository.event.EventsRepository
import commangalaeticketdatabase.EventListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

class GetEventsUseCase(private val repository: EventsRepository) {
    suspend operator fun invoke(
        eventTitle: String? = null,
        categoriesId: MutableList<String>? = mutableListOf(),
        eventVenue: String? = null,
        eventStartTime: Long? = null
    ): Flow<PagingData<EventListEntity>> {
        return runBlocking {
            return@runBlocking repository.getEventsWithAppCash(eventTitle, categoriesId, eventVenue, eventStartTime)
        }

    }
}