package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.domain.reset.usecases.ClearConversationHistoryUseCase
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearConversationHistoryUseCaseImpl(
    private val chatHistoryRepository: ChatHistoryRepository
) : ClearConversationHistoryUseCase {

    override suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        chatHistoryRepository.clearAllConversationHistory()
    }
}