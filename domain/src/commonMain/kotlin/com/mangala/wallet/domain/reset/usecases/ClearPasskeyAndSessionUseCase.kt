package com.mangala.wallet.domain.reset.usecases

interface ClearPasskeyAndSessionUseCase {
    suspend operator fun invoke(): Result<Unit>
}
