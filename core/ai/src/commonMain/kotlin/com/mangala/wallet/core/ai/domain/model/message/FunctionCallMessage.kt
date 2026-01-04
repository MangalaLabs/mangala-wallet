package com.mangala.wallet.core.ai.domain.model.message

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * Message representing a function call made by the AI
 */
data class FunctionCallMessage(
    override val id: String,
    override val senderId: String,
    override val isFromUser: Boolean = false,
    override val parentMessageId: String? = null,
    override val timestamp: Instant = Clock.System.now(),
    override val sendingStatus: MessageSendingStatus = MessageSendingStatus.SENT,
    val functionName: String,
    val parameters: Map<String, Any?>,
    val explanation: String? = null,
    val callId: String?
) : Message {
    override fun getContentType(): String = "function_call"

    override fun serializeJson(): String {
        val jsonObject = buildJsonObject {
            put("functionName", JsonPrimitive(functionName))
            put("parameters", JsonObject(parameters.mapValues {
                JsonPrimitive(it.value?.toString() ?: "")
            }))
            explanation?.let { put("explanation", JsonPrimitive(it)) }
            callId?.let { put("callId", JsonPrimitive(it)) }
        }
        return jsonObject.toString()
    }

    override fun deserializeJson(json: String): Message {
        TODO("Not yet implemented")
    }
}