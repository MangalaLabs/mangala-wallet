package com.mangala.eticket.domain.usecases.favourite

import app.cash.paging.PagingData
import com.mangala.eticket.data.model.favourite.UserEventFavouriteResponse
import com.mangala.eticket.domain.repository.favourite.UserEventFavouriteRepository
import kotlinx.coroutines.flow.Flow

class ListUserEventFavouriteUseCase(
    private val userEventFavouriteRepository: UserEventFavouriteRepository
) {
    fun invoke(): Flow<PagingData<UserEventFavouriteResponse>> {
        return userEventFavouriteRepository.listUserEventFavourite()
    }
}