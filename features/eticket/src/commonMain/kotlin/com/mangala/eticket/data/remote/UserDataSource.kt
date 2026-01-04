package com.mangala.eticket.data.remote

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.user.RegisterRequest
import com.mangala.eticket.data.model.user.UserResponse
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError

class UserDataSource(private val api: UserApi) {
    suspend fun createUser(body: RegisterRequest): ApiResponse<ETicketResponse<UserResponse>, CustomError> {
        return safeApiCall {
            api.createUser(body)
        }
    }

    suspend fun checkUserExist(id: String): ApiResponse<ETicketResponse<Boolean>, CustomError> {
        return safeApiCall {
            api.checkUserExist(id)
        }
    }
}