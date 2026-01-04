package com.mangala.wallet.features.conversationui.domain.util

import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.domain.model.message.ImageMessage
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.MultiModalMessage
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import com.mangala.wallet.core.ai.domain.model.message.TagParser
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun AIResponse.mapAIResponseToMessage(messageId: String): Message? {
    return when (this) {
        is AIResponse.TextResponse -> {
            val parseResult = TagParser.parseMessage(text)
            TextMessage(
                id = messageId,
                isFromUser = false,
                senderId = "",
                text = parseResult.cleanedText,
                uiTags = listOfNotNull(parseResult.uiTag) + this.uiTags
            )
        }

        is AIResponse.FunctionCallResponse -> {
            // This case is handled specially in the main flow,
            // where we create a FunctionCallMessage
            null
        }

        is AIResponse.ConfirmationRequiredResponse -> {
            // We don't create a message here since the confirmation message
            // was already saved in the main flow
            null
        }

        is AIResponse.FunctionResultResponse -> {
            val resultText = "Function result: ${functionName}"
            val parseResult = TagParser.parseMessage(resultText)
            TextMessage(
                id = messageId,
                isFromUser = false,
                senderId = "",
                text = parseResult.cleanedText,
                uiTags = listOfNotNull(parseResult.uiTag)
            )
        }

        is AIResponse.ImageResponse -> {
            ImageMessage(
                id = messageId,
                isFromUser = false,
                senderId = "",
                imageData = imageData,
                mimeType = mimeType
            )
        }

        is AIResponse.MultiModalResponse -> {
            val messageComponents = responses.mapNotNull { response ->
                response.mapAIResponseToMessage(Uuid.random().toString())
            }

            if (messageComponents.isNotEmpty()) {
                MultiModalMessage(
                    id = messageId,
                    isFromUser = false,
                    senderId = "",
                    messages = messageComponents.toMutableList() // Convert to mutable list to ensure proper initialization
                )
            } else {
                val parseResult = TagParser.parseMessage("")
                TextMessage(
                    id = messageId,
                    isFromUser = false,
                    senderId = "",
                    text = parseResult.cleanedText,
                    uiTags = listOfNotNull(parseResult.uiTag)
                )
            }
        }

        is AIResponse.ErrorResponse -> {
            val errorText = "Error: ${error}"
            val parseResult = TagParser.parseMessage(errorText)
            TextMessage(
                id = messageId,
                isFromUser = false,
                senderId = "",
                text = parseResult.cleanedText,
                uiTags = listOfNotNull(parseResult.uiTag)
            )
        }
    }
}