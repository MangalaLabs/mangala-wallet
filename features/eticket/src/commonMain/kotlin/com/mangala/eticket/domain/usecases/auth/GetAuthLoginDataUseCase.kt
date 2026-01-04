package com.mangala.eticket.domain.usecases.auth

import com.mangala.eticket.domain.model.auth.AuthLoginData
import com.mangala.eticket.domain.repository.auth.AuthRepository

class GetAuthLoginDataUseCase(private val authRepository: AuthRepository) {
    fun invoke(publicKey: String): AuthLoginData? {
        return authRepository.getAuthEntity(publicKey)
    }
}