package com.mangala.wallet.twofactorauth.domain.usecase

import com.mangala.wallet.twofactorauth.data.model.TotpSetupResult
import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository

/**
 * Use case: Set up 2FA
 */
class Setup2FAUseCase(private val repository: TwoFactorRepository) {
    suspend operator fun invoke(walletAddress: String): TotpSetupResult {
        return repository.setup2FA(walletAddress)
    }
}