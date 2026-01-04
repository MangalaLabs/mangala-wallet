package com.mangala.wallet.twofactorauth.domain.usecase

import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository
import kotlinx.coroutines.flow.Flow

class GetAuthStateUseCase(private val repository: TwoFactorRepository) {
    operator fun invoke(): Flow<Boolean> {
        return repository.getAuthStateFlow()
    }
}
