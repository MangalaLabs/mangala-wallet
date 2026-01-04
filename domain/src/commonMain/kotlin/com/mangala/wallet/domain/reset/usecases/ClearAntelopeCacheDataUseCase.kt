package com.mangala.wallet.domain.reset.usecases

interface ClearAntelopeCacheDataUseCase {
    suspend operator fun invoke(): Result<Unit>
}