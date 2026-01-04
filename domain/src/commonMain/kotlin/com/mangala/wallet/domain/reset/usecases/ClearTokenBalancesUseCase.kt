package com.mangala.wallet.domain.reset.usecases

import com.mangala.wallet.domain.token.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearTokenBalancesUseCase(
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            tokenRepository.clearAllUserTokenBalances().getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}