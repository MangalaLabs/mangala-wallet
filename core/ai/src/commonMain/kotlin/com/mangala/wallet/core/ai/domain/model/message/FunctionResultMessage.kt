package com.mangala.wallet.core.ai.domain.model.message

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * Message representing the result of a function call execution
 */
data class FunctionResultMessage(
    override val id: String,
    override val senderId: String,
    override val isFromUser: Boolean = true,  // Results are typically from the user side
    override val parentMessageId: String? = null,
    override val timestamp: Instant = Clock.System.now(),
    override val sendingStatus: MessageSendingStatus = MessageSendingStatus.SENT,
    val functionName: String,
    val result: FunctionResult,
    val callId: String?
) : Message {
    override fun getContentType(): String = "function_result"

    override fun serializeJson(): String {
        val jsonObject = buildJsonObject {
            put("functionName", JsonPrimitive(functionName))
            callId?.let { put("callId", JsonPrimitive(it)) }
            
            when (result) {
                is FunctionResult.Success -> {
                    put("resultType", JsonPrimitive("success"))
                    put("data", JsonObject(result.data.mapValues {
                        JsonPrimitive(it.value?.toString() ?: "")
                    }))
                    result.uiHint?.let { hint ->
                        put("uiHint", buildJsonObject {
                            put("type", JsonPrimitive(hint.type))
                            put("renderer", JsonPrimitive(hint.renderer))
                            put("metadata", JsonObject(hint.metadata.mapValues {
                                JsonPrimitive(it.value?.toString() ?: "")
                            }))
                        })
                    }
                }
                is FunctionResult.Error -> {
                    put("resultType", JsonPrimitive("error"))
                    put("errorCode", JsonPrimitive(result.code))
                    put("errorMessage", JsonPrimitive(result.message))
                }
            }
        }
        return jsonObject.toString()
    }

    override fun deserializeJson(json: String): Message {
        TODO("Not yet implemented")
    }
}