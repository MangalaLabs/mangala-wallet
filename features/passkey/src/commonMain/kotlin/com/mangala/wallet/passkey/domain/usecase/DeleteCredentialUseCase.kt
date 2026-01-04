package com.mangala.wallet.passkey.domain.usecase

import com.mangala.wallet.passkey.domain.repository.PasskeyDomainRepository

/**
 * Use case for deleting a passkey credential
 */
class DeleteCredentialUseCase(
    private val passkeyRepository: PasskeyDomainRepository
) {
    suspend operator fun invoke(credentialId: String): Boolean {
        require(credentialId.isNotBlank()) { "Credential ID cannot be blank" }
        return passkeyRepository.deleteCredential(credentialId)
    }
}