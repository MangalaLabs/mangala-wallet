package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.domain.model.message.ImageMessage
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.MessageSendingStatus
import com.mangala.wallet.core.ai.domain.model.message.MultiModalMessage
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import com.mangala.wallet.core.ai.domain.model.process.preprocess.PreprocessorChain
import com.mangala.wallet.core.ai.domain.model.process.preprocess.preprocessors.TextNormalizationPreprocessor
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import com.mangala.wallet.features.conversationui.domain.service.MessageType
import com.mangala.wallet.features.conversationui.domain.service.StompWebSocketService
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.JsonElement

class SendSocketMessageUseCase(
    private val stompWebSocketService: StompWebSocketService,
    private val chatHistoryRepository: ChatHistoryRepository
) {
    suspend operator fun invoke(
        userId: String,
        sessionId: String,
        message: Message,
        text: String,
        conversationId: String? = null,
        type: MessageType = MessageType.TEXT,
        metadata: Map<String, JsonElement>? = null
    ): Result<String> {
        val messageId = message.id
        
        return try {
            // Step 1: Save message with PENDING status
            val pendingMessage = ensurePendingStatus(message)
            saveUserMessage(
                userId = userId,
                sessionId = sessionId,
                message = pendingMessage
            )

            // Check connection before updating to SENDING
            if (!isConnected()) {
                Napier.e("SendSocketMessageUseCase: WebSocket not connected, attempting to reconnect...")
                
                val connected = stompWebSocketService.connect()
                if (!connected) {
                    Napier.e("SendSocketMessageUseCase: Failed to reconnect WebSocket")
                    // Step 4: Update to FAILED on error
                    chatHistoryRepository.updateMessageSendingStatus(messageId, MessageSendingStatus.FAILED)
                    return Result.failure(Exception("Failed to connect to chat server. Please try again later."))
                }
            }
            
            // Step 2: Update to SENDING when WebSocket starts transmission
            chatHistoryRepository.updateMessageSendingStatus(messageId, MessageSendingStatus.SENDING)
            
            Napier.d("SendSocketMessageUseCase: Sending message via WebSocket: $text")
            stompWebSocketService.sendMessage(
                content = text,
                conversationId = conversationId,
                type = type,
                metadata = metadata
            )

            // Step 3: Update to SENT on successful completion
            chatHistoryRepository.updateMessageSendingStatus(messageId, MessageSendingStatus.SENT)
            
            // Step 5: Return the message ID for UI tracking
            Result.success(messageId)
        } catch (e: Exception) {
            Napier.e("SendSocketMessageUseCase: Failed to send WebSocket message", e)
            // Step 4: Update to FAILED on error
            chatHistoryRepository.updateMessageSendingStatus(messageId, MessageSendingStatus.FAILED)
            Result.failure(e)
        }
    }

    private fun isConnected(): Boolean {
        return stompWebSocketService.connectionState.value == StompWebSocketService.ConnectionState.AUTHENTICATED
    }

    private suspend fun saveUserMessage(userId: String, sessionId: String, message: Message) {
        val processedMessage = preprocessMessage(message)
        chatHistoryRepository.saveMessage(sessionId, processedMessage)
    }

    private fun preprocessMessage(message: Message): Message {
        return when (message) {
            is TextMessage -> {
                val preprocessorChain = PreprocessorChain.createChain(
                    listOf(
                        TextNormalizationPreprocessor(toLowercase = false)
                    )
                )

                val result = preprocessorChain?.process(message.text)

                message.copy(
                    text = result ?: message.text,
                    sendingStatus = message.sendingStatus // Preserve the sending status
                )
            }
            is ImageMessage -> message
            is MultiModalMessage -> message

            else -> message
        }
    }
    
    private fun ensurePendingStatus(message: Message): Message {
        return when (message) {
            is TextMessage -> message.copy(sendingStatus = MessageSendingStatus.PENDING)
            is ImageMessage -> message.copy(sendingStatus = MessageSendingStatus.PENDING)
            is MultiModalMessage -> message.copy(sendingStatus = MessageSendingStatus.PENDING)
            else -> message
        }
    }
}