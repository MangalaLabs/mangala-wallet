package com.mangala.wallet.core.ai.domain.model.message

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

data class SystemMessage @OptIn(ExperimentalUuidApi::class) constructor(
    override val id: String = Uuid.Companion.random().toString(),
    override val timestamp: Instant = Clock.System.now(),
    override val senderId: String,
    override val isFromUser: Boolean = false,
    override val parentMessageId: String? = null,
    override val sendingStatus: MessageSendingStatus = MessageSendingStatus.SENT,
    val text: String
): Message {
    override fun getContentType(): String = CONTENT_TYPE

    override fun serializeJson(): String {
        val jsonObject = buildJsonObject {
            put("text", JsonPrimitive(text))
        }
        return jsonObject.toString()
    }

    override fun deserializeJson(json: String): Message {
        TODO("Not yet implemented")
    }

    companion object {
        const val CONTENT_TYPE = "system"
    }
}