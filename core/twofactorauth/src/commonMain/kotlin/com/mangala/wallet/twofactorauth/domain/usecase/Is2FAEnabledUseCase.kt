package com.mangala.wallet.twofactorauth.domain.usecase

import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository

class Is2FAEnabledUseCase(private val repository: TwoFactorRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.is2FAEnabled()
    }
}