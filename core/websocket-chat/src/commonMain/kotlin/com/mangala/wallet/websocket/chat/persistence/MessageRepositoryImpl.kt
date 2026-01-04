package com.mangala.wallet.websocket.chat.persistence

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.mangala.wallet.websocket.chat.ChatMessage
import com.mangala.wallet.websocket.chat.ChatMessageQueries
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers as KDispatchers

class MessageRepositoryImpl(
    private val queries: ChatMessageQueries,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : MessageRepository {
    
    override suspend fun saveMessage(
        id: String,
        conversationId: String,
        senderId: String,
        recipientId: String,
        content: String,
        timestamp: Long,
        status: String,
        priority: String,
        retryCount: Int,
        expiresAt: Long?,
        metadata: String?
    ) = withContext(KDispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.insert(
            id = id,
            conversationId = conversationId,
            senderId = senderId,
            recipientId = recipientId,
            content = content,
            timestamp = timestamp,
            status = status,
            priority = priority,
            retryCount = retryCount.toLong(),
            expiresAt = expiresAt,
            metadata = metadata,
            createdAt = now,
            updatedAt = now
        )
    }
    
    override suspend fun getMessage(id: String): PersistentMessage? = withContext(KDispatchers.Default) {
        queries.selectById(id).executeAsOneOrNull()?.toPersistentMessage()
    }
    
    override suspend fun getConversationMessages(
        conversationId: String,
        limit: Int,
        offset: Int
    ): List<PersistentMessage> = withContext(KDispatchers.Default) {
        queries.selectByConversationId(conversationId)
            .executeAsList()
            .map { it.toPersistentMessage() }
            .drop(offset)
            .take(limit)
    }
    
    override suspend fun getPendingMessages(): List<PersistentMessage> = withContext(KDispatchers.Default) {
        queries.selectPending()
            .executeAsList()
            .map { it.toPersistentMessage() }
    }
    
    override suspend fun getFailedMessages(maxRetries: Int): List<PersistentMessage> = withContext(KDispatchers.Default) {
        queries.selectFailed(maxRetries.toLong())
            .executeAsList()
            .map { it.toPersistentMessage() }
    }
    
    override suspend fun updateMessageStatus(id: String, status: String) = withContext(KDispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.updateStatus(status, now, id)
    }
    
    override suspend fun incrementRetryCount(id: String) = withContext(KDispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.incrementRetryCount(now, id)
    }
    
    override suspend fun deleteMessage(id: String) = withContext(KDispatchers.Default) {
        queries.deleteById(id)
    }
    
    override suspend fun deleteByConversationId(conversationId: String) = withContext(KDispatchers.Default) {
        queries.deleteByConversationId(conversationId)
    }
    
    override suspend fun deleteExpiredMessages(currentTime: Long) = withContext(KDispatchers.Default) {
        queries.deleteExpired(currentTime)
    }
    
    override suspend fun getMessageCount(conversationId: String): Long = withContext(KDispatchers.Default) {
        queries.selectByConversationId(conversationId).executeAsList().size.toLong()
    }
    
    override fun observeConversationMessages(conversationId: String): Flow<List<PersistentMessage>> {
        return queries.selectByConversationId(conversationId)
            .asFlow()
            .mapToList(KDispatchers.Default)
            .map { messages ->
                messages.map { it.toPersistentMessage() }
            }
    }
    
    override fun observePendingMessages(): Flow<List<PersistentMessage>> {
        return queries.selectPending()
            .asFlow()
            .mapToList(KDispatchers.Default)
            .map { messages ->
                messages.map { it.toPersistentMessage() }
            }
    }
    
    override suspend fun clearAllMessages() = withContext(KDispatchers.IO) {
        queries.clearAllMessages()
    }
    
    private fun ChatMessage.toPersistentMessage(): PersistentMessage {
        return PersistentMessage(
            id = id,
            conversationId = conversationId,
            senderId = senderId,
            recipientId = recipientId,
            content = content,
            timestamp = timestamp,
            status = status,
            priority = priority,
            expiresAt = expiresAt,
            retryCount = retryCount.toInt(),
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}