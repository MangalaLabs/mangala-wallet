package com.mangala.wallet.core.ai.domain.model.message

import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

enum class ExecutionStatus {
    PENDING,      // Awaiting user confirmation
    CONFIRMED,    // User confirmed, execution in progress
    EXECUTED,     // Successfully executed on chain
    FAILED,       // Execution failed
    CANCELLED,    // User cancelled
    EXPIRED       // Confirmation expired
}

data class ExecutionMetadata(
    val walletAddress: String,
    val networkId: String?,
    val gasUsed: String?,
    val errorMessage: String?,
    val blockNumber: Long?
)

data class FunctionCallConfirmationRequiredMessage(
    override val id: String,
    override val senderId: String,
    override val isFromUser: Boolean = false,
    override val parentMessageId: String? = null,
    override val timestamp: Instant = Clock.System.now(),
    override val sendingStatus: MessageSendingStatus = MessageSendingStatus.SENT,
    val functionCall: FunctionCallRequest,
    val confirmationPrompt: String,
    val functionDescription: String? = null,
    val executionStatus: ExecutionStatus = ExecutionStatus.PENDING,
    val transactionHash: String? = null,
    val executedAt: Instant? = null,
    val executionMetadata: ExecutionMetadata? = null,
    val functionHash: String? = null // Unique identifier for deduplication
) : Message {
    override fun getContentType(): String = "function_call_confirmation_required"

    override fun serializeJson(): String {
        val jsonObject = buildJsonObject {
            put("confirmationPrompt", JsonPrimitive(confirmationPrompt))
            functionDescription?.let { put("functionDescription", JsonPrimitive(it)) }
            put("functionCall", buildJsonObject {
                put("functionName", JsonPrimitive(functionCall.name))
                put("parameters", JsonObject(
                    functionCall.parameters.mapValues {
                        JsonPrimitive(it.value?.toString() ?: "")
                    }
                ))
                put("securityLevel", JsonPrimitive(functionCall.securityLevel.name))
                functionCall.callId?.let { put("callId", JsonPrimitive(it)) }
            })
            
            // Serialize new execution tracking fields
            put("executionStatus", JsonPrimitive(executionStatus.name))
            transactionHash?.let { put("transactionHash", JsonPrimitive(it)) }
            executedAt?.let { put("executedAt", JsonPrimitive(it.toEpochMilliseconds())) }
            functionHash?.let { put("functionHash", JsonPrimitive(it)) }
            
            executionMetadata?.let { metadata ->
                put("executionMetadata", buildJsonObject {
                    put("walletAddress", JsonPrimitive(metadata.walletAddress))
                    metadata.networkId?.let { put("networkId", JsonPrimitive(it)) }
                    metadata.gasUsed?.let { put("gasUsed", JsonPrimitive(it)) }
                    metadata.errorMessage?.let { put("errorMessage", JsonPrimitive(it)) }
                    metadata.blockNumber?.let { put("blockNumber", JsonPrimitive(it)) }
                })
            }
        }
        return jsonObject.toString()
    }

    override fun deserializeJson(json: String): Message {
        TODO("Not yet implemented")
    }
}