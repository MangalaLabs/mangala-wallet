package com.mangala.wallet.core.ai.data.remote.providers.openai.response

import com.mangala.wallet.core.ai.data.remote.providers.openai.request.OpenAiChatCompletionRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAiChatCompletionResponse(
    @SerialName("id")
    val id: String? = null,
    @SerialName("object")
    val objectType: String? = null,
    @SerialName("created")
    val created: Long? = null,
    @SerialName("model")
    val model: String? = null,
    @SerialName("output")
    val outputs: List<Output>? = null,
    @SerialName("usage")
    val usage: Usage? = null,
    @SerialName("system_fingerprint")
    val systemFingerprint: String? = null
) {
    @Serializable
    data class Output(
        @SerialName("id")
        val id: String,
        @SerialName("type")
        val type: String,
        @SerialName("status")
        val status: String,
        @SerialName("content")
        val content: List<Content>? = null,
        @SerialName("arguments")
        val arguments: String? = null,
        @SerialName("call_id")
        val callId: String? = null,
        @SerialName("name")
        val name: String? = null,
        @SerialName("role")
        val role: String? = null,
    ) {
        @Serializable
        data class Content(
            @SerialName("type")
            val type: String? = null,
            @SerialName("text")
            val text: String? = null,
            @SerialName("tool_calls")
            val toolCalls: List<OpenAiChatCompletionRequest.ToolCall>? = null
        )
    }

    @Serializable
    data class Usage(
        @SerialName("prompt_tokens")
        val promptTokens: Int? = null,
        @SerialName("completion_tokens")
        val completionTokens: Int? = null,
        @SerialName("total_tokens")
        val totalTokens: Int? = null
    )
}