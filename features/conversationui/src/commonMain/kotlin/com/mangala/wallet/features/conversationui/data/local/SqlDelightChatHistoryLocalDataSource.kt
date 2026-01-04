package com.mangala.wallet.features.conversationui.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mangala.wallet.core.ai.domain.model.message.ExecutionMetadata
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.MessageSendingStatus
import com.mangala.wallet.features.conversationui.data.local.mapper.MessageMapper.toDbModel
import com.mangala.wallet.features.conversationui.data.local.mapper.MessageMapper.toDomainModel
import com.mangala.wallet.features.conversationui.data.local.mapper.SessionMapper.toDbModel
import com.mangala.wallet.features.conversationui.data.local.mapper.SessionMapper.toDomainModel
import com.mangala.wallet.features.conversationui.database.ConversationUiDatabase
import com.mangala.wallet.features.conversationui.domain.model.ConversationSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class SqlDelightChatHistoryLocalDataSource(
    private val database: ConversationUiDatabase
) : ChatHistoryLocalDataSource {

    private val chatMessageQueries = database.chatMessageQueries
    private val conversationSessionQueries = database.conversationSessionQueries

    override suspend fun getSession(sessionId: String): ConversationSession? = withContext(Dispatchers.IO) {
        val session = conversationSessionQueries.getSessionById(sessionId).executeAsOneOrNull()?.toDomainModel()
        session?.let {
            val messages = getMessagesForSession(sessionId)
            it.copy(messages = messages)
        }
    }

    override fun getSessionFlow(sessionId: String): Flow<ConversationSession?> {
        val sessionFlow = conversationSessionQueries.getSessionById(sessionId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomainModel() }

        val messagesFlow = getMessagesForSessionFlow(sessionId)

        return combine(sessionFlow, messagesFlow) { session, messages ->
            session?.copy(messages = messages)
        }
    }

    override suspend fun getAllSessions(userId: String): List<ConversationSession> = withContext(Dispatchers.IO) {
        conversationSessionQueries.getAllSessionsForUser(userId).executeAsList().map { dbSession ->
            val messages = getMessagesForSession(dbSession.id)
            dbSession.toDomainModel().copy(messages = messages)
        }
    }

    override fun getAllSessionsFlow(userId: String): Flow<List<ConversationSession>> {
        return conversationSessionQueries.getAllSessionsForUser(userId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbSessions ->
                dbSessions.map { dbSession ->
                    val messages = getMessagesForSession(dbSession.id)
                    dbSession.toDomainModel().copy(messages = messages)
                }
            }
    }

    override suspend fun insertSession(session: ConversationSession) = withContext(Dispatchers.IO) {
        val dbSession = session.toDbModel()

        conversationSessionQueries.insertSession(
            id = dbSession.id,
            userId = dbSession.userId,
            startTime = dbSession.startTime,
            lastUpdatedTime = dbSession.lastUpdatedTime,
            title = dbSession.title,
            metadata = dbSession.metadata
        )
    }

    override suspend fun updateSession(session: ConversationSession) = withContext(Dispatchers.IO) {
        val dbSession = session.toDbModel()

        conversationSessionQueries.updateSession(
            lastUpdatedTime = dbSession.lastUpdatedTime,
            title = dbSession.title,
            metadata = dbSession.metadata,
            id = dbSession.id
        )
    }

    override suspend fun insertMessage(sessionId: String, message: Message) = withContext(Dispatchers.IO) {
        println("[SqlDelightDataSource] Inserting message ${message.id} into session $sessionId")
        val dbMessage = message.toDbModel(sessionId)
        chatMessageQueries.insertMessage(
            id = dbMessage.id,
            sessionId = dbMessage.sessionId,
            messageType = dbMessage.messageType,
            timestamp = dbMessage.timestamp,
            senderId = dbMessage.senderId,
            isFromUser = dbMessage.isFromUser,
            parentMessageId = dbMessage.parentMessageId,
            content = dbMessage.content,
            sendingStatus = dbMessage.sendingStatus
        )
        
        // Update session's last updated time
        conversationSessionQueries.getSessionById(sessionId).executeAsOneOrNull()?.let { session ->
            conversationSessionQueries.updateSession(
                lastUpdatedTime = Clock.System.now().toEpochMilliseconds(),
                title = session.title,
                metadata = session.metadata,
                id = session.id
            )
        }
        println("[SqlDelightDataSource] Message inserted successfully")
    }

    override suspend fun getMessagesForSession(sessionId: String): List<Message> = withContext(Dispatchers.IO) {
        chatMessageQueries.getMessagesForSession(sessionId).executeAsList().map { it.toDomainModel() }
    }

    override fun getMessagesForSessionFlow(sessionId: String): Flow<List<Message>> {
        return chatMessageQueries.getMessagesForSession(sessionId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbMessages ->
                dbMessages.map { it.toDomainModel() }
            }
    }

    override suspend fun deleteSession(sessionId: String) = withContext(Dispatchers.IO) {
        println("[SqlDelightDataSource] Deleting session: $sessionId")
        conversationSessionQueries.transaction {
            // Delete all messages for this session first (even though CASCADE should handle this)
            println("[SqlDelightDataSource] Deleting messages for session")
            chatMessageQueries.deleteMessagesForSession(sessionId)
            
            // Then delete the session
            println("[SqlDelightDataSource] Deleting session record")
            conversationSessionQueries.deleteSession(sessionId)
            
            // Verify deletion
            val verifySession = conversationSessionQueries.getSessionById(sessionId).executeAsOneOrNull()
            println("[SqlDelightDataSource] Session after delete: ${verifySession != null}")
        }
        println("[SqlDelightDataSource] Delete session completed")
    }

    override suspend fun deleteAllSessions(userId: String) = withContext(Dispatchers.IO) {
        conversationSessionQueries.deleteAllSessionsForUser(userId)
    }
    
    override suspend fun updateMessageSendingStatus(messageId: String, status: MessageSendingStatus) = withContext(Dispatchers.IO) {
        chatMessageQueries.updateMessageSendingStatus(status.name, messageId)
    }
    
    override suspend fun updateFunctionCallExecutionStatus(
        messageId: String,
        executionStatus: ExecutionStatus,
        transactionHash: String?,
        executionMetadata: ExecutionMetadata?
    ) = withContext(Dispatchers.IO) {
        // Get the existing message
        val existingMessage = chatMessageQueries.getMessageById(messageId).executeAsOneOrNull()
        if (existingMessage != null) {
            // Parse the existing message to FunctionCallConfirmationRequiredMessage
            val message = existingMessage.toDomainModel() as? FunctionCallConfirmationRequiredMessage
            if (message != null) {
                // Update the message with new execution data
                val updatedMessage = message.copy(
                    executionStatus = executionStatus,
                    transactionHash = transactionHash ?: message.transactionHash,
                    executedAt = if (executionStatus == ExecutionStatus.EXECUTED) Clock.System.now() else message.executedAt,
                    executionMetadata = executionMetadata ?: message.executionMetadata
                )
                
                // Convert back to DB model and update
                val dbMessage = updatedMessage.toDbModel(existingMessage.sessionId)
                chatMessageQueries.updateMessage(
                    content = dbMessage.content,
                    sendingStatus = dbMessage.sendingStatus,
                    id = messageId
                )
            }
        }
    }
    
    override suspend fun getFunctionCallByHash(
        sessionId: String,
        functionHash: String
    ): FunctionCallConfirmationRequiredMessage? = withContext(Dispatchers.IO) {
        // Get all messages for the session
        val messages = chatMessageQueries.getMessagesForSession(sessionId).executeAsList()
        
        // Find function call confirmation messages with matching hash
        messages.firstNotNullOfOrNull { dbMessage ->
            val message = dbMessage.toDomainModel()
            if (message is FunctionCallConfirmationRequiredMessage && message.functionHash == functionHash) {
                message
            } else {
                null
            }
        }
    }
    
    override suspend fun hasExecutedFunctionCall(
        sessionId: String,
        functionHash: String
    ): Boolean = withContext(Dispatchers.IO) {
        val functionCall = getFunctionCallByHash(sessionId, functionHash)
        functionCall?.executionStatus == ExecutionStatus.EXECUTED
    }
    
    override suspend fun getRecentFunctionCalls(
        userId: String,
        functionName: String?,
        executionStatus: ExecutionStatus?,
        limit: Int
    ): List<FunctionCallConfirmationRequiredMessage> = withContext(Dispatchers.IO) {
        // Get all sessions for the user
        val sessions = conversationSessionQueries.getAllSessionsForUser(userId).executeAsList()
        
        // Collect all function call messages across sessions
        val allFunctionCalls = mutableListOf<FunctionCallConfirmationRequiredMessage>()
        
        sessions.forEach { session ->
            val messages = chatMessageQueries.getMessagesForSession(session.id).executeAsList()
            messages.forEach { dbMessage ->
                val message = dbMessage.toDomainModel()
                if (message is FunctionCallConfirmationRequiredMessage) {
                    // Apply filters
                    val matchesName = functionName == null || message.functionCall.name == functionName
                    val matchesStatus = executionStatus == null || message.executionStatus == executionStatus
                    
                    if (matchesName && matchesStatus) {
                        allFunctionCalls.add(message)
                    }
                }
            }
        }
        
        // Sort by timestamp (most recent first) and limit
        allFunctionCalls.sortedByDescending { it.timestamp }.take(limit)
    }
    
    override suspend fun clearAllConversationHistory() = withContext(Dispatchers.IO) {
        conversationSessionQueries.transaction {
            // Clear all chat messages first (though CASCADE should handle this)
            chatMessageQueries.clearAllChatMessages()
            // Clear all conversation sessions
            conversationSessionQueries.clearAllConversationSessions()
        }
    }
}