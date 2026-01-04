package com.mangala.wallet.core.ai.domain.model.message

enum class MessageSendingStatus {
    PENDING,    // Message is queued to be sent
    SENDING,    // Message is currently being sent
    SENT,       // Message was successfully sent
    FAILED      // Message failed to send
}