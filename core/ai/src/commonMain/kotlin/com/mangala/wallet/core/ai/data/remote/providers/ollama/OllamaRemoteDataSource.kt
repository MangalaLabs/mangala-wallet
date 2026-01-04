package com.mangala.wallet.core.ai.data.remote.providers.ollama

import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.data.remote.AiRemoteDataSource
import com.mangala.wallet.core.ai.data.remote.RemoteMessage
import com.mangala.wallet.core.ai.data.remote.asFlow
import com.mangala.wallet.core.ai.data.remote.providers.ollama.request.OllamaChatRequest
import com.mangala.wallet.core.ai.data.remote.providers.ollama.response.OllamaChatResponse
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

internal class OllamaRemoteDataSource(
    private val ollamaApi: OllamaApi,
    private val functionRegistry: FunctionRegistry,
): AiRemoteDataSource {
    override suspend fun processMessage(
        userId: String,
        conversationContext: List<RemoteMessage>,
        isMultiModalEnabled: Boolean
    ): Flow<AIResponse> {
        val streamRequest = OllamaChatRequest(
            model = GEMMA_3_12b_TOOLS,
            messages = conversationContext.toOllamaChatRequestMessages(),
            stream = true,
            tools = emptyList()
        )

        return ollamaApi.chatStream(streamRequest).asFlow<OllamaChatResponse>(Json).map {
            AIResponse.TextResponse(
                text = it.message.content
            )
        }
    }

    override fun prepareFunctionDefinitions(functions: List<FunctionDefinition>): Any {
        TODO("Not yet implemented")
    }

    companion object {
        const val GEMMA_3_12b_TOOLS = "PetrosStav/gemma3-tools:12b"
        const val GEMMA_3_27b_TOOLS = "PetrosStav/gemma3-tools:27b"
    }
}

private fun List<RemoteMessage>.toOllamaChatRequestMessages(): List<OllamaChatRequest.Message> {
    return map { message ->
        when (message) {
            is RemoteMessage.UserMessage -> {
                // For Ollama, we can only send text content, so build a text representation
                // If there are multiple content items, we'll append them all as text
                val textContent = if (message.contents.isEmpty()) {
                    message.text ?: ""
                } else {
                    buildString {
                        // Start with any text content
                        val textParts = message.contents.filterIsInstance<RemoteMessage.Content.Text>()
                        if (textParts.isNotEmpty()) {
                            textParts.forEach { append(it.text) }
                        } else if (message.text != null) {
                            append(message.text)
                        }

                        // Mention file attachments
                        val fileParts = message.contents.filterIsInstance<RemoteMessage.Content.FileData>()
                        if (fileParts.isNotEmpty()) {
                            if (isNotEmpty()) append("\n\n")
                            append("[Attached files: ")
                            append(fileParts.joinToString(", ") { "${it.mimeType} file" })
                            append("]")
                        }
                    }
                }

                OllamaChatRequest.Message(
                    role = message.role,
                    content = textContent
                )
            }
            is RemoteMessage.AssistantMessage -> OllamaChatRequest.Message(
                role = message.role,
                content = message.content
            )
            is RemoteMessage.SystemMessage -> OllamaChatRequest.Message(
                role = message.role,
                content = message.content
            )
            is RemoteMessage.FunctionCallMessage -> OllamaChatRequest.Message(
                role = message.role,
                content = "Function call: ${message.name}"
            )
            is RemoteMessage.FunctionResultMessage -> OllamaChatRequest.Message(
                role = message.role,
                content = "Function result: ${message.result}"
            )
        }
    }
}