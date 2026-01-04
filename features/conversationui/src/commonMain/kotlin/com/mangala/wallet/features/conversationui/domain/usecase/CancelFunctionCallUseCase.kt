package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.domain.model.message.FunctionResultMessage
import com.mangala.wallet.core.ai.domain.repository.AiRepository
import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallMessage
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import com.mangala.wallet.features.conversationui.domain.util.mapAIResponseToMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Use case to handle user cancellation of a function call
 */
@OptIn(ExperimentalUuidApi::class)
class CancelFunctionCallUseCase(
    private val chatHistoryRepository: ChatHistoryRepository,
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(
        sessionId: String,
        messageId: String,
        userId: String,
        functionCall: FunctionCallRequest
    ): Flow<AIResponse> {
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
            explanation = "User cancelled function execution",
            callId = functionCall.callId
        )
        chatHistoryRepository.saveMessage(currentSession.id, functionCallMsg)
        
        val functionResultMsg = FunctionResultMessage(
            id = Uuid.random().toString(),
            senderId = userId,
            functionName = functionCall.name,
            result = FunctionResult.Error(
                code = "",
                message = "Function call cancelled"
            ),
            callId = functionCall.callId
        )
        chatHistoryRepository.saveMessage(currentSession.id, functionResultMsg)
        
        currentSession = chatHistoryRepository.getSession(sessionId)
            ?: return flowOf(AIResponse.ErrorResponse("Session not found"))
        
//        val response = aiRepository.processConversation(
//            userId = userId,
//            messages = currentSession.messages,
//            isMultiModalEnabled = false
//        ).first()
//        response.mapAIResponseToMessage(Uuid.random().toString())?.let {
//            chatHistoryRepository.saveMessage(currentSession.id, it)
//        }
        
            // Once processing is complete, mark the message as not loading anymore
            chatHistoryRepository.markMessageAsLoading(currentSession.id, messageId, false)
            chatHistoryRepository.updateMessageExecutionStatus(messageId, ExecutionStatus.CANCELLED)
        
//        return flowOf(response)
            return flowOf()
        } catch (e: Exception) {
            // If any error occurs, make sure to reset the loading state
            chatHistoryRepository.markMessageAsLoading(sessionId, messageId, false)
            throw e
        }
    }
}