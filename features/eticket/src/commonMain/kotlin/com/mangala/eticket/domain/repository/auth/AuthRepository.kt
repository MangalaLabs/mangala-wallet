package com.mangala.eticket.domain.repository.auth

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.auth.AuthenticationResponse
import com.mangala.eticket.data.model.auth.LoginRequest
import com.mangala.eticket.domain.model.auth.AuthLoginData
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

interface AuthRepository {
    suspend fun login(body: LoginRequest): ApiResponse<ETicketResponse<AuthenticationResponse>, CustomError>

    fun saveAuthEntity(authLoginData: AuthLoginData)

    fun getAuthEntity(id: String): AuthLoginData?
}