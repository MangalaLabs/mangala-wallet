package com.mangala.eticket.data.remote

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.auth.AuthenticationResponse
import com.mangala.eticket.data.model.auth.LoginRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface AuthApi {
    @POST("v1/auth/login")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun login(
        @Body body: LoginRequest
    ): ETicketResponse<AuthenticationResponse>
}