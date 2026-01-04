package com.mangala.wallet.core.ai.data.remote.providers.ollama.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class OllamaChatRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean,
    val tools: List<Tool>,
    val format: String? = null
) {
    @Serializable
    data class Message(
        val role: String,
        val content: String,
    )

    @Serializable
    data class Tool(
        val type: String,
        val function: FunctionDefinition,
    )

    @Serializable
    data class FunctionDefinition(
        val name: String,
        val description: String,
        val parameters: ParameterDefinition
    )

    @Serializable
    data class ParameterDefinition(
        val type: String,
        val properties: JsonObject,
        val required: List<String>
    )
}