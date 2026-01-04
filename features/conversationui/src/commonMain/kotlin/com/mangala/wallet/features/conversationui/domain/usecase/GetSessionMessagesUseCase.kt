package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSessionMessagesUseCase(
    private val chatHistoryRepository: ChatHistoryRepository
) {
    operator fun invoke(sessionId: String): Flow<List<Message>> {
        return chatHistoryRepository.getSessionFlow(sessionId)
            .map { session -> 
                session.messages
            }
    }
}