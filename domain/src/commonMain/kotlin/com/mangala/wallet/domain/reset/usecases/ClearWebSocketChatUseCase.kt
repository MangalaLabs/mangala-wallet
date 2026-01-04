package com.mangala.wallet.domain.reset.usecases

interface ClearWebSocketChatUseCase {
    suspend operator fun invoke(): Result<Unit>
}