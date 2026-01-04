package com.mangala.eticket.domain.repository.favourite

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.mangala.eticket.data.model.favourite.UserEventFavouriteResponse
import com.mangala.eticket.data.remote.favourite.UserEventFavouriteDataSource
import kotlinx.coroutines.flow.Flow

class UserEventFavouriteRepositoryImpl(
    private val datasource: UserEventFavouriteDataSource
): UserEventFavouriteRepository {
    override fun listUserEventFavourite(): Flow<PagingData<UserEventFavouriteResponse>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            initialKey = 1,
            pagingSourceFactory = { UserEventFavouritePagingSource(datasource) }
        ).flow
    }
}