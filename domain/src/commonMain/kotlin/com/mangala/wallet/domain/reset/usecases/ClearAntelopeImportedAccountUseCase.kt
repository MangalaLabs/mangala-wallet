package com.mangala.wallet.domain.reset.usecases

interface ClearAntelopeImportedAccountUseCase {
    suspend operator fun invoke(): Result<Unit>
}