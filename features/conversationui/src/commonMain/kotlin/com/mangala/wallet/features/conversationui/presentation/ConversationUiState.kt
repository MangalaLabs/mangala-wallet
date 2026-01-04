package com.mangala.wallet.features.conversationui.presentation

import com.mangala.wallet.core.ai.domain.model.action.QuickAction
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.features.conversationui.domain.model.ConversationSession
import com.mangala.wallet.features.conversationui.presentation.model.ConversationInputState
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.conversationui.presentation.components.transaction.ProgressStep
import com.mangala.wallet.features.conversationui.presentation.components.transaction.TransactionReviewData
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.long
import kotlinx.serialization.Serializable

enum class FlowStepType {
    // Existing types
    SELECT_NETWORK,
    INPUT_ADDRESS,
    FIND_LOCAL_CONTACT,
    
    // New types for send token flow
    GET_LOCAL_DATA,
    CONDITIONAL_LOGIC,
    FIND_CONTACT_LOCAL,
    ADDRESS_INPUT,
    SHOW_LOCAL_SELECTOR,
    MEMO_INPUT,
    CALCULATE_FEES_LOCAL,
    TRANSACTION_REVIEW,
    SECURITY_AUTHENTICATION,
    EXECUTE_BLOCKCHAIN_TRANSACTION
}

data class TransactionFlowData(
    val selectedToken: TokenInfo? = null,
    val sendAmount: String? = null,
    val recipient: ContactAddress? = null,
    val recipientAddress: String? = null,
    val selectedContact: ContactModel? = null,
    val memo: String? = null,
    val calculatedFees: FeeCalculation? = null,
    val context: WalletContext? = null
)

data class TokenInfo(
    val symbol: String,
    val contractAddress: String? = null,
    val decimals: Int,
    val balance: String,
    val balanceUSD: String? = null
)

data class ContactAddress(
    val address: String,
    val network: String,
    val label: String? = null
)

data class FeeCalculation(
    val networkFee: String?,
    val networkFeeUSD: String?,
    val cpuUsage: String? = null,  // For EOS
    val netUsage: String? = null,  // For EOS
    val ramUsage: String? = null,  // For EOS
    val gasLimit: String? = null,  // For Ethereum
    val gasPrice: String? = null   // For Ethereum
)

data class WalletContext(
    val currentNetwork: NetworkInfo,
    val availableTokens: List<TokenInfo>,
    val walletsOnCurrentNetwork: List<WalletAccount>,
    val contacts: List<ContactModel>,
    val recentTransactions: List<Transaction>
)

data class NetworkInfo(
    val id: String,
    val name: String,
    val chainId: String? = null,
    val symbol: String,
    val rpcUrl: String? = null
)

data class WalletAccount(
    val id: String,
    val name: String,
    val address: String,
    val balance: String? = null
)

data class Transaction(
    val id: String,
    val fromAddress: String,
    val toAddress: String,
    val amount: String,
    val tokenSymbol: String,
    val timestamp: Long,
    val status: String
)

data class FlowState(
    val flowId: String,
    val currentStepIndex: Int,
    val flowDefinition: JsonObject,
    val collectedData: MutableMap<String, Any> = mutableMapOf(),
    val transactionData: TransactionFlowData? = null
)

@Serializable
data class SerializableFlowState(
    val flowId: String,
    val currentStepIndex: Int,
    val flowDefinition: JsonObject,
    val collectedData: Map<String, JsonElement> = emptyMap()
)

data class ContactSelectorState(
    val isVisible: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val contacts: List<ContactModel> = emptyList(),
    val selectedContactId: String? = null,
    val onSelectAction: String? = null,
    val onCancelAction: String? = null,
    val nextStep: String? = null,
    val cancelReason: String? = null,
    val maxVisible: Int = 10,
    val enableSearch: Boolean = true,
    val showLastUsed: Boolean = true,
    val showAddressCount: Boolean = true,
    val allowManualAddressInput: Boolean = false
)

data class AccountSelectorState(
    val isVisible: Boolean = false,
    val accounts: List<AntelopeAccount> = emptyList(),
    val selectedAccountId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val title: String = "Select Account"
)

sealed class TransactionFlowState {
    data object None : TransactionFlowState()
    
    data class Review(
        val reviewData: TransactionReviewData
    ) : TransactionFlowState()
    
    data class Progress(
        val steps: List<ProgressStep>,
        val currentStep: Int
    ) : TransactionFlowState()
}

fun Any.toJsonElement(): JsonElement = when (this) {
    is String -> JsonPrimitive(this)
    is Int -> JsonPrimitive(this)
    is Long -> JsonPrimitive(this)
    is Float -> JsonPrimitive(this)
    is Double -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is List<*> -> JsonArray(this.mapNotNull { it?.toJsonElement() })
    is Map<*, *> -> JsonObject(this.mapNotNull { (k, v) -> 
        if (k is String && v != null) k to v.toJsonElement() else null 
    }.toMap())
    else -> JsonPrimitive(this.toString()) // Fallback to string representation
}

/**
 * Convert JsonElement back to typed value
 */
inline fun <reified T> JsonElement.getValue(): T? = try {
    when (T::class) {
        String::class -> this.jsonPrimitive.content as? T
        Int::class -> this.jsonPrimitive.int as? T
        Long::class -> this.jsonPrimitive.long as? T
        Float::class -> this.jsonPrimitive.double.toFloat() as? T
        Double::class -> this.jsonPrimitive.double as? T
        Boolean::class -> this.jsonPrimitive.boolean as? T
        else -> null
    }
} catch (e: Exception) {
    null
}

/**
 * Convert JsonElement to Any for runtime use
 */
fun JsonElement.toAny(): Any = try {
    when {
        this is JsonPrimitive && this.isString -> this.content
        this is JsonPrimitive -> {
            // Try to parse as different types
            this.content.toBooleanStrictOrNull() 
                ?: this.content.toIntOrNull() 
                ?: this.content.toLongOrNull()
                ?: this.content.toDoubleOrNull()
                ?: this.content
        }
        this is JsonObject -> this.mapValues { it.value.toAny() }
        this is JsonArray -> this.map { it.toAny() }
        else -> this.toString()
    }
} catch (e: Exception) {
    this.toString()
}

/**
 * Convert FlowState to SerializableFlowState for persistence
 */
fun FlowState.toSerializable(): SerializableFlowState {
    return SerializableFlowState(
        flowId = flowId,
        currentStepIndex = currentStepIndex,
        flowDefinition = flowDefinition,
        collectedData = collectedData.mapValues { (_, value) -> value.toJsonElement() }
    )
}

/**
 * Convert SerializableFlowState back to FlowState for runtime use
 */
fun SerializableFlowState.toFlowState(): FlowState {
    return FlowState(
        flowId = flowId,
        currentStepIndex = currentStepIndex,
        flowDefinition = flowDefinition,
        collectedData = collectedData.mapValues { (_, jsonElement) -> jsonElement.toAny() }.toMutableMap()
    )
}

/**
 * Data class representing the UI state for the ConversationUiScreen
 */
data class ConversationUiState(
    val conversationSession: ConversationSession? = null,
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val isRecording: Boolean = false,
    val selectedImage: ByteArray? = null,
    val imageMimeType: String? = null,
    val isImageLoading: Boolean = false,
    val processingMessageIds: Set<String> = emptySet(), // Track messages that are currently being processed
    val quickActionsForMessages: Map<String, List<QuickAction>> = emptyMap(), // Track quick actions by message ID
    val expandedQuickActionMessageId: String? = null, // Track which message has expanded quick actions
    val error: String? = null,
    val inputState: ConversationInputState = ConversationInputState(),
    val showDebugInfo: Boolean = false,
    val isWebSocketConnected: Boolean = false, // WebSocket connection status
    val streamingMessage: Message? = null, // Current streaming message from WebSocket
    val activeFlow: FlowState? = null, // Current active flow state
    val contactSelectorState: ContactSelectorState = ContactSelectorState(), // Contact selector state for multiple matches
    val accountSelectorState: AccountSelectorState = AccountSelectorState(), // Account selector state for receive token
    val transactionFlow: TransactionFlowState = TransactionFlowState.None, // Transaction flow state for review and progress
    val functionCallSelections: Map<String, Map<String, Any?>> = emptyMap(), // Track selections for function calls by message ID
    val availableTokens: List<TokenInfo> = emptyList() // Available tokens in the current wallet context
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConversationUiState) return false

        if (conversationSession != other.conversationSession) return false
        if (messages != other.messages) return false
        if (isLoading != other.isLoading) return false
        if (isRecording != other.isRecording) return false
        if (selectedImage != null) {
            if (other.selectedImage == null) return false
            if (!selectedImage.contentEquals(other.selectedImage)) return false
        } else if (other.selectedImage != null) return false
        if (imageMimeType != other.imageMimeType) return false
        if (isImageLoading != other.isImageLoading) return false
        if (processingMessageIds != other.processingMessageIds) return false
        if (quickActionsForMessages != other.quickActionsForMessages) return false
        if (expandedQuickActionMessageId != other.expandedQuickActionMessageId) return false
        if (error != other.error) return false
        if (showDebugInfo != other.showDebugInfo) return false
        if (isWebSocketConnected != other.isWebSocketConnected) return false
        if (inputState != other.inputState) return false
        if (streamingMessage != other.streamingMessage) return false
        if (activeFlow != other.activeFlow) return false
        if (contactSelectorState != other.contactSelectorState) return false
        if (accountSelectorState != other.accountSelectorState) return false
        if (transactionFlow != other.transactionFlow) return false
        if (functionCallSelections != other.functionCallSelections) return false
        if (availableTokens != other.availableTokens) return false

        return true
    }

    override fun hashCode(): Int {
        var result = conversationSession?.hashCode() ?: 0
        result = 31 * result + messages.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + isRecording.hashCode()
        result = 31 * result + (selectedImage?.contentHashCode() ?: 0)
        result = 31 * result + (imageMimeType?.hashCode() ?: 0)
        result = 31 * result + isImageLoading.hashCode()
        result = 31 * result + processingMessageIds.hashCode()
        result = 31 * result + quickActionsForMessages.hashCode()
        result = 31 * result + (expandedQuickActionMessageId?.hashCode() ?: 0)
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + showDebugInfo.hashCode()
        result = 31 * result + inputState.hashCode()
        result = 31 * result + isWebSocketConnected.hashCode()
        result = 31 * result + (streamingMessage?.hashCode() ?: 0)
        result = 31 * result + (activeFlow?.hashCode() ?: 0)
        result = 31 * result + contactSelectorState.hashCode()
        result = 31 * result + accountSelectorState.hashCode()
        result = 31 * result + transactionFlow.hashCode()
        result = 31 * result + functionCallSelections.hashCode()
        result = 31 * result + availableTokens.hashCode()
        return result
    }
}