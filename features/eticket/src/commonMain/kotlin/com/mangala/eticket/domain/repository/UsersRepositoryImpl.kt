package com.mangala.eticket.domain.repository

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.user.RegisterRequest
import com.mangala.eticket.data.model.user.UserResponse
import com.mangala.eticket.data.remote.UserDataSource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

class UsersRepositoryImpl(private val dataSource: UserDataSource): UsersRepository {
    override suspend fun checkUserExist(id: String): ApiResponse<ETicketResponse<Boolean>, CustomError> {
        return dataSource.checkUserExist(id)
    }

    override suspend fun createUser(body: RegisterRequest): ApiResponse<ETicketResponse<UserResponse>, CustomError> {
        return dataSource.createUser(body)
    }
}