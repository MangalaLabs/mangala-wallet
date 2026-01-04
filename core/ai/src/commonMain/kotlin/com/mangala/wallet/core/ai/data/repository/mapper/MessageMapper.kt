package com.mangala.wallet.core.ai.data.repository.mapper

import com.mangala.wallet.core.ai.data.remote.RemoteMessage
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallMessage
import com.mangala.wallet.core.ai.domain.model.message.FunctionResultMessage
import com.mangala.wallet.core.ai.domain.model.message.ImageMessage
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.MultiModalMessage
import com.mangala.wallet.core.ai.domain.model.message.SystemMessage
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal fun List<Message>.toAiMessages(): List<RemoteMessage> {
    return mapNotNull { it.toAiMessage() }
}

private fun Message.toAiMessage(): RemoteMessage? {
    return when (this) {
        is TextMessage -> {
            if (isFromUser) {
                RemoteMessage.UserMessage(text = text)
            } else {
                RemoteMessage.AssistantMessage(content = text)
            }
        }
        is ImageMessage -> {
            if (isFromUser) {
                // Create a multimodal message with file data
                val base64Data = imageDataToBase64()
                val fileData = RemoteMessage.Content.FileData(
                    mimeType = mimeType,
                    fileUri = "data:$mimeType;base64,$base64Data"
                )
                RemoteMessage.UserMessage(
                    text = null,
                    contents = listOf(fileData)
                )
            } else {
                RemoteMessage.AssistantMessage(content = "[Image data]")
            }
        }
        is MultiModalMessage -> {
            if (isFromUser) {
                // Extract text and images into separate content items
                val contents = mutableListOf<RemoteMessage.Content>()
                var textContent: String? = null

                messages.forEach { message ->
                    when (message) {
                        is TextMessage -> {
                            textContent = message.text
                            contents.add(RemoteMessage.Content.Text(message.text))
                        }
                        is ImageMessage -> {
                            val base64Data = message.imageDataToBase64()
                            contents.add(
                                RemoteMessage.Content.InlineData(
                                    mimeType = message.mimeType,
                                    data = base64Data
                                )
                            )
                        }
                        // Other message types would be handled here
                    }
                }

                RemoteMessage.UserMessage(
                    text = textContent,
                    contents = contents
                )
            } else {
                // For assistant messages, we'll simplify to text for now
                val combinedContent = messages.joinToString("\n") {
                    when (val msg = it.toAiMessage()) {
                        is RemoteMessage.UserMessage -> msg.text ?: "[Complex content]"
                        is RemoteMessage.AssistantMessage -> msg.content
                        else -> "[Complex content]"
                    }
                }
                RemoteMessage.AssistantMessage(content = combinedContent)
            }
        }
        is FunctionCallMessage -> {
            // Function calls come from the assistant (AI) side
            RemoteMessage.FunctionCallMessage(
                name = functionName,
                parameters = parameters,
                callId = callId
            )
        }
        is FunctionResultMessage -> {
            // Function results are from the user side
            RemoteMessage.FunctionResultMessage(
                name = functionName,
                result = result,
                callId = callId
            )
        }
        is FunctionCallConfirmationRequiredMessage -> null // This type should not be sent to the AI
        is SystemMessage -> RemoteMessage.SystemMessage(text)
        else -> {
            // Default to user message with empty content for unsupported types
            RemoteMessage.UserMessage(text = "Unsupported message type in toAiMessage: ${getContentType()}")
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
private fun ImageMessage.imageDataToBase64(): String {
    return Base64.encode(imageData)
}
