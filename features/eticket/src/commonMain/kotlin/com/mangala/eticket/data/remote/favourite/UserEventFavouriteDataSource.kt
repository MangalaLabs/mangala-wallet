package com.mangala.eticket.data.remote.favourite

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.favourite.UserEventFavouriteResponse
import com.mangala.eticket.di.ApiResponse
import com.mangala.eticket.di.safeApiCall
import com.mangala.eticket.network.CustomError

class UserEventFavouriteDataSource(private val api: UserEventFavouriteApi) {
    suspend fun listUserEventsFavourites(page: Int?, pageSize: Int?): ApiResponse<ETicketResponse<PageResponse<UserEventFavouriteResponse>>, CustomError> {
        return safeApiCall {
            api.listUserEventsFavourite(page, pageSize)
        }
    }
}