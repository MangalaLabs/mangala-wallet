package com.mangala.eticket.data.remote

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.user.RegisterRequest
import com.mangala.eticket.data.model.user.UserResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface UserApi {
    @POST("v1/users")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun createUser(
        @Body body: RegisterRequest
    ): ETicketResponse<UserResponse>

    @POST("v1/users/{id}:exists")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun checkUserExist(
        @Path("id") id: String
    ): ETicketResponse<Boolean>
}