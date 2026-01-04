package com.mangala.wallet.features.conversationui.data.local.mapper

import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.message.*
import com.mangala.wallet.core.security.models.SecurityLevel
import com.mangala.wallet.features.conversationui.ChatMessage as DbMessage
import com.mangala.wallet.features.addressbook.presentation.message.ContactResultsMessage
import com.mangala.wallet.features.addressbook.domain.model.ContactInfo
import com.mangala.wallet.features.addressbook.domain.model.ContactAddress
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object MessageMapper {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private fun parseMessageSendingStatus(status: String?): MessageSendingStatus {
        return status?.let {
            try {
                MessageSendingStatus.valueOf(it)
            } catch (e: IllegalArgumentException) {
                MessageSendingStatus.SENT // Default to SENT for backward compatibility
            }
        } ?: MessageSendingStatus.SENT // Default to SENT for null values (existing messages)
    }
    
    private fun parseJsonValue(element: JsonElement): Any? {
        return when (element) {
            is JsonPrimitive -> {
                when {
                    element.isString -> element.content
                    element.booleanOrNull != null -> element.boolean
                    element.intOrNull != null -> element.int
                    element.longOrNull != null -> element.long
                    element.doubleOrNull != null -> element.double
                    else -> element.content
                }
            }
            is JsonArray -> {
                element.map { parseJsonValue(it) }
            }
            is JsonObject -> {
                element.mapValues { parseJsonValue(it.value) }
            }
            is JsonNull -> null
        }
    }

    fun DbMessage.toDomainModel(): Message {
        return when (messageType) {
            "text" -> parseTextMessage()
            "function_call" -> parseFunctionCallMessage()
            "function_result" -> parseFunctionResultMessage()
            "image" -> parseImageMessage()
            "system" -> parseSystemMessage()
            "multimodal" -> parseMultiModalMessage()
            "function_call_confirmation_required" -> parseFunctionCallConfirmationMessage()
            "addressbook_contact_results" -> parseContactResultsMessage()
            else -> parseTextMessage()
        }
    }

    fun Message.toDbModel(sessionId: String): DbMessage {
        val content = this.serializeJson()
        val type = this.getContentType()

        return DbMessage(
            id = id,
            sessionId = sessionId,
            messageType = type,
            timestamp = timestamp.toEpochMilliseconds(),
            senderId = senderId,
            isFromUser = isFromUser,
            parentMessageId = parentMessageId,
            content = content,
            sendingStatus = sendingStatus.name
        )
    }

    private fun DbMessage.parseTextMessage(): TextMessage {
        val jsonContent = json.parseToJsonElement(content).jsonObject
        return TextMessage(
            id = id,
            timestamp = Instant.fromEpochMilliseconds(timestamp),
            senderId = senderId,
            isFromUser = isFromUser,
            parentMessageId = parentMessageId,
            sendingStatus = parseMessageSendingStatus(sendingStatus),
            text = jsonContent["text"]?.jsonPrimitive?.content ?: "",
            uiTags = jsonContent["uiTags"]?.jsonArray?.mapNotNull { UiTag.fromString(it.jsonPrimitive.content) }
                ?: emptyList()
        )
    }

    private fun DbMessage.parseFunctionCallMessage(): FunctionCallMessage {
        val jsonContent = json.parseToJsonElement(content).jsonObject
        return FunctionCallMessage(
            id = id,
            timestamp = Instant.fromEpochMilliseconds(timestamp),
            senderId = senderId,
            isFromUser = isFromUser,
            parentMessageId = parentMessageId,
            sendingStatus = parseMessageSendingStatus(sendingStatus),
            functionName = jsonContent["functionName"]?.jsonPrimitive?.content ?: "",
            callId = null,
            parameters = jsonContent["parameters"]?.jsonObject?.mapValues { it.value.jsonPrimitive.content }
                ?: emptyMap(),
//            confirmationRequired = jsonContent["confirmationRequired"]?.jsonPrimitive?.boolean ?: false
        )
    }

    private fun DbMessage.parseFunctionResultMessage(): FunctionResultMessage {
        val jsonContent = json.parseToJsonElement(content).jsonObject
        val resultType = jsonContent["resultType"]?.jsonPrimitive?.content
        
        val result = when (resultType) {
            "success" -> {
                val data = jsonContent["data"]?.jsonObject?.mapValues {
                    it.value.jsonPrimitive.content
                } ?: emptyMap()
                
                val uiHint = jsonContent["uiHint"]?.jsonObject?.let { hintObj ->
                    FunctionResult.UiHint(
                        type = hintObj["type"]?.jsonPrimitive?.content ?: "",
                        renderer = hintObj["renderer"]?.jsonPrimitive?.content ?: "",
                        metadata = hintObj["metadata"]?.jsonObject?.mapValues {
                            it.value.jsonPrimitive.content
                        } ?: emptyMap()
                    )
                }
                
                FunctionResult.Success(data, uiHint)
            }
            "error" -> {
                FunctionResult.Error(
                    code = jsonContent["errorCode"]?.jsonPrimitive?.content ?: "",
                    message = jsonContent["errorMessage"]?.jsonPrimitive?.content ?: ""
                )
            }
            else -> FunctionResult.Success(emptyMap())
        }
        
        return FunctionResultMessage(
            id = id,
            timestamp = Instant.fromEpochMilliseconds(timestamp),
            senderId = senderId,
            isFromUser = isFromUser,
            parentMessageId = parentMessageId,
            sendingStatus = parseMessageSendingStatus(sendingStatus),
            result = result,
            callId = jsonContent["callId"]?.jsonPrimitive?.content,
            functionName = jsonContent["functionName"]?.jsonPrimitive?.content ?: ""
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun DbMessage.parseImageMessage(): ImageMessage {
        val jsonContent = json.parseToJsonElement(content).jsonObject
        val imageDataBase64 = jsonContent["imageData"]?.jsonPrimitive?.content ?: ""
        val imageData = if (imageDataBase64.isNotEmpty()) {
            Base64.decode(imageDataBase64)
        } else {
            byteArrayOf()
        }
        
        return ImageMessage(
            id = id,
            timestamp = Instant.fromEpochMilliseconds(timestamp),
            senderId = senderId,
            isFromUser = isFromUser,
            parentMessageId = parentMessageId,
            sendingStatus = parseMessageSendingStatus(sendingStatus),
            imageData = imageData,
            mimeType = jsonContent["mimeType"]?.jsonPrimitive?.content ?: ""
        )
    }

    private fun DbMessage.parseSystemMessage(): SystemMessage {
        val jsonContent = json.parseToJsonElement(content).jsonObject
        return SystemMessage(
            id = id,
            timestamp = Instant.fromEpochMilliseconds(timestamp),
            senderId = senderId,
            isFromUser = isFromUser,
            parentMessageId = parentMessageId,
            sendingStatus = parseMessageSendingStatus(sendingStatus),
            text = jsonContent["text"]?.jsonPrimitive?.content ?: ""
        )
    }

    private fun DbMessage.parseMultiModalMessage(): MultiModalMessage {
        // For now, just return an empty multimodal message
        // The actual parsing logic would need to handle nested messages
        // which would require recursive deserialization
        return MultiModalMessage(
            id = id,
            timestamp = Instant.fromEpochMilliseconds(timestamp),
            senderId = senderId,
            isFromUser = isFromUser,
            parentMessageId = parentMessageId,
            sendingStatus = parseMessageSendingStatus(sendingStatus),
            messages = emptyList()
        )
    }

    private fun DbMessage.parseFunctionCallConfirmationMessage(): FunctionCallConfirmationRequiredMessage {
        val jsonContent = json.parseToJsonElement(content).jsonObject

        // Parse execution metadata if present
        val executionMetadata = jsonContent["executionMetadata"]?.jsonObject?.let { metadataObj ->
            ExecutionMetadata(
                walletAddress = metadataObj["walletAddress"]?.jsonPrimitive?.content ?: "",
                networkId = metadataObj["networkId"]?.jsonPrimitive?.content,
                gasUsed = metadataObj["gasUsed"]?.jsonPrimitive?.content,
                errorMessage = metadataObj["errorMessage"]?.jsonPrimitive?.content,
                blockNumber = metadataObj["blockNumber"]?.jsonPrimitive?.long
            )
        }

        val functionCallJson = jsonContent["functionCall"]?.jsonObject

        return FunctionCallConfirmationRequiredMessage(
            id = id,
            timestamp = Instant.fromEpochMilliseconds(timestamp),
            senderId = senderId,
            isFromUser = isFromUser,
            parentMessageId = parentMessageId,
            sendingStatus = parseMessageSendingStatus(sendingStatus),
            functionCall = FunctionCallRequest(
                name = functionCallJson?.get("functionName")?.jsonPrimitive?.content ?: "",
                parameters = functionCallJson?.get("parameters")?.jsonObject?.mapValues {
                    parseJsonValue(it.value)
                } ?: emptyMap(),
                securityLevel = SecurityLevel.parseSecurityLevel(
                    functionCallJson?.get("securityLevel")?.jsonPrimitive?.content
                        ?: SecurityLevel.None.name
                ),
                callId = functionCallJson?.get("callId")?.jsonPrimitive?.content
            ),
            confirmationPrompt = jsonContent["confirmationPrompt"]?.jsonPrimitive?.content ?: "",
            functionDescription = jsonContent["functionDescription"]?.jsonPrimitive?.content,

            // Parse new execution tracking fields
            executionStatus = jsonContent["executionStatus"]?.jsonPrimitive?.content?.let {
                try {
                    ExecutionStatus.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    ExecutionStatus.PENDING
                }
            } ?: ExecutionStatus.PENDING,
            transactionHash = jsonContent["transactionHash"]?.jsonPrimitive?.content,
            executedAt = jsonContent["executedAt"]?.jsonPrimitive?.long?.let {
                Instant.fromEpochMilliseconds(it)
            },
            executionMetadata = executionMetadata,
            functionHash = jsonContent["functionHash"]?.jsonPrimitive?.content
        )
    }

    private fun DbMessage.parseContactResultsMessage(): ContactResultsMessage {
        val jsonContent = json.parseToJsonElement(content).jsonObject
        
        // Parse contacts array
        val contacts = jsonContent["contacts"]?.jsonArray?.map { contactElement ->
            val contactObj = contactElement.jsonObject
            ContactInfo(
                id = contactObj["id"]?.jsonPrimitive?.content ?: "",
                name = contactObj["name"]?.jsonPrimitive?.content ?: "",
                notes = contactObj["notes"]?.jsonPrimitive?.content,
                addresses = contactObj["addresses"]?.jsonArray?.map { addressElement ->
                    val addressObj = addressElement.jsonObject
                    ContactAddress(
                        address = addressObj["address"]?.jsonPrimitive?.content ?: "",
                        network = addressObj["network"]?.jsonPrimitive?.content ?: ""
                    )
                } ?: emptyList()
            )
        } ?: emptyList()
        
        return ContactResultsMessage(
            id = id,
            timestamp = Instant.fromEpochMilliseconds(timestamp),
            senderId = senderId,
            isFromUser = isFromUser,
            parentMessageId = parentMessageId,
            query = jsonContent["query"]?.jsonPrimitive?.content ?: "",
            contacts = contacts,
            totalCount = jsonContent["totalCount"]?.jsonPrimitive?.int ?: 0,
            displayText = jsonContent["displayText"]?.jsonPrimitive?.content
        )
    }
}