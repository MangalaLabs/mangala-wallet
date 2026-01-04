package com.mangala.wallet.core.ai.domain.repository

import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.domain.model.message.Message
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    suspend fun processConversation(
        userId: String,
        messages: List<Message>,
        isMultiModalEnabled: Boolean
    ): Flow<AIResponse>
}