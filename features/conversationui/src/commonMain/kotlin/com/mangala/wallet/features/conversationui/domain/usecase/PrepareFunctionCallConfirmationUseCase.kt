package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.features.conversationui.data.local.ChatHistoryLocalDataSource
import com.mangala.wallet.features.conversationui.domain.util.FunctionCallHasher
import kotlinx.datetime.Clock

/**
 * Use case to prepare a function call confirmation message with proper tracking.
 * This generates the function hash and sets up the message for tracking.
 */
class PrepareFunctionCallConfirmationUseCase(
    private val repository: ChatHistoryLocalDataSource,
    private val hasher: FunctionCallHasher = FunctionCallHasher
) {
    /**
     * Prepares a function call confirmation message with tracking data.
     * 
     * @param sessionId The conversation session ID
     * @param functionCall The function call request
     * @param confirmationPrompt The prompt to show to the user
     * @param walletAddress The wallet address for context
     * @param networkId Optional network ID for multi-chain support
     * @param functionDescription Optional description of what the function does
     * @return A prepared function call confirmation message with hash and tracking
     */
    suspend operator fun invoke(
        sessionId: String,
        functionCall: FunctionCallRequest,
        confirmationPrompt: String,
        walletAddress: String,
        networkId: String? = null,
        functionDescription: String? = null
    ): FunctionCallConfirmationRequiredMessage {
        val functionHash = hasher.generateHash(functionCall, walletAddress, networkId)
        
        val message = FunctionCallConfirmationRequiredMessage(
            id = generateMessageId(),
            senderId = "assistant",
            isFromUser = false,
            parentMessageId = null,
            timestamp = Clock.System.now(),
            functionCall = functionCall,
            confirmationPrompt = confirmationPrompt,
            functionDescription = functionDescription,
            executionStatus = ExecutionStatus.PENDING,
            functionHash = functionHash
        )
        
        // Insert the message into the database
        repository.insertMessage(sessionId, message)
        
        return message
    }
    
    /**
     * Prepares a function call confirmation with time window tracking.
     * Useful for operations that can be repeated after a certain period.
     */
    suspend fun prepareWithTimeWindow(
        sessionId: String,
        functionCall: FunctionCallRequest,
        confirmationPrompt: String,
        walletAddress: String,
        networkId: String? = null,
        functionDescription: String? = null,
        windowSizeMinutes: Int = 60
    ): FunctionCallConfirmationRequiredMessage {
        val functionHash = hasher.generateHashWithTimeWindow(
            functionCall, 
            walletAddress, 
            networkId, 
            windowSizeMinutes
        )
        
        val message = FunctionCallConfirmationRequiredMessage(
            id = generateMessageId(),
            senderId = "assistant",
            isFromUser = false,
            parentMessageId = null,
            timestamp = Clock.System.now(),
            functionCall = functionCall,
            confirmationPrompt = confirmationPrompt,
            functionDescription = functionDescription,
            executionStatus = ExecutionStatus.PENDING,
            functionHash = functionHash
        )
        
        // Insert the message into the database
        repository.insertMessage(sessionId, message)
        
        return message
    }
    
    private fun generateMessageId(): String {
        return "msg_${Clock.System.now().toEpochMilliseconds()}_${(0..999).random()}"
    }
}