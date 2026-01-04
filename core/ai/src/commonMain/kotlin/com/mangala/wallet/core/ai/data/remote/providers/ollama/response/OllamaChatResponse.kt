package com.mangala.wallet.core.ai.data.remote.providers.ollama.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
class OllamaChatResponse(
    val model: String,
    @SerialName("created_at")
    val createdAt: String,
    val message: Message,
    val done: Boolean,
    @SerialName("done_reason")
    val doneReason: String? = null,
    @SerialName("total_duration")
    val totalDuration: Long? = null,
    @SerialName("load_duration")
    val loadDuration: Long? = null,
    @SerialName("prompt_eval_duration")
    val promptEvalDuration: Long? = null,
    @SerialName("prompt_eval_count")
    val promptEvalCount: Int? = null,
    @SerialName("eval_count")
    val evalCount: Int? = null,
    @SerialName("eval_duration")
    val evalDuration: Long? = null
) {
    @Serializable
    class Message(
        val role: String,
        val content: String,
        @SerialName("tool_calls")
        val toolCall: List<ToolCall>? = null
    ) {
        @Serializable
        data class ToolCall(
            val function: FunctionCall
        )

        @Serializable
        class FunctionCall(
            val name: String,
            val arguments: JsonObject
        )
    }
}