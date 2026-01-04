package com.mangala.wallet.twofactorauth.domain.usecase

import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository

class VerifyBackupCodeUseCase(private val repository: TwoFactorRepository) {
    suspend operator fun invoke(code: String): Boolean {
        return repository.verifyBackupCode(code)
    }
}