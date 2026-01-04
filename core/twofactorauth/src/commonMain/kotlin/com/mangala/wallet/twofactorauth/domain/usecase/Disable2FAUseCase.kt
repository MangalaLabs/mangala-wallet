package com.mangala.wallet.twofactorauth.domain.usecase

import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository

/**
 * Use case: Disable 2FA
 */
class Disable2FAUseCase(private val repository: TwoFactorRepository) {
    suspend operator fun invoke(confirmationCode: String): Boolean {
        return repository.disable2FA(confirmationCode)
    }
}