package com.mangala.wallet.core.ai.data.remote

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult

internal sealed class RemoteMessage {
    abstract val role: String

    sealed class Content {
        data class Text(val text: String) : Content()

        data class FileData(
            val mimeType: String,
            val fileUri: String
        ) : Content()

        data class InlineData(
            val mimeType: String,
            val data: String // Base64 encoded data
        ) : Content()
    }

    data class UserMessage(
        val text: String? = null,
        val contents: List<Content> = emptyList()
    ) : RemoteMessage() {
        override val role: String = "user"

        companion object {
            fun text(text: String): UserMessage = UserMessage(text = text)

            fun withFile(text: String?, mimeType: String, fileUri: String): UserMessage {
                return UserMessage(
                    text = text,
                    contents = listOf(Content.FileData(mimeType, fileUri))
                )
            }

            fun multimodal(vararg contents: Content): UserMessage {
                val textContent = contents.filterIsInstance<Content.Text>().firstOrNull()
                return UserMessage(
                    text = textContent?.text,
                    contents = contents.toList()
                )
            }
        }
    }

    data class AssistantMessage(
        val content: String
    ) : RemoteMessage() {
        override val role: String = "model"
    }

    data class FunctionCallMessage(
        val name: String,
        val parameters: Map<String, Any?>,
        val callId: String? = null // Serves as identifier for OpenAI function calls
    ) : RemoteMessage() {
        override val role: String = "assistant"
    }

    data class FunctionResultMessage(
        val name: String,
        val result: FunctionResult,
        val callId: String? = null // Serves as identifier for OpenAI function calls
    ) : RemoteMessage() {
        override val role: String = "user"
    }

    data class SystemMessage(
        val content: String
    ) : RemoteMessage() {
        override val role: String = "system"
    }
}