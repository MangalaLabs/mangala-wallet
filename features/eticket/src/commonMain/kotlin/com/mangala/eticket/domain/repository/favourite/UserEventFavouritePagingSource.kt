package com.mangala.eticket.domain.repository.favourite

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import com.mangala.eticket.data.model.favourite.UserEventFavouriteResponse
import com.mangala.eticket.data.remote.favourite.UserEventFavouriteDataSource
import com.mangala.eticket.di.ApiResponse

class UserEventFavouritePagingSource(
    private val datasource: UserEventFavouriteDataSource
): PagingSource<Int, UserEventFavouriteResponse>() {
    override fun getRefreshKey(state: PagingState<Int, UserEventFavouriteResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: PagingSourceLoadParams<Int>): PagingSourceLoadResult<Int, UserEventFavouriteResponse> {
        try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            datasource.listUserEventsFavourites(page, pageSize).let {
                if (it is ApiResponse.Success) {
                    val prevKey = if (page > 0) page - 1 else null
                    val nextKey = if ((prevKey ?: 1) < (it.body.data?.totalPages ?: 1)) page + 1 else null
                    return PagingSourceLoadResultPage(
                        data = it.body.data?.content!!,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                } else {
                    return PagingSourceLoadResultError(Exception("Network error when loading user event favourite"))
                }
            }
        } catch (ex: Exception) {
            return PagingSourceLoadResultError(ex)
        }
    }
}