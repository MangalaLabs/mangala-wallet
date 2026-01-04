package com.mangala.eticket.data.remote

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.auth.AuthenticationResponse
import com.mangala.eticket.data.model.auth.LoginRequest
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError


class AuthDataSource(private val api: AuthApi) {
    suspend fun login(body: LoginRequest): ApiResponse<ETicketResponse<AuthenticationResponse>, CustomError> {
        return safeApiCall {
            api.login(body)
        }
    }
}