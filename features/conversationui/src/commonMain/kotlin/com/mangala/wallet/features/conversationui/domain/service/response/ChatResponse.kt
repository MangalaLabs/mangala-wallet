package com.mangala.wallet.features.conversationui.domain.service.response

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ChatResponse(
    val id: String,
    @SerialName("conversation_id")
    val conversationId: String,
    val content: String,
    val type: String,
    val timestamp: Instant,
    val metadata: UiCommandMetadata,
    @SerialName("quick_replies")
    val quickReplies: List<String>? = null,
    val actions: List<String>? = null,
    val suggestions: List<String>? = null,
    @SerialName("is_final")
    val isFinal: Boolean,
    @SerialName("requires_confirmation")
    val requiresConfirmation: Boolean? = null,
    @SerialName("confidence_score")
    val confidenceScore: Double? = null,
    @SerialName("processing_time_ms")
    val processingTimeMs: Long? = null,
    @SerialName("error_code")
    val errorCode: String? = null,
    @SerialName("error_message")
    val errorMessage: String? = null
)

@Serializable
data class UiCommandMetadata(
    val flowMode: String,
    val intentConfidence: Double,
    val language: String? = null,
    val detectedIntent: String,
    val uiCommands: List<UiCommand>
)

@Serializable
data class UiCommand(
    val id: String,
    val type: UiCommandType,
    val config: JsonElement? = null,
    val data: UiCommandData,
    val children: List<UiCommand>? = null,
    val conditions: JsonElement? = null,
    val priority: String,
    val timing: JsonElement? = null,
    val metadata: JsonElement? = null,
    @SerialName("created_at")
    val createdAt: Instant
)

@Serializable
enum class UiCommandType {
    CHAT_MESSAGE,
    EXECUTE_LOCAL_FLOW,
    GET_LOCAL_DATA,
    CONDITIONAL_LOGIC,
    FIND_CONTACT_LOCAL,
    ADDRESS_INPUT,
    SHOW_LOCAL_SELECTOR,
    MEMO_INPUT,
    CALCULATE_FEES_LOCAL,
    TRANSACTION_REVIEW,
    SECURITY_AUTHENTICATION,
    EXECUTE_BLOCKCHAIN_TRANSACTION,
    SHOW_ERROR,
    SET_VARIABLE,
    SHOW_ADDRESS_INPUT,
    SHOW_CONTACT_SELECTOR,
    SHOW_ADDRESS_SELECTOR
}

@Serializable
data class UiCommandData(
    // For CHAT_MESSAGE
    val text: String? = null,
    val sender: String? = null,
    
    // For EXECUTE_LOCAL_FLOW
    val executeLocally: Boolean? = null,
    val description: String? = null,
    val flowVersion: String? = null,
    val steps: List<FlowStep>? = null,
    val flowId: String? = null,
    val events: FlowEvents? = null,
    val flowType: String? = null
)

@Serializable
data class FlowStep(
    val stepId: String,
    val type: String,
    val config: JsonElement? = null,
    val showWhen: String? = null
)

@Serializable
data class FlowEvents(
    val onFlowStart: FlowEvent? = null,
    val onFlowComplete: FlowEvent? = null,
    val onFlowCancel: FlowEvent? = null
)

@Serializable
data class FlowEvent(
    val type: String,
    val data: JsonElement
)