package com.mangala.eticket.domain.usecases.auth

import com.mangala.eticket.domain.repository.auth.AuthRepository
import com.mangala.eticket.domain.model.auth.AuthLoginData

class SaveAuthEntityUseCase(private val repository: AuthRepository) {
    fun invoke(authLogin: AuthLoginData) {
        repository.saveAuthEntity(authLogin)
    }
}