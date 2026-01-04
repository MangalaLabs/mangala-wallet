package com.mangala.wallet.twofactorauth.domain.usecase

import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository

/**
 * Use case: Verify a 2FA code
 */
class VerifyCodeUseCase(private val repository: TwoFactorRepository) {
    suspend operator fun invoke(code: String): Boolean {
        return repository.verifyCode(code)
    }
}