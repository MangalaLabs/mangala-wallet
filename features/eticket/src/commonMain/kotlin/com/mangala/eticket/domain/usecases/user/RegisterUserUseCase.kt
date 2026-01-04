package com.mangala.eticket.domain.usecases.user

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.user.RegisterRequest
import com.mangala.eticket.data.model.user.UserResponse
import com.mangala.eticket.domain.repository.UsersRepository
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

class RegisterUserUseCase(private val usersRepository: UsersRepository) {
    suspend fun invoke(id: String, fullName: String): ApiResponse<ETicketResponse<UserResponse>, CustomError> {
        val registerRequest = RegisterRequest(id = id, fullName = fullName)
        return usersRepository.createUser(registerRequest)
    }
}