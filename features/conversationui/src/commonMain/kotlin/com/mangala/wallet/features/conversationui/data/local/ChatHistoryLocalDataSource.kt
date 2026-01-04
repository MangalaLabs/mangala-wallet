package com.mangala.wallet.features.conversationui.data.local

import com.mangala.wallet.core.ai.domain.model.message.ExecutionMetadata
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.MessageSendingStatus
import com.mangala.wallet.features.conversationui.domain.model.ConversationSession
import kotlinx.coroutines.flow.Flow

interface ChatHistoryLocalDataSource {

    suspend fun getSession(sessionId: String): ConversationSession?

    fun getSessionFlow(sessionId: String): Flow<ConversationSession?>

    suspend fun getAllSessions(userId: String): List<ConversationSession>

    fun getAllSessionsFlow(userId: String): Flow<List<ConversationSession>>

    suspend fun insertSession(session: ConversationSession)

    suspend fun updateSession(session: ConversationSession)

    suspend fun insertMessage(sessionId: String, message: Message)

    suspend fun getMessagesForSession(sessionId: String): List<Message>

    fun getMessagesForSessionFlow(sessionId: String): Flow<List<Message>>

    suspend fun deleteSession(sessionId: String)

    suspend fun deleteAllSessions(userId: String)
    
    suspend fun updateMessageSendingStatus(messageId: String, status: MessageSendingStatus)
    
    // Function call tracking methods
    suspend fun updateFunctionCallExecutionStatus(
        messageId: String,
        executionStatus: ExecutionStatus,
        transactionHash: String? = null,
        executionMetadata: ExecutionMetadata? = null
    )
    
    suspend fun getFunctionCallByHash(
        sessionId: String,
        functionHash: String
    ): FunctionCallConfirmationRequiredMessage?
    
    suspend fun hasExecutedFunctionCall(
        sessionId: String,
        functionHash: String
    ): Boolean
    
    suspend fun getRecentFunctionCalls(
        userId: String,
        functionName: String? = null,
        executionStatus: ExecutionStatus? = null,
        limit: Int = 20
    ): List<FunctionCallConfirmationRequiredMessage>
    
    suspend fun clearAllConversationHistory()
}