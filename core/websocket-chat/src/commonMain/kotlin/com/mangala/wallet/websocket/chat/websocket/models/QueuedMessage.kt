package com.mangala.wallet.websocket.chat.websocket.models

import kotlinx.serialization.Serializable

@Serializable
data class QueuedMessage(
    val id: String,
    val message: ChatFrame.Message,
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val enqueuedAt: Long,
    val lastAttemptAt: Long? = null,
    val status: QueueStatus = QueueStatus.PENDING
)

@Serializable
enum class QueueStatus {
    PENDING,
    SENDING,
    DELIVERED,
    FAILED
}