package com.mangala.wallet.passkey.domain.usecase

import com.mangala.wallet.passkey.domain.model.PasskeyRegistrationRequest
import com.mangala.wallet.passkey.domain.model.PasskeyRegistrationResult
import com.mangala.wallet.passkey.domain.repository.PasskeyDomainRepository

/**
 * Use case for registering a new passkey
 */
class RegisterPasskeyUseCase(
    private val passkeyRepository: PasskeyDomainRepository
) {
    suspend operator fun invoke(
        userId: String,
        userName: String,
        displayName: String
    ): PasskeyRegistrationResult {
        // Validate inputs
        require(userId.isNotBlank()) { "User ID cannot be blank" }
        require(userName.isNotBlank()) { "Username cannot be blank" }
        require(displayName.isNotBlank()) { "Display name cannot be blank" }
        
        // Check if passkeys are supported
        if (!passkeyRepository.isSupported()) {
            return PasskeyRegistrationResult(
                success = false,
                errorMessage = "Passkeys are not supported on this device"
            )
        }
        
        val request = PasskeyRegistrationRequest(
            userId = userId,
            userName = userName,
            displayName = displayName
        )
        
        return passkeyRepository.register(request)
    }
}