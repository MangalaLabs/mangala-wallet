package com.mangala.eticket.domain.usecases.auth

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.auth.AuthenticationResponse
import com.mangala.eticket.data.model.auth.LoginRequest
import com.mangala.eticket.domain.repository.auth.AuthRepository
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend fun invoke(publicKey: String, signedPublicKey: String, signedMessage: String, signature: String): ApiResponse<ETicketResponse<AuthenticationResponse>, CustomError> {
        val requestBody = LoginRequest(publicKey, signedPublicKey, signedMessage, signature)
        return authRepository.login(requestBody)
    }
}