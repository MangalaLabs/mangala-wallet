package com.mangala.wallet.websocket.chat.queue

import com.benasher44.uuid.uuid4
import com.mangala.wallet.websocket.chat.websocket.MessageQueue
import com.mangala.wallet.websocket.chat.websocket.models.QueuedMessage
import com.mangala.wallet.websocket.chat.websocket.models.QueueStatus
import com.mangala.wallet.websocket.chat.websocket.models.ChatFrame
import com.mangala.wallet.websocket.chat.persistence.MessageRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class PersistentMessageQueue(
    private val repository: MessageRepository,
    private val conversationId: String,
    private val senderId: String,
    private val recipientId: String,
    private val maxRetries: Int = 3,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) : MessageQueue {
    
    private val tag = "PersistentMessageQueue"
    private val json = Json { ignoreUnknownKeys = true }
    
    init {
        // Load pending messages on initialization
        coroutineScope.launch {
            loadPendingMessages()
        }
        
        // Periodically clean up expired messages
        coroutineScope.launch {
            cleanupExpiredMessages()
        }
    }
    
    override suspend fun enqueue(message: QueuedMessage) {
        try {
            // Save to persistent storage
            repository.saveMessage(
                id = message.id,
                conversationId = conversationId,
                senderId = senderId,
                recipientId = recipientId,
                content = json.encodeToString(message.message),
                timestamp = message.enqueuedAt,
                status = message.status.name,
                priority = "NORMAL",
                retryCount = message.retryCount,
                expiresAt = null,
                metadata = null
            )
            
            Napier.d("Message ${message.id} enqueued", tag = tag)
        } catch (e: Exception) {
            Napier.e("Failed to enqueue message ${message.id}", e, tag = tag)
            throw e
        }
    }
    
    override suspend fun dequeue(): QueuedMessage? {
        // Get the oldest pending message from the repository
        val pendingList = repository.getPendingMessages()
        val message = pendingList.firstOrNull() ?: return null
        
        // Update status to SENDING
        repository.updateMessageStatus(message.id, QueueStatus.SENDING.name)
        
        return toQueuedMessage(message)
    }
    
    override suspend fun markAsDelivered(messageId: String) {
        repository.updateMessageStatus(messageId, QueueStatus.DELIVERED.name)
        Napier.d("Message $messageId marked as delivered", tag = tag)
    }
    
    override suspend fun markAsFailed(messageId: String) {
        val message = repository.getMessage(messageId)
        if (message != null) {
            repository.incrementRetryCount(messageId)
            if (message.retryCount + 1 >= maxRetries) {
                repository.updateMessageStatus(messageId, QueueStatus.FAILED.name)
                Napier.d("Message $messageId marked as failed after ${message.retryCount + 1} retries", tag = tag)
            } else {
                repository.updateMessageStatus(messageId, QueueStatus.PENDING.name)
                Napier.d("Message $messageId will be retried (attempt ${message.retryCount + 2}/$maxRetries)", tag = tag)
            }
        }
    }
    
    override suspend fun getFailedMessages(): List<QueuedMessage> {
        return repository.getFailedMessages(maxRetries)
            .mapNotNull { toQueuedMessage(it) }
    }
    
    override suspend fun getPendingMessages(): List<QueuedMessage> {
        return repository.getPendingMessages()
            .mapNotNull { toQueuedMessage(it) }
    }
    
    override suspend fun clear() {
        repository.deleteByConversationId(conversationId)
        Napier.d("Cleared all messages for conversation $conversationId", tag = tag)
    }
    
    override suspend fun size(): Int {
        return repository.getPendingMessages().size
    }
    
    private suspend fun loadPendingMessages() {
        try {
            val messages = repository.getPendingMessages()
                .filter { it.conversationId == conversationId }
            
            Napier.d("Loaded ${messages.size} pending messages for conversation $conversationId", tag = tag)
        } catch (e: Exception) {
            Napier.e("Failed to load pending messages", e, tag = tag)
        }
    }
    
    private suspend fun cleanupExpiredMessages() {
        while (true) {
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                repository.deleteExpiredMessages(now)
            } catch (e: Exception) {
                Napier.e("Failed to cleanup expired messages", e, tag = tag)
            }
            kotlinx.coroutines.delay(60_000) // Run every minute
        }
    }
    
    private fun toQueuedMessage(persistentMessage: com.mangala.wallet.websocket.chat.persistence.PersistentMessage): QueuedMessage? {
        return try {
            val chatMessage = json.decodeFromString<ChatFrame.Message>(persistentMessage.content)
            QueuedMessage(
                id = persistentMessage.id,
                message = chatMessage,
                retryCount = persistentMessage.retryCount,
                maxRetries = maxRetries,
                enqueuedAt = persistentMessage.timestamp,
                lastAttemptAt = persistentMessage.updatedAt.takeIf { it > 0 },
                status = QueueStatus.valueOf(persistentMessage.status)
            )
        } catch (e: Exception) {
            Napier.e("Failed to deserialize message ${persistentMessage.id}", e, tag = tag)
            null
        }
    }
}