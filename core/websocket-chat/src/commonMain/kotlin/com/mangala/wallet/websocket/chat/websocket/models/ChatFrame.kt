package com.mangala.wallet.websocket.chat.websocket.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ChatFrame {
    abstract val id: String
    abstract val timestamp: Long
    
    @Serializable
    @SerialName("message")
    data class Message(
        override val id: String,
        override val timestamp: Long,
        val payload: EncryptedPayload,
        val recipientAddress: String,
        val senderAddress: String
    ) : ChatFrame()
    
    @Serializable
    @SerialName("ack")
    data class Acknowledgment(
        override val id: String,
        override val timestamp: Long,
        val messageId: String,
        val status: DeliveryStatus
    ) : ChatFrame()
    
    @Serializable
    @SerialName("heartbeat")
    data class Heartbeat(
        override val id: String,
        override val timestamp: Long
    ) : ChatFrame()
    
    @Serializable
    @SerialName("auth_challenge")
    data class AuthChallenge(
        override val id: String,
        override val timestamp: Long,
        val challenge: String
    ) : ChatFrame()
    
    @Serializable
    @SerialName("auth_response")
    data class AuthResponse(
        override val id: String,
        override val timestamp: Long,
        val signature: String,
        val publicKey: String
    ) : ChatFrame()
    
    @Serializable
    @SerialName("auth_success")
    data class AuthSuccess(
        override val id: String,
        override val timestamp: Long,
        val token: String,
        val expiresAt: Long
    ) : ChatFrame()
    
    @Serializable
    @SerialName("error")
    data class Error(
        override val id: String,
        override val timestamp: Long,
        val code: String,
        val message: String
    ) : ChatFrame()
}

@Serializable
data class EncryptedPayload(
    val data: ByteArray,
    val algorithm: String = "AES-256-GCM"
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EncryptedPayload

        if (!data.contentEquals(other.data)) return false
        if (algorithm != other.algorithm) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + algorithm.hashCode()
        return result
    }
}

@Serializable
enum class DeliveryStatus {
    @SerialName("delivered")
    DELIVERED,
    
    @SerialName("failed")
    FAILED,
    
    @SerialName("pending")
    PENDING
}