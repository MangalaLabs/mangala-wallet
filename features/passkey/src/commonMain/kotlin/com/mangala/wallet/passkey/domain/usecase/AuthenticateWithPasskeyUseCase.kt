package com.mangala.wallet.passkey.domain.usecase

import com.mangala.wallet.passkey.domain.model.PasskeyAuthenticationRequest
import com.mangala.wallet.passkey.domain.model.PasskeyAuthenticationResult
import com.mangala.wallet.passkey.domain.repository.PasskeyDomainRepository

/**
 * Use case for authenticating with passkey
 */
class AuthenticateWithPasskeyUseCase(
    private val passkeyRepository: PasskeyDomainRepository
) {
    suspend operator fun invoke(
        userId: String? = null,
        username: String? = null
    ): PasskeyAuthenticationResult {
        // Check if passkeys are supported
        if (!passkeyRepository.isSupported()) {
            return PasskeyAuthenticationResult(
                success = false,
                errorMessage = "Passkeys are not supported on this device"
            )
        }
        
        val request = PasskeyAuthenticationRequest(
            userId = userId,
            username = username
        )
        
        return passkeyRepository.authenticate(request)
    }
}