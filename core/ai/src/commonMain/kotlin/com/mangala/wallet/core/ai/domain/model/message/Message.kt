package com.mangala.wallet.core.ai.domain.model.message

import kotlinx.datetime.Instant

interface Message {
    val id: String
    val timestamp: Instant
    val senderId: String
    val isFromUser: Boolean
    val parentMessageId: String?
    val sendingStatus: MessageSendingStatus

    // Method to get a content type identifier - determines which renderer to use
    fun getContentType(): String
    fun serializeJson(): String
    fun deserializeJson(json: String): Message
}