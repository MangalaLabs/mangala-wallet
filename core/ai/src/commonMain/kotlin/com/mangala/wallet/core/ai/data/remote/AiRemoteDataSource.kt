package com.mangala.wallet.core.ai.data.remote

import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import kotlinx.coroutines.flow.Flow

/**
 * Interface for AI services that can process messages and execute functions
 */
internal interface AiRemoteDataSource {
    /**
     * Process a message in a conversation
     *
     * @param userId The ID of the user
     * @param conversationContext The conversation history
     * @param isMultiModalEnabled Flag to indicate if multimodal processing is enabled
     * @return A flow of AI responses which may include single or multimodal responses
     */
    suspend fun processMessage(
        userId: String,
        conversationContext: List<RemoteMessage>,
        isMultiModalEnabled: Boolean = false
    ): Flow<AIResponse>
    
    /**
     * Prepare function definitions for the AI service
     *
     * @param functions The list of function definitions
     * @return A provider-specific representation of the functions
     */
    fun prepareFunctionDefinitions(functions: List<FunctionDefinition>): Any
}