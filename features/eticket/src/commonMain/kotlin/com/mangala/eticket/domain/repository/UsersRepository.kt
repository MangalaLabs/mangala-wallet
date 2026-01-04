package com.mangala.eticket.domain.repository

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.user.RegisterRequest
import com.mangala.eticket.data.model.user.UserResponse
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

interface UsersRepository {
    suspend fun checkUserExist(id: String): ApiResponse<ETicketResponse<Boolean>, CustomError>
    suspend fun createUser(body: RegisterRequest): ApiResponse<ETicketResponse<UserResponse>, CustomError>
}