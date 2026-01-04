package com.mangala.wallet.features.conversationui.domain.repository

import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.MessageSendingStatus
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.features.conversationui.domain.model.ConversationSession
import kotlinx.coroutines.flow.Flow

interface ChatHistoryRepository {
    fun getSessionFlow(sessionId: String): Flow<ConversationSession>

    suspend fun getSession(sessionId: String): ConversationSession?
    
    fun getAllSessions(userId: String): Flow<List<ConversationSession>>
    
    suspend fun createSession(
        userId: String,
        title: String? = null, 
        metadata: Map<String, String> = emptyMap()
    ): ConversationSession
    
    suspend fun saveMessage(sessionId: String, message: Message)

    suspend fun appendContentToMessage(sessionId: String, messageId: String, content: String)
    
    suspend fun updateSession(session: ConversationSession)
    
    suspend fun deleteSession(sessionId: String)
    
    suspend fun clearAllSessions(userId: String)
    
    suspend fun markMessageAsLoading(sessionId: String, messageId: String, isLoading: Boolean = true)
    
    suspend fun updateMessageSendingStatus(messageId: String, status: MessageSendingStatus)
    
    suspend fun updateMessageExecutionStatus(messageId: String, status: ExecutionStatus)
    
    suspend fun updateSessionMetadata(sessionId: String, metadata: Map<String, String>)
    
    suspend fun clearAllConversationHistory(): Result<Unit>
}