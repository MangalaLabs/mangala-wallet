package com.mangala.wallet.domain.reset.usecases

interface ClearConversationHistoryUseCase {
    suspend operator fun invoke(): Result<Unit>
}