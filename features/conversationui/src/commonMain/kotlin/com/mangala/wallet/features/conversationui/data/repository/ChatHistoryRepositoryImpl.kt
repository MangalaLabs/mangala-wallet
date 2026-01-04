package com.mangala.wallet.features.conversationui.data.repository

import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.MessageSendingStatus
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.MultiModalMessage
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import com.mangala.wallet.features.conversationui.domain.model.ConversationSession
import com.mangala.wallet.features.conversationui.data.local.ChatHistoryLocalDataSource
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
class ChatHistoryRepositoryImpl(
    private val chatHistoryLocalDataSource: ChatHistoryLocalDataSource,
) : ChatHistoryRepository {

    
    override fun getSessionFlow(sessionId: String): Flow<ConversationSession> {
        return getSessionWithMessagesFlow(sessionId).flowOn(Dispatchers.Default)
    }

    override suspend fun getSession(sessionId: String): ConversationSession? {
        return chatHistoryLocalDataSource.getSession(sessionId)?.let { session ->
            val messages = chatHistoryLocalDataSource.getMessagesForSession(sessionId)
            // Messages are already in the right format, though we might need to reconstruct multimodal messages
            val processedMessages = reconstructMultiModalMessages(messages)
            session.copy(messages = processedMessages)
        }
    }

    /**
     * Processes a list of messages to reconstruct multimodal messages
     */
    private fun reconstructMultiModalMessages(messages: List<Message>): List<Message> {
        // Create a map of all messages by ID for easy lookup
        val messageMap = messages.associateBy { it.id }

        // Extract standalone and components of multimodal messages
        val multimodalMessages = messages.filterIsInstance<MultiModalMessage>()
        val componentIds = multimodalMessages.flatMap {
            // If it's a MultiModalMessage with submessages, use their IDs
            if (it.messages.isNotEmpty()) {
                it.messages.map { subMessage -> subMessage.id }
            } else {
                // Otherwise, this is not a container message
                emptyList()
            }
        }.toSet()

        // Return only messages that aren't components of other multimodal messages
        return messages.filter { it.id !in componentIds }
    }

    override fun getAllSessions(userId: String): Flow<List<ConversationSession>> {
        return chatHistoryLocalDataSource.getAllSessionsFlow(userId)
            .flowOn(Dispatchers.Default)
    }
    
    override suspend fun createSession(
        userId: String, 
        title: String?, 
        metadata: Map<String, String>
    ): ConversationSession {
        val sessionId = Uuid.random().toString()
        val now = Clock.System.now()
        
        val session = ConversationSession(
            id = sessionId,
            userId = userId,
            startTime = now,
            lastUpdatedTime = now,
            title = title,
            metadata = metadata,
            messages = emptyList()
        )
        
        chatHistoryLocalDataSource.insertSession(session)
        
        return session
    }
    
    override suspend fun saveMessage(sessionId: String, message: Message) {
        chatHistoryLocalDataSource.insertMessage(sessionId, message)

        // If this is a multimodal message, also save its component messages
        if (message is MultiModalMessage) {
            message.messages.forEach {
                chatHistoryLocalDataSource.insertMessage(sessionId, it)
            }
        }

        val session = getSession(sessionId)
            ?: throw IllegalStateException("Session $sessionId not found")

        // Don't add the message to the session's messages list
        // Messages are retrieved from the data source in getSessionWithMessagesFlow
        val updatedSession = session.copy(
            lastUpdatedTime = Clock.System.now()
        )

        chatHistoryLocalDataSource.updateSession(updatedSession)
        
        println("[ChatHistoryRepository] Message saved successfully")
    }

    override suspend fun appendContentToMessage(
        sessionId: String,
        messageId: String,
        content: String
    ) {
        val session = getSession(sessionId)
            ?: throw IllegalStateException("Session $sessionId not found")

        val messageToUpdate = session.messages.find { it.id == messageId }
            ?: throw IllegalArgumentException("Message $messageId not found in session $sessionId")

        if (messageToUpdate is TextMessage) {
            val updatedMessage = messageToUpdate.copy(
                text = content
            )

            chatHistoryLocalDataSource.insertMessage(sessionId, updatedMessage)

            val updatedMessages = session.messages.map {
                if (it.id == messageId) updatedMessage else it
            }
            val updatedSession = session.copy(
                lastUpdatedTime = Clock.System.now(),
                messages = updatedMessages
            )

            chatHistoryLocalDataSource.updateSession(updatedSession)
        } else {
            throw UnsupportedOperationException("Cannot append content to message of type ${messageToUpdate.getContentType()}")
        }
    }

    override suspend fun updateSession(session: ConversationSession) {
        chatHistoryLocalDataSource.updateSession(session)
    }
    
    override suspend fun deleteSession(sessionId: String) {
        chatHistoryLocalDataSource.deleteSession(sessionId)
    }
    
    override suspend fun clearAllSessions(userId: String) {
        chatHistoryLocalDataSource.deleteAllSessions(userId)
    }
    
    override suspend fun markMessageAsLoading(sessionId: String, messageId: String, isLoading: Boolean) {
        val session = getSession(sessionId)
            ?: throw IllegalStateException("Session $sessionId not found")

        val messageToUpdate = session.messages.find { it.id == messageId }
            ?: throw IllegalArgumentException("Message $messageId not found in session $sessionId")

        if (messageToUpdate is FunctionCallConfirmationRequiredMessage) {
            // Create a copy of the message with the loading state
            val updatedMessage = messageToUpdate.copy(
                // Apply loading state - We need to update FunctionCallConfirmationRequiredMessage
                // to add a loading state property, but for now we have to use other mechanism
                // like storing the loading state separately
            )

            // For now, we'll just add a metadata marker to track the message processing state
            // This can be used in the UI to show a loading state
            val updatedSession = session.copy(
                metadata = session.metadata + mapOf("processing_$messageId" to isLoading.toString()),
                lastUpdatedTime = Clock.System.now()
            )

            chatHistoryLocalDataSource.updateSession(updatedSession)
        }
    }
    
    override suspend fun updateMessageSendingStatus(messageId: String, status: MessageSendingStatus) {
        println("[ChatHistoryRepository] Updating message $messageId status to $status")
        chatHistoryLocalDataSource.updateMessageSendingStatus(messageId, status)
        
        // Force a refresh by updating the session's lastUpdatedTime
        // This ensures the Flow emits the updated messages
        val allSessions = chatHistoryLocalDataSource.getAllSessions("default_user")
        for (session in allSessions) {
            val messages = chatHistoryLocalDataSource.getMessagesForSession(session.id)
            if (messages.any { it.id == messageId }) {
                val updatedSession = session.copy(lastUpdatedTime = Clock.System.now())
                chatHistoryLocalDataSource.updateSession(updatedSession)
                println("[ChatHistoryRepository] Updated session ${session.id} to trigger Flow refresh")
                break
            }
        }
    }
    
    override suspend fun updateMessageExecutionStatus(messageId: String, status: ExecutionStatus) {
        chatHistoryLocalDataSource.updateFunctionCallExecutionStatus(
            messageId = messageId,
            executionStatus = status,
            transactionHash = null,
            executionMetadata = null
        )
        
        // Find the session containing this message to trigger Flow refresh
        val allSessions = chatHistoryLocalDataSource.getAllSessions("default_user")
        for (session in allSessions) {
            val messages = chatHistoryLocalDataSource.getMessagesForSession(session.id)
            if (messages.any { it.id == messageId }) {
                val updatedSession = session.copy(lastUpdatedTime = Clock.System.now())
                chatHistoryLocalDataSource.updateSession(updatedSession)
                break
            }
        }
    }
    
    override suspend fun updateSessionMetadata(sessionId: String, metadata: Map<String, String>) {
        val session = getSession(sessionId)
            ?: throw IllegalStateException("Session $sessionId not found")
        
        val updatedSession = session.copy(
            metadata = metadata,
            lastUpdatedTime = Clock.System.now()
        )
        
        chatHistoryLocalDataSource.updateSession(updatedSession)
    }
    
    override suspend fun clearAllConversationHistory(): Result<Unit> {
        return try {
            chatHistoryLocalDataSource.clearAllConversationHistory()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getSessionWithMessagesFlow(sessionId: String): Flow<ConversationSession> {
        return chatHistoryLocalDataSource.getSessionFlow(sessionId)
            .flatMapLatest { session ->
                if (session == null) {
                    flow { throw IllegalStateException("Session $sessionId not found") }
                } else {
                    chatHistoryLocalDataSource.getMessagesForSessionFlow(sessionId)
                        .map { messages ->
                            val processedMessages = reconstructMultiModalMessages(messages)
                            session.copy(messages = processedMessages)
                        }
                }
            }
    }
}