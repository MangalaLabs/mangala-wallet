package com.mangala.wallet.websocket.chat.websocket

import com.mangala.wallet.websocket.chat.websocket.models.QueuedMessage

interface MessageQueue {
    suspend fun enqueue(message: QueuedMessage)
    suspend fun dequeue(): QueuedMessage?
    suspend fun markAsDelivered(messageId: String)
    suspend fun markAsFailed(messageId: String)
    suspend fun getFailedMessages(): List<QueuedMessage>
    suspend fun getPendingMessages(): List<QueuedMessage>
    suspend fun clear()
    suspend fun size(): Int
}