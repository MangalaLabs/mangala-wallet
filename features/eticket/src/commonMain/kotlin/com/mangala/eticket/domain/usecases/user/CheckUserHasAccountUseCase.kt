package com.mangala.eticket.domain.usecases.user

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.domain.repository.UsersRepository
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

class CheckUserHasAccountUseCase(private val repository: UsersRepository) {
    suspend operator fun invoke(id: String): ApiResponse<ETicketResponse<Boolean>, CustomError> {
        return repository.checkUserExist(id)
    }
}