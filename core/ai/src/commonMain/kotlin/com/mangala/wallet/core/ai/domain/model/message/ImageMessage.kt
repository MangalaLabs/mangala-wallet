package com.mangala.wallet.core.ai.domain.model.message

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class ImageMessage @OptIn(ExperimentalUuidApi::class) constructor(
    override val id: String = Uuid.random().toString(),
    override val timestamp: Instant = Clock.System.now(),
    override val senderId: String,
    override val isFromUser: Boolean,
    override val parentMessageId: String? = null,
    override val sendingStatus: MessageSendingStatus = MessageSendingStatus.SENT,
    val imageData: ByteArray,
    val mimeType: String
) : Message {
    override fun getContentType(): String = CONTENT_TYPE

    @OptIn(ExperimentalEncodingApi::class)
    override fun serializeJson(): String {
        val jsonObject = buildJsonObject {
            put("imageData", JsonPrimitive(Base64.encode(imageData)))
            put("mimeType", JsonPrimitive(mimeType))
        }
        return jsonObject.toString()
    }

    override fun deserializeJson(json: String): Message {
        TODO("Not yet implemented")
    }

    companion object {
        const val CONTENT_TYPE = "image"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ImageMessage

        if (id != other.id) return false
        if (timestamp != other.timestamp) return false
        if (senderId != other.senderId) return false
        if (isFromUser != other.isFromUser) return false
        if (parentMessageId != other.parentMessageId) return false
        if (sendingStatus != other.sendingStatus) return false
        if (!imageData.contentEquals(other.imageData)) return false
        if (mimeType != other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + senderId.hashCode()
        result = 31 * result + isFromUser.hashCode()
        result = 31 * result + (parentMessageId?.hashCode() ?: 0)
        result = 31 * result + sendingStatus.hashCode()
        result = 31 * result + imageData.contentHashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }
}