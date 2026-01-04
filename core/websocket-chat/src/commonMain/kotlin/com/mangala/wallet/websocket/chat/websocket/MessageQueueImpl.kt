package com.mangala.wallet.websocket.chat.websocket

import io.github.aakira.napier.Napier
import com.mangala.wallet.websocket.chat.websocket.models.QueueStatus
import com.mangala.wallet.websocket.chat.websocket.models.QueuedMessage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

class MessageQueueImpl : MessageQueue {
    
    private val queue = mutableListOf<QueuedMessage>()
    private val mutex = Mutex()
    
    override suspend fun enqueue(message: QueuedMessage) = mutex.withLock {
        queue.add(message)
        Napier.d("Message enqueued: ${message.id}, queue size: ${queue.size}", tag = "MessageQueueImpl")
    }
    
    override suspend fun dequeue(): QueuedMessage? = mutex.withLock {
        val message = queue.firstOrNull { it.status == QueueStatus.PENDING }
        if (message != null) {
            queue.remove(message)
            Napier.d("Message dequeued: ${message.id}, remaining: ${queue.size}", tag = "MessageQueueImpl")
        }
        message
    }
    
    override suspend fun markAsDelivered(messageId: String) = mutex.withLock {
        val index = queue.indexOfFirst { it.id == messageId || it.message.id == messageId }
        if (index != -1) {
            queue[index] = queue[index].copy(
                status = QueueStatus.DELIVERED,
                lastAttemptAt = Clock.System.now().toEpochMilliseconds()
            )
            Napier.d("Message marked as delivered: $messageId", tag = "MessageQueueImpl")
            
            // Remove delivered messages to prevent queue growth
            queue.removeAll { it.status == QueueStatus.DELIVERED }
        }
    }
    
    override suspend fun markAsFailed(messageId: String) = mutex.withLock {
        val index = queue.indexOfFirst { it.id == messageId || it.message.id == messageId }
        if (index != -1) {
            val message = queue[index]
            queue[index] = message.copy(
                status = if (message.retryCount >= message.maxRetries) {
                    QueueStatus.FAILED
                } else {
                    QueueStatus.PENDING
                },
                retryCount = message.retryCount + 1,
                lastAttemptAt = Clock.System.now().toEpochMilliseconds()
            )
            Napier.d("Message marked as failed: $messageId, retries: ${message.retryCount + 1}", tag = "MessageQueueImpl")
        }
    }
    
    override suspend fun getFailedMessages(): List<QueuedMessage> = mutex.withLock {
        queue.filter { it.status == QueueStatus.FAILED }.toList()
    }
    
    override suspend fun getPendingMessages(): List<QueuedMessage> = mutex.withLock {
        queue.filter { it.status == QueueStatus.PENDING }.toList()
    }
    
    override suspend fun clear() = mutex.withLock {
        val size = queue.size
        queue.clear()
        Napier.d("Queue cleared, removed $size messages", tag = "MessageQueueImpl")
    }
    
    override suspend fun size(): Int = mutex.withLock {
        queue.size
    }
}