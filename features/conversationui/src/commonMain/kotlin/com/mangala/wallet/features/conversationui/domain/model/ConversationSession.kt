package com.mangala.wallet.features.conversationui.domain.model

import com.mangala.wallet.core.ai.domain.model.message.Message
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Represents a conversation session between a user and the AI
 *
 * @property id Unique identifier for the conversation session
 * @property userId Identifier of the user participating in the conversation
 * @property startTime Time when the conversation was started
 * @property lastUpdatedTime Time when the conversation was last updated
 * @property title Optional title for the conversation
 * @property metadata Additional metadata for the conversation
 * @property messages List of messages in the conversation
 */
data class ConversationSession(
    val id: String,
    val userId: String,
    val startTime: Instant,
    val lastUpdatedTime: Instant,
    val title: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val messages: List<Message> = emptyList()
) {
    /**
     * Create a new conversation session with updated messages
     *
     * @param newMessages The new messages to add
     * @return A new ConversationSession with updated messages and lastUpdatedTime
     */
    fun withMessages(newMessages: List<Message>): ConversationSession {
        return copy(
            messages = newMessages,
            lastUpdatedTime = Clock.System.now()
        )
    }

    /**
     * Add a message to the conversation
     *
     * @param message The message to add
     * @return A new ConversationSession with the added message and updated lastUpdatedTime
     */
    fun addMessage(message: Message): ConversationSession {
        return withMessages(messages + message)
    }

    /**
     * Update the conversation title
     *
     * @param newTitle The new title
     * @return A new ConversationSession with the updated title and lastUpdatedTime
     */
    fun withTitle(newTitle: String?): ConversationSession {
        return copy(
            title = newTitle,
            lastUpdatedTime = Clock.System.now()
        )
    }

    /**
     * Add metadata to the conversation
     *
     * @param key The metadata key
     * @param value The metadata value
     * @return A new ConversationSession with the added metadata and updated lastUpdatedTime
     */
    fun addMetadata(key: String, value: String): ConversationSession {
        return copy(
            metadata = metadata + (key to value),
            lastUpdatedTime = Clock.System.now()
        )
    }
}