package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository

class ClearChatContextUseCase(
    private val chatHistoryRepository: ChatHistoryRepository
) {
    suspend operator fun invoke(userId: String) {
        chatHistoryRepository.clearAllSessions(userId)
    }
}