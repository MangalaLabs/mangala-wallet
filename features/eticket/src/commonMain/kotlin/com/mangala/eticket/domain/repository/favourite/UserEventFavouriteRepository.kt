package com.mangala.eticket.domain.repository.favourite

import app.cash.paging.PagingData
import com.mangala.eticket.data.model.favourite.UserEventFavouriteResponse
import kotlinx.coroutines.flow.Flow

interface UserEventFavouriteRepository {
    fun listUserEventFavourite(): Flow<PagingData<UserEventFavouriteResponse>>
}