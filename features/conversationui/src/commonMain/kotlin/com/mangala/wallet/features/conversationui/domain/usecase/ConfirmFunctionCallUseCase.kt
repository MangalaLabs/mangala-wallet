package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallMessage
import com.mangala.wallet.core.ai.domain.model.message.FunctionResultMessage
import com.mangala.wallet.core.ai.domain.repository.AiRepository
import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.factory.MessageFactoryRegistry
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import com.mangala.wallet.features.conversationui.domain.util.mapAIResponseToMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Use case to handle user confirmation of a function call
 */
@OptIn(ExperimentalUuidApi::class)
class ConfirmFunctionCallUseCase(
    private val chatHistoryRepository: ChatHistoryRepository,
    private val aiRepository: AiRepository,
    private val functionHandlerRegistry: FunctionHandlerRegistry,
    private val messageFactoryRegistry: MessageFactoryRegistry
) {
    suspend operator fun invoke(
        sessionId: String,
        messageId: String,
        userId: String,
        functionCall: FunctionCallRequest
    ): Flow<AIResponse> {
        val handler = functionHandlerRegistry.getHandlerByName(functionCall.name) ?: return flowOf(
            AIResponse.ErrorResponse("Function handler not found")
        )

        var currentSession = chatHistoryRepository.getSession(sessionId)
            ?: return flowOf(AIResponse.ErrorResponse("Session not found"))
        
        try {
            // Mark message as loading to prevent further interactions
            chatHistoryRepository.markMessageAsLoading(currentSession.id, messageId, true)
        
        // Save the function call message
        val functionCallMsg = FunctionCallMessage(
            id = Uuid.random().toString(),
            senderId = "",
            functionName = functionCall.name,
            parameters = functionCall.parameters,
            explanation = "User confirmed function execution",
            callId = functionCall.callId
        )
        chatHistoryRepository.saveMessage(currentSession.id, functionCallMsg)
        
        // Execute the function
        val result = handler.execute(functionCall.parameters)
        println("ConfirmFunctionCallUseCase executed function ${functionCall.name} $result")

        // Update the confirmation message execution status based on result
        val executionStatus = if (result is FunctionResult.Success) {
            ExecutionStatus.EXECUTED
        } else {
            ExecutionStatus.FAILED
        }
        chatHistoryRepository.updateMessageExecutionStatus(messageId, executionStatus)

        // Try to create feature-specific message if result has UI hints
        if (result is FunctionResult.Success && result.uiHint != null) {
            val featureMessage = messageFactoryRegistry.createMessageFromFunctionResult(
                functionName = functionCall.name,
                result = result,
                messageId = Uuid.random().toString(),
                senderId = ""
            )
            
            featureMessage?.let {
                chatHistoryRepository.saveMessage(currentSession.id, it)
            }
        }

        // Save the function result message
        val functionResultMsg = FunctionResultMessage(
            id = Uuid.random().toString(),
            senderId = userId,
            functionName = functionCall.name,
            result = result,
            callId = functionCall.callId
        )
        chatHistoryRepository.saveMessage(currentSession.id, functionResultMsg)
        
        // Get updated session with all messages
        currentSession = chatHistoryRepository.getSession(sessionId) 
            ?: return flowOf(AIResponse.ErrorResponse("Session not found"))
        
        // Once processing is complete, mark the message as not loading anymore
        chatHistoryRepository.markMessageAsLoading(currentSession.id, messageId, false)
        
            return flowOf()
        } catch (e: Exception) {
            chatHistoryRepository.updateMessageExecutionStatus(messageId, ExecutionStatus.FAILED)
            chatHistoryRepository.markMessageAsLoading(sessionId, messageId, false)
            throw e
        }
    }
}