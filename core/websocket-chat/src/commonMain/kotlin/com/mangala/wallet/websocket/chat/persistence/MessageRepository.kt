package com.mangala.wallet.websocket.chat.persistence

import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun saveMessage(
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
    )
    
    suspend fun getMessage(id: String): PersistentMessage?
    
    suspend fun getConversationMessages(
        conversationId: String,
        limit: Int = 50,
        offset: Int = 0
    ): List<PersistentMessage>
    
    suspend fun getPendingMessages(): List<PersistentMessage>
    suspend fun getFailedMessages(maxRetries: Int): List<PersistentMessage>
    suspend fun updateMessageStatus(id: String, status: String)
    suspend fun incrementRetryCount(id: String)
    suspend fun deleteMessage(id: String)
    suspend fun deleteByConversationId(conversationId: String)
    suspend fun deleteExpiredMessages(currentTime: Long)
    suspend fun getMessageCount(conversationId: String): Long
    
    fun observeConversationMessages(conversationId: String): Flow<List<PersistentMessage>>
    fun observePendingMessages(): Flow<List<PersistentMessage>>
    
    suspend fun clearAllMessages()
}

data class PersistentMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: Long,
    val status: String,
    val priority: String,
    val expiresAt: Long? = null,
    val retryCount: Int = 0,
    val metadata: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)