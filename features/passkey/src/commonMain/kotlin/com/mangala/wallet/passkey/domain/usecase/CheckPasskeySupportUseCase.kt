package com.mangala.wallet.passkey.domain.usecase

import com.mangala.wallet.passkey.domain.repository.PasskeyDomainRepository

/**
 * Use case for checking if passkeys are supported
 */
class CheckPasskeySupportUseCase(
    private val passkeyRepository: PasskeyDomainRepository
) {
    suspend operator fun invoke(): Boolean {
        return passkeyRepository.isSupported()
    }
}