package com.mangala.wallet.core.ai.data.remote.providers.openai.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class OpenAiChatCompletionRequest(
    @SerialName("model")
    val model: String,
    @SerialName("input")
    val input: List<Message>,
    @SerialName("temperature")
    val temperature: Double? = null,
    @SerialName("top_p")
    val topP: Double? = null,
    @SerialName("stream")
    val stream: Boolean? = null,
    @SerialName("max_tokens")
    val maxTokens: Int? = null,
    @SerialName("tools")
    val tools: List<Tool>? = null,
    @SerialName("tool_choice")
    val toolChoice: JsonElement? = null
) {
    @Serializable
    data class Message(
        @SerialName("role")
        val role: String? = null,
        @SerialName("content")
        val content: List<ContentItem>? = null,
        @SerialName("type")
        val type: String? = null,
        @SerialName("arguments")
        val arguments: String? = null,
        @SerialName("name")
        val name: String? = null,
        @SerialName("call_id")
        val toolCallId: String? = null,
        @SerialName("tool_calls")
        val toolCalls: List<ToolCall>? = null,
        @SerialName("output")
        val output: String? = null,
    )
    
    @Serializable
    data class ContentItem(
        @SerialName("type")
        val type: String? = null,
        @SerialName("text")
        val text: String? = null,
        @SerialName("image_url")
        val imageUrl: String? = null
    )

    @Serializable
    data class Tool(
        @SerialName("type")
        val type: String,
        @SerialName("name")
        val name: String,
        @SerialName("description")
        val description: String,
        @SerialName("parameters")
        val parameters: JsonObject
    )

    @Serializable
    data class ToolCall(
        @SerialName("id")
        val id: String,
        @SerialName("type")
        val type: String = "function",
        @SerialName("function")
        val function: Function
    ) {
        @Serializable
        data class Function(
            @SerialName("name")
            val name: String,
            @SerialName("arguments")
            val arguments: String // JSON string of arguments
        )
    }
}