package com.mangala.eticket.domain.usecases.event

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.event.EventDetailResponse
import com.mangala.eticket.domain.repository.event.EventsRepository
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

class GetEventUseCase (private val repository: EventsRepository) {

    suspend operator fun invoke(
        id: String,
    ): ApiResponse<ETicketResponse<EventDetailResponse>, CustomError> {
        return repository.getEvent(id)
    }
}