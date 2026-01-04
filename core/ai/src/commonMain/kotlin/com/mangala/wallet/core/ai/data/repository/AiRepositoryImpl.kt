package com.mangala.wallet.core.ai.data.repository

import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.data.remote.AIServiceConfig
import com.mangala.wallet.core.ai.data.remote.AIServiceFactory
import com.mangala.wallet.core.ai.data.remote.AIServiceType
import com.mangala.wallet.core.ai.data.remote.AiRemoteDataSource
import com.mangala.wallet.core.ai.data.repository.mapper.toAiMessages
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow

internal class AiRepositoryImpl(
    private val aiServiceFactory: AIServiceFactory,
    private val activeServiceType: AIServiceType
) : AiRepository {
    
    private val aiService: AiRemoteDataSource by lazy {
        aiServiceFactory.createAIService(
            config = AIServiceConfig(
                type = activeServiceType,
                apiKey = "",
                modelName = ""
            )
        )
    }
    
    override suspend fun processConversation(
        userId: String,
        messages: List<Message>,
        isMultiModalEnabled: Boolean
    ): Flow<AIResponse> {
        val remoteMessages = messages.toAiMessages()

        return aiService.processMessage(
            userId = userId,
            conversationContext = remoteMessages,
            isMultiModalEnabled = isMultiModalEnabled
        )
    }
}
