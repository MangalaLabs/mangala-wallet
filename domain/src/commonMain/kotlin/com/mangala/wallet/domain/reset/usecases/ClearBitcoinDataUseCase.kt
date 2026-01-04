package com.mangala.wallet.domain.reset.usecases

interface ClearBitcoinDataUseCase {
    suspend operator fun invoke(): Result<Unit>
}