package com.mangala.wallet.domain.reset.usecases

interface ClearNFTDataUseCase {
    suspend operator fun invoke(): Result<Unit>
}