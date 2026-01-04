package com.mangala.eticket.data.remote.favourite

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.favourite.UserEventFavouriteResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Query

interface UserEventFavouriteApi {
    @GET("v1/my/favourites/events")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun listUserEventsFavourite(
        @Query("page") page: Int?,
        @Query("page_size") pageSize: Int?
    ): ETicketResponse<PageResponse<UserEventFavouriteResponse>>
}