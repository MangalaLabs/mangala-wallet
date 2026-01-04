package com.mangala.wallet.websocket.chat.usecase

import com.mangala.wallet.domain.reset.usecases.ClearWebSocketChatUseCase
import com.mangala.wallet.websocket.chat.persistence.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearWebSocketChatUseCaseImpl(
    private val messageRepository: MessageRepository
) : ClearWebSocketChatUseCase {

    override suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            messageRepository.clearAllMessages()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}