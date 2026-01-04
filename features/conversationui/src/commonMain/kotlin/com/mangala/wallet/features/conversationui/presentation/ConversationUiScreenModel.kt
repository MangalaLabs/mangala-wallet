package com.mangala.wallet.features.conversationui.presentation

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.wallet.core.ai.domain.model.action.QuickAction
import com.mangala.wallet.core.ai.domain.model.action.QuickActionType
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallMessage
import com.mangala.wallet.core.ai.domain.model.message.FunctionResultMessage
import com.mangala.wallet.core.ai.domain.model.message.ImageMessage
import com.mangala.wallet.core.ai.domain.model.message.MultiModalMessage
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import com.mangala.wallet.core.ai.domain.model.message.UiTag
import com.mangala.wallet.core.ai.domain.model.message.TagParser
import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.SystemMessage
import com.mangala.wallet.core.ai.domain.model.message.MessageSendingStatus
import com.mangala.wallet.core.auth.SessionManager
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.features.conversationui.domain.usecase.CancelFunctionCallUseCase
import com.mangala.wallet.features.conversationui.domain.usecase.ClearChatContextUseCase
import com.mangala.wallet.features.conversationui.domain.usecase.ConfirmFunctionCallUseCase
import com.mangala.wallet.features.conversationui.domain.usecase.ExportChatLogUseCase
import com.mangala.wallet.features.conversationui.domain.usecase.GetSessionMessagesUseCase
import com.mangala.wallet.features.conversationui.domain.validation.AddressValidatorRegistry
import com.mangala.wallet.features.conversationui.presentation.model.InputMode
import com.mangala.wallet.features.conversationui.presentation.model.AddressValidationState
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.security.models.SecurityLevel
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FilterContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FilterCriteria
import com.mangala.wallet.features.addressbook.presentation.message.ContactResultsMessage
import com.mangala.wallet.features.addressbook.domain.model.ContactInfo
import com.mangala.wallet.features.addressbook.domain.model.ContactAddress
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesWithBlockchainByContactIdUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.model.blockchain.NetworkType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import com.mangala.wallet.features.conversationui.domain.service.StompWebSocketService
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetAllContactsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.token.GetAntelopeAccountTokenBalanceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.send.AntelopeSendCryptoUseCase
import com.mangala.wallet.features.send_base.conversation.*
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.auth.AuthenticationFlowManager
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.CreateContactWithAddressUseCase
import com.mangala.wallet.features.chains.antelope.create_account.conversation.AntelopeCreateAccountNavigationEvent
import com.mangala.wallet.features.conversationui.domain.model.ConversationSession
import kotlinx.serialization.json.buildJsonObject
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import com.mangala.wallet.features.conversationui.domain.service.ChunkType
import com.mangala.wallet.features.conversationui.domain.usecase.SendSocketMessageUseCase
import com.mangala.wallet.features.conversationui.presentation.model.AmountValidationState
import com.mangala.wallet.features.receive.presentation.conversation.ReceiveNavigationEvent
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import com.mangala.wallet.wallet.presentation.conversation.RestoreWalletNavigationEvent
import com.mangala.wallet.websocket.chat.websocket.exceptions.WebSocketAuthenticationException
import kotlin.collections.plus

@OptIn(ExperimentalUuidApi::class)
class ConversationUiScreenModel(
    private val sessionId: String?,
    private val getSessionMessagesUseCase: GetSessionMessagesUseCase,
    private val confirmFunctionCallUseCase: ConfirmFunctionCallUseCase,
    private val cancelFunctionCallUseCase: CancelFunctionCallUseCase,
    private val clearChatContextUseCase: ClearChatContextUseCase,
    private val exportChatLogUseCase: ExportChatLogUseCase,
    private val addressValidatorRegistry: AddressValidatorRegistry,
    private val stompWebSocketService: StompWebSocketService,
    private val sessionManager: SessionManager,
    private val chatHistoryRepository: ChatHistoryRepository,
    private val sendSocketMessageUseCase: SendSocketMessageUseCase,
    private val filterContactsUseCase: FilterContactsUseCase,
    private val getWalletAddressesWithBlockchainByContactIdUseCase: GetWalletAddressesWithBlockchainByContactIdUseCase,
    private val getAllContactsUseCase: GetAllContactsUseCase,
    private val createContactWithAddressUseCase: CreateContactWithAddressUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val json: Json,
    private val getAntelopeAccountTokenBalanceUseCase: GetAntelopeAccountTokenBalanceUseCase,
    private val antelopeSendCryptoUseCase: AntelopeSendCryptoUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider,
    private val authFlowManager: AuthenticationFlowManager
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    private var messagesCollectorJob: Job? = null
    private var addressValidationJob: Job? = null
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    sealed class NavigationEvent {
        data object NavigateToSignIn: NavigationEvent()
        data class GenericNavigationEvent(val event: Any) : NavigationEvent()
    }

    private var defaultUserId: String = ""

    init {
        screenModelScope.launch {
            defaultUserId = sessionManager.loadSession()?.userId ?: "default_user"

            if (sessionId != null) {
                // Load specific session
                loadSession(sessionId)
            } else {
                // New conversation mode - no initial session
                _uiState.update {
                    it.copy(
                        conversationSession = null,
                        isLoading = false,
                        error = null
                    )
                }
            }

            initializeStompWebSocket()
            observeStompMessages()
            observeStompConnectionState()
        }
    }

    private suspend fun saveFlowState() {
        val flowState = _uiState.value.activeFlow
        val session = _uiState.value.conversationSession

        if (session == null) {
            Napier.w("Cannot save flow state: no active session")
            return
        }

        println("ConversationUiScreenModel saveFlowState")

        try {
            val updatedMetadata = if (flowState != null) {
                // Serialize flow state and store in metadata
                val serializableFlowState = flowState.toSerializable()
                val flowStateJson = json.encodeToString(serializableFlowState)
                session.metadata + ("active_flow_state" to flowStateJson)
            } else {
                // Remove flow state from metadata if no active flow
                session.metadata - "active_flow_state"
            }

            // TODO: Save the new metadata back to the session state
            chatHistoryRepository.updateSessionMetadata(session.id, updatedMetadata)

            Napier.d("Flow state saved to session metadata")
        } catch (e: Exception) {
            Napier.e("Error saving flow state to session metadata", e)
        }
    }

    private fun loadFlowState(session: ConversationSession): FlowState? {
        return try {
            val flowStateJson = session.metadata["active_flow_state"]
            if (flowStateJson != null) {
                val serializableFlowState =
                    json.decodeFromString<SerializableFlowState>(flowStateJson)
                val flowState = serializableFlowState.toFlowState()
                Napier.d("Flow state loaded from session metadata: ${flowState.flowId}")
                flowState
            } else {
                null
            }
        } catch (e: Exception) {
            Napier.e("Error loading flow state from session metadata", e)
            null
        }
    }

    private fun restoreUiStateFromFlow(flowState: FlowState?) {
        if (flowState == null) return

        try {
            // Parse the flow definition to understand the current step
            val steps = flowState.flowDefinition["steps"]?.jsonArray
            if (steps != null && flowState.currentStepIndex < steps.size) {
                val currentStep = steps[flowState.currentStepIndex].jsonObject
                val stepType = currentStep["type"]?.jsonPrimitive?.content

                when (stepType) {
                    "address_input", "INPUT_ADDRESS" -> {
                        // Check if we have a selected network in collected data
                        val selectedNetworkId = flowState.collectedData["selectedNetwork"] as? String
                        val networkName = if (selectedNetworkId != null) {
                            BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true)
                                .find { it.blockChainUid == selectedNetworkId }?.name
                        } else {
                            null
                        }

                        if (networkName != null) {
                            Napier.d("Restoring address input mode for network: $networkName")
                            setAddressInputMode(networkName)
                        } else {
                            Napier.w("No network found in flow state for address input restoration")
                        }
                    }

                    "network_selection", "SELECT_NETWORK" -> {
                        // Restore network selection mode
                        val rawMessage = currentStep["message"]?.jsonPrimitive?.content
                            ?: currentStep["config"]?.jsonObject?.get("ui")?.jsonObject?.get("subtitle")?.jsonPrimitive?.content
                            ?: "Please select the network for your contact"
                        val message = replaceTemplateVariables(
                            rawMessage,
                            _uiState.value.activeFlow?.collectedData ?: emptyMap()
                        )
                        Napier.d("Restoring network selection mode")
                        requestNetworkSelection(message)
                    }

                    "contact_selection", "FIND_LOCAL_CONTACT" -> {
                        // Note: Contact selection state is handled separately via contactSelectorState
                        Napier.d("Flow is in contact selection step")
                    }
                }
            }
        } catch (e: Exception) {
            Napier.e("Error restoring UI state from flow", e)
        }
    }

    private fun loadSession(sessionId: String) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val session = chatHistoryRepository.getSession(sessionId)

                if (session != null) {
                    // Extract function call selections from session metadata
                    val functionCallSelections = mutableMapOf<String, Map<String, Any?>>()
                    session.metadata.entries.forEach { (key, value) ->
                        if (key.startsWith("function_call_selection_")) {
                            val messageId = key.removePrefix("function_call_selection_")
                            try {
                                // Parse the JSON string back to a map
                                val json = Json.parseToJsonElement(value).jsonObject
                                val selectionData = mapOf<String, Any?>(
                                    "selectedContactId" to json["selectedContactId"]?.jsonPrimitive?.content,
                                    "selectedContactName" to json["selectedContactName"]?.jsonPrimitive?.content
                                )
                                functionCallSelections[messageId] = selectionData
                            } catch (e: Exception) {
                                Napier.e(
                                    "Error parsing function call selection for message $messageId",
                                    e
                                )
                            }
                        }
                    }

                    // Load flow state from session metadata
                    val activeFlow = loadFlowState(session)

                    _uiState.update {
                        it.copy(
                            conversationSession = session,
                            isLoading = false,
                            error = null,
                            functionCallSelections = functionCallSelections,
                            activeFlow = activeFlow
                        )
                    }

                    // Restore UI state to match the flow state
                    restoreUiStateFromFlow(activeFlow)

                    if (session.metadata["memo_in_progress"] == "true") {
                        setMemoInputMode()
                    }

                    if (session.metadata["amount_in_progress"] == "true") {
                        val selectedToken = (activeFlow?.collectedData?.get("selectedToken") ?: activeFlow?.collectedData?.get("selectedAssetSymbol")) as? String
                        val sendAmount = activeFlow?.collectedData?.get("sendAmount") as? String
                        setAmountInputMode(selectedToken.orEmpty(), sendAmount.orEmpty())
                        val savedAmount = session.metadata["amount_input_text"] ?: ""
                        updateMessageText(savedAmount)
                    }

                    fetchSessionMessages(session.id)

//                     Save this session ID for app restart scenarios
//                    navigationPreferences.saveLastViewedSessionId(session.id)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Session not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load conversation: ${e.message}"
                    )
                }
            }
        }
    }

    private fun fetchSessionMessages(sessionId: String) {
        messagesCollectorJob?.cancel()

        messagesCollectorJob = screenModelScope.launch {
            try {
                Napier.d("Starting message collector for session: $sessionId")
                getSessionMessagesUseCase(sessionId).collect { messages ->
                    val filteredMessages =
                        messages.filterNot { it is FunctionCallMessage || it is FunctionResultMessage || it is SystemMessage }
                    Napier.d("Received ${messages.size} messages from repository, filtered to ${filteredMessages.size}")

                    // Log message details
                    filteredMessages.forEachIndexed { index, message ->
                        when (message) {
                            is TextMessage -> Napier.d(
                                "Message $index: ${if (message.isFromUser) "User" else "AI"} - ${
                                    message.text.take(
                                        50
                                    )
                                }..."
                            )

                            else -> Napier.d("Message $index: ${message::class.simpleName}")
                        }
                    }

                    // Extract processing message IDs from session metadata
                    val session = chatHistoryRepository.getSession(sessionId)
                    val processingIds = session?.metadata
                        ?.filterKeys { it.startsWith("processing_") }
                        ?.filterValues { it == "true" }
                        ?.keys
                        ?.map { it.removePrefix("processing_") }
                        ?.toSet() ?: emptySet()

                    _uiState.update { currentState ->
                        currentState.copy(
                            messages = filteredMessages,
                            processingMessageIds = processingIds
                        )
                    }
                    Napier.d("UI state updated with ${filteredMessages.size} messages")
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Napier.e("Failed to load messages", e)
                    _uiState.update {
                        it.copy(
                            error = "Failed to load messages: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    fun sendMessage(text: String) {
        clearSelectedImage()
        updateMessageText("")

        screenModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isLoading = true)
            }

            val imageData = _uiState.value.selectedImage
            val mimeType = _uiState.value.imageMimeType
            val hasImage = imageData != null && mimeType != null

            try {
                val message = if (hasImage) {
                    val textMessage = TextMessage(
                        isFromUser = true,
                        senderId = defaultUserId,
                        text = text
                    )

                    val imageMessage = ImageMessage(
                        isFromUser = true,
                        senderId = defaultUserId,
                        imageData = imageData!!,
                        mimeType = mimeType!!
                    )

                    MultiModalMessage(
                        isFromUser = true,
                        senderId = defaultUserId,
                        messages = listOf(textMessage, imageMessage)
                    )
                } else {
                    TextMessage(
                        isFromUser = true,
                        senderId = defaultUserId,
                        text = text
                    )
                }

                // Check if we have an existing session
                val existingSessionId = _uiState.value.conversationSession?.id

                if (existingSessionId != null) {
                    // Use existing session
                    sendStompMessage(text, message, existingSessionId)
                } else {
                    // Create new session and then send message
                    val newSession = chatHistoryRepository.createSession(
                        userId = defaultUserId,
                        title = "New Conversation",
                        metadata = emptyMap()
                    )

                    // Update UI state with new session
                    _uiState.update { currentState ->
                        currentState.copy(conversationSession = newSession)
                    }

                    // Start observing messages for the new session
                    fetchSessionMessages(newSession.id)

                    // Send the message
                    sendStompMessage(text, message, newSession.id)
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to send message: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleRecording() {
        _uiState.update { currentState ->
            currentState.copy(
                isRecording = !currentState.isRecording
            )
        }
    }

    fun stopRecording() {
        _uiState.update { currentState ->
            currentState.copy(
                isRecording = false
            )
        }
    }

    fun setSelectedImage(imageData: ByteArray, mimeType: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedImage = imageData,
                imageMimeType = mimeType,
                isImageLoading = false
            )
        }
    }

    private fun clearSelectedImage() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedImage = null,
                imageMimeType = null
            )
        }
    }

    fun setImageLoading(isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isImageLoading = isLoading
            )
        }
    }

    fun emitNavigationEvent(event: Any) {
        screenModelScope.launch {
            _navigationEvents.emit(NavigationEvent.GenericNavigationEvent(event))
        }
    }

    fun confirmFunctionCall(messageId: String, functionCallRequest: FunctionCallRequest) {
        if (functionCallRequest.name == "navigate_to_create_account") {
            screenModelScope.launch {
                val receiveTokenScreen = AntelopeCreateAccountNavigationEvent.NavigateToCreateAccount

                _navigationEvents.emit(NavigationEvent.GenericNavigationEvent(receiveTokenScreen))
            }
            return
        }

        if (functionCallRequest.name == "navigate_to_import_account") {
            screenModelScope.launch {
                val importAccountScreen = RestoreWalletNavigationEvent.NavigateToRestoreWallet

                _navigationEvents.emit(NavigationEvent.GenericNavigationEvent(importAccountScreen))
            }
            return
        }

        if (functionCallRequest.name == "select_contact_for_update") {
            handleContactSelectionConfirm(messageId, functionCallRequest)
            return
        }

        if (functionCallRequest.name == "select_asset_for_transaction") {
            handleAssetSelectionConfirm(messageId, functionCallRequest)
            return
        }

        if (functionCallRequest.name == CHAT_INLINE_ACTION_ADD_MEMO) {
            handleMemoConfirmation(messageId)
            return
        }

        // Add the message ID to the processing set and mark as CONFIRMED
        _uiState.update { currentState ->
            currentState.copy(
                processingMessageIds = currentState.processingMessageIds + messageId,
            )
        }

        screenModelScope.launch {
            try {
                confirmFunctionCallUseCase(
                    sessionId = _uiState.value.conversationSession?.id
                        ?: throw IllegalStateException("No active session"),
                    messageId = messageId,
                    userId = defaultUserId,
                    functionCall = functionCallRequest
                ).collectLatest { aiResponse ->
                    println("ProcessMessageUseCase AI response $aiResponse")
                }
            } catch (e: Exception) {
                // Mark as FAILED on error
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Failed to execute function: ${e.message}"
                    )
                }
            } finally {
                // Ensure the message is removed from processing state when done
                _uiState.update { currentState ->
                    currentState.copy(
                        processingMessageIds = currentState.processingMessageIds - messageId
                    )
                }
            }
        }
    }

    fun editFunctionCall(messageId: String, functionCallRequest: FunctionCallRequest) {
        // TODO: Navigate the user to the appropriate screen to edit the function call
    }

    fun denyFunctionCall(messageId: String, functionCallRequest: FunctionCallRequest) {
        // Handle special cases first
        when (functionCallRequest.name) {
            CHAT_INLINE_ACTION_ADD_MEMO -> {
                handleMemoSkip(messageId)
                return
            }
        }

        // Add the message ID to the processing set and mark as DENIED
        _uiState.update { currentState ->
            currentState.copy(
                processingMessageIds = currentState.processingMessageIds + messageId,
            )
        }

        screenModelScope.launch {
            try {
                cancelFunctionCallUseCase(
                    sessionId = _uiState.value.conversationSession?.id
                        ?: throw IllegalStateException("No active session"),
                    messageId = messageId,
                    userId = defaultUserId,
                    functionCall = functionCallRequest
                ).collectLatest { aiResponse ->
                    println("ProcessMessageUseCase AI response $aiResponse")
                }
            } finally {
                // Ensure the message is removed from processing state when done
                _uiState.update { currentState ->
                    currentState.copy(
                        processingMessageIds = currentState.processingMessageIds - messageId
                    )
                }
            }
        }
    }

    private fun handleMemoSkip(messageId: String) {
        _uiState.value.activeFlow?.collectedData?.put("memo", "")
        updateFunctionCallStatus(messageId, ExecutionStatus.CANCELLED)
        screenModelScope.launch {
            saveUserMessage("Skip")
            moveToNextFlowStep()
        }
    }

    private fun createMemoConfirmationDialog() {
        val activeFlow = _uiState.value.activeFlow ?: return
        
        // Extract transaction details from flow data
        val amount = activeFlow.collectedData["sendAmount"] as? String ?: ""
        val recipient = activeFlow.collectedData["targetContact"] as? String ?: ""
        val token = activeFlow.collectedData["selectedAsset"] as? String ?: ""
        
        val functionCall = FunctionCallRequest(
            name = CHAT_INLINE_ACTION_ADD_MEMO,
            parameters = mapOf(
                "amount" to amount,
                "recipient" to recipient,
                "token" to token
            ),
            callId = Uuid.random().toString(),
            securityLevel = SecurityLevel.RequireConfirmation
        )
        
        val confirmationMessage = FunctionCallConfirmationRequiredMessage(
            id = Uuid.random().toString(),
            senderId = "ai",
            isFromUser = false,
            parentMessageId = null,
            functionCall = functionCall,
            confirmationPrompt = "Would you like to add a memo to this transaction?",
            functionDescription = "Add optional memo to your transaction"
        )
        
        // Add to messages and save to history
        addMessageToState(confirmationMessage)
        saveMessageToHistory(confirmationMessage)
    }

    private fun addMessageToState(message: FunctionCallConfirmationRequiredMessage) {
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + message
            )
        }
    }

    private fun saveMessageToHistory(message: FunctionCallConfirmationRequiredMessage) {
        _uiState.value.conversationSession?.id?.let { sessionId ->
            screenModelScope.launch {
                chatHistoryRepository.saveMessage(sessionId, message)
            }
        }
    }

    private fun handleMemoConfirmation(messageId: String) {
        val amountMessage = TextMessage(
            isFromUser = false,
            senderId = "ai",
            text = "Please enter a memo for your transaction.",
            timestamp = Clock.System.now()
        )
        _uiState.value.conversationSession?.let {
            screenModelScope.launch {
                chatHistoryRepository.saveMessage(it.id, amountMessage)
                setMemoInputMode()
                saveFlowState()
                updateFunctionCallStatus(messageId, ExecutionStatus.CONFIRMED)
            }
        }
    }

    private fun updateFunctionCallStatus(messageId: String, status: ExecutionStatus) {
        screenModelScope.launch {
            try {
                chatHistoryRepository.updateMessageExecutionStatus(messageId, status)
            } catch (e: Exception) {
                Napier.e("Error updating function call status", e)
            }
        }
    }

    fun handleUiTagAction(uiTag: UiTag, data: Any) {
        when (uiTag) {
            UiTag.SelectNetwork -> {
                // Instead of handling inline, trigger network selection state
                requestNetworkSelection()
            }

            UiTag.RequestAddressInput, is UiTag.EnterAddress -> {
                // Instead of handling the address directly, set the input mode
                val networkName = when (uiTag) {
                    is UiTag.EnterAddress -> uiTag.networkName
                    else -> "unknown"
                }
                setAddressInputMode(networkName)
            }
        }
    }

    private fun requestNetworkSelection(message: String = "Please select the network for your contact") {
        _uiState.update { currentState ->
            currentState.copy(
                inputState = currentState.inputState.copy(
                    mode = InputMode.SelectNetwork,
                    networkSelectionMessage = message
                )
            )
        }
    }

    fun handleNetworkSelection(network: BlockchainNetworkData) {
        _uiState.value.activeFlow?.collectedData?.put("selectedNetwork", network.blockChainUid)

        _uiState.update { currentState ->
            currentState.copy(
                inputState = currentState.inputState.copy(
                    selectedNetworkId = network.blockChainUid,
                    networkSelectionMessage = null,
                    mode = InputMode.Normal
                )
            )
        }

        val activeFlow = _uiState.value.activeFlow
        if (activeFlow != null) {
            screenModelScope.launch {
                saveUserMessage(network.name)
                moveToNextFlowStep()
            }
        } else {
            screenModelScope.launch {
                val text = network.name
                val networkMessage = TextMessage(
                    isFromUser = true,
                    senderId = defaultUserId,
                    text = text
                )

                _uiState.value.conversationSession?.id?.let { sessionId ->
                    sendStompMessage(text, networkMessage, sessionId)
                }
            }
        }
    }

    fun minimizeNetworkSelection() {
        // Keep the network selector visible (pendingNetworkSelection = true)
        // But don't change the mode or clear the message
        // The UI will handle collapsing to compact view
        // No state changes needed here - the component handles its own expanded state
    }

    fun clearMessages() {
        screenModelScope.launch {
            try {
                clearChatContextUseCase(defaultUserId)
                // Don't generate a new userId - keep using the same one from the session
                _uiState.update { currentState ->
                    currentState.copy(
                        messages = emptyList(),
                        error = null
                    )
                }
                // Session remains the same after clearing messages
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to clear messages: ${e.message}")
                }
            }
        }
    }

    fun toggleDebugInfo() {
        _uiState.update { currentState ->
            currentState.copy(
                showDebugInfo = !currentState.showDebugInfo
            )
        }
    }

    fun exportChatLog() {
        screenModelScope.launch {
            try {
                val session = _uiState.value.conversationSession
                if (session != null) {
                    val exportResult = exportChatLogUseCase(session.id)
                    if (exportResult.filePath != null) {
                        println("Export successful! File saved to: ${exportResult.filePath}")
                    } else {
                        println("Export to console only - file export failed")
                    }
                } else {
                    println("No active session to export")
                    _uiState.update {
                        it.copy(error = "No active session to export")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to export chat log: ${e.message}")
                }
                println("Export failed: ${e.message}")
            }
        }
    }

    fun resetConversationState() {
        _uiState.update {
            ConversationUiState()
        }
        // Note: After reset, the session is cleared and navigation should handle re-initialization
    }

    fun toggleLoadingState() {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = !currentState.isLoading
            )
        }
    }

    private fun setAddressInputMode(networkName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                inputState = currentState.inputState.copy(
                    mode = InputMode.EnterAddress(networkName),
                    addressValidation = AddressValidationState(
                        isValid = null,
                        errorMessage = null,
                        network = networkName,
                        formattedAddress = null,
                        isLoading = false
                    )
                ),
                isLoading = false
            )
        }
    }

    private fun setMemoInputMode() {
        screenModelScope.launch {
            val session = _uiState.value.conversationSession ?: return@launch

            val updatedMetadata = session.metadata + ("memo_in_progress" to "true")
            chatHistoryRepository.updateSessionMetadata(session.id, updatedMetadata)

            _uiState.update { currentState ->
                currentState.copy(
                    inputState = currentState.inputState.copy(
                        mode = InputMode.EnterMemo,
                        addressValidation = null,
                        messageText = ""
                    ),
                    conversationSession = session.copy(metadata = updatedMetadata)
                )
            }
        }
    }

    private fun setAmountInputMode(tokenSymbol: String, balance: String) {
        _uiState.update { currentState ->
            currentState.copy(
                inputState = currentState.inputState.copy(
                    mode = InputMode.EnterAmount(tokenSymbol, balance),
                    addressValidation = null,
                    amountValidation = AmountValidationState(
                        isValid = null,
                        errorMessage = null,
                        tokenSymbol = tokenSymbol,
                        balance = balance
                    ),
                    messageText = ""
                )
            )
        }
    }

    private fun validateAmount(amount: String) {
        val currentMode = _uiState.value.inputState.mode
        if (currentMode !is InputMode.EnterAmount) return

        screenModelScope.launch {
            val balance = currentMode.balance.toDoubleOrNull() ?: 0.0
            val amountValue = amount.toDoubleOrNull()

            val isValid: Boolean
            val errorMessage: String?

            when {
                amount.isBlank() -> {
                    isValid = true
                    errorMessage = null
                }
                amountValue == null -> {
                    isValid = false
                    errorMessage = "Please enter a valid number."
                }
                amountValue <= 0 -> {
                    isValid = false
                    errorMessage = "Amount must be greater than zero."
                }
                amountValue > balance -> {
                    isValid = false
                    errorMessage = "Amount exceeds available balance (${currentMode.balance} ${currentMode.tokenSymbol})."
                }
                else -> {
                    isValid = true
                    errorMessage = null
                }
            }

            _uiState.update { currentState ->
                currentState.copy(
                    inputState = currentState.inputState.copy(
                        amountValidation = currentState.inputState.amountValidation?.copy(
                            isValid = isValid,
                            errorMessage = errorMessage
                        )
                    )
                )
            }
        }
    }

    private fun resetInputMode() {
        screenModelScope.launch {
            val session = _uiState.value.conversationSession
            if (session != null) {
                val updatedMetadata = session.metadata - "amount_in_progress" - "amount_token_symbol" - "amount_balance" - "amount_input_text" - "memo_in_progress"
                chatHistoryRepository.updateSessionMetadata(session.id, updatedMetadata)
                _uiState.update {
                    it.copy(conversationSession = session.copy(metadata = updatedMetadata))
                }
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                inputState = currentState.inputState.copy(
                    mode = InputMode.Normal,
                    addressValidation = null,
                    messageText = ""
                )
            )
        }
    }

    private fun validateAddress(address: String) {
        val currentMode = _uiState.value.inputState.mode
        if (currentMode !is InputMode.EnterAddress) return

        addressValidationJob?.cancel()

        if (address.isBlank()) {
            _uiState.update { currentState ->
                currentState.copy(
                    inputState = currentState.inputState.copy(
                        addressValidation = currentState.inputState.addressValidation?.copy(
                            isValid = null,
                            errorMessage = null,
                            formattedAddress = null,
                            isLoading = false
                        )
                    )
                )
            }
            return
        }

        val validator = addressValidatorRegistry.getValidator(currentMode.networkName)
        if (validator == null) {
            _uiState.update { currentState ->
                currentState.copy(
                    inputState = currentState.inputState.copy(
                        addressValidation = currentState.inputState.addressValidation?.copy(
                            isValid = null,
                            errorMessage = null,
                            formattedAddress = null,
                            isLoading = false
                        )
                    )
                )
            }
            return
        }

        try {
            // Run address format validation immediately
            val addressResult = validator.validateAddress(address, networkName = currentMode.networkName)
            
            // Update UI with format validation result immediately
            // If account existence check is required and format is valid, don't show as valid yet
            val willCheckExistence = validator.requiresAccountExistenceCheck(currentMode.networkName) && addressResult.isValid
            _uiState.update { currentState ->
                currentState.copy(
                    inputState = currentState.inputState.copy(
                        addressValidation = currentState.inputState.addressValidation?.copy(
                            isValid = if (willCheckExistence) null else addressResult.isValid,
                            errorMessage = addressResult.errorMessage,
                            formattedAddress = addressResult.formattedAddress,
                            isLoading = willCheckExistence
                        )
                    )
                )
            }
            
            // Only debounce if account existence check is required and format is valid
            if (addressResult.isValid && validator.requiresAccountExistenceCheck(currentMode.networkName)) {
                addressValidationJob = screenModelScope.launch {
                    delay(300)
                    
                    try {
                        val existenceResult = validator.checkAccountExists(address, networkName = currentMode.networkName)
                        val finalResult = if (existenceResult.exists) {
                            addressResult
                        } else {
                            addressResult.copy(
                                isValid = false,
                                errorMessage = existenceResult.errorMessage ?: "Account does not exist"
                            )
                        }
                        
                        _uiState.update { currentState ->
                            currentState.copy(
                                inputState = currentState.inputState.copy(
                                    addressValidation = currentState.inputState.addressValidation?.copy(
                                        isValid = finalResult.isValid,
                                        errorMessage = finalResult.errorMessage,
                                        formattedAddress = finalResult.formattedAddress,
                                        isLoading = false
                                    )
                                )
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                inputState = currentState.inputState.copy(
                                    addressValidation = currentState.inputState.addressValidation?.copy(
                                        isValid = false,
                                        errorMessage = "Account validation error: ${e.message}",
                                        formattedAddress = addressResult.formattedAddress,
                                        isLoading = false
                                    )
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update { currentState ->
                currentState.copy(
                    inputState = currentState.inputState.copy(
                        addressValidation = currentState.inputState.addressValidation?.copy(
                            isValid = false,
                            errorMessage = "Validation error: ${e.message}",
                            formattedAddress = null,
                            isLoading = false
                        )
                    )
                )
            }
        }
    }

    fun sendAddress(address: String) {
        val currentMode = _uiState.value.inputState.mode
        if (currentMode !is InputMode.EnterAddress) return

        screenModelScope.launch {
            val validator = addressValidatorRegistry.getValidator(currentMode.networkName)
            if (validator != null) {
                val addressResult = validator.validateAddress(address, currentMode.networkName)
                
                var finalResult = addressResult
                if (addressResult.isValid && validator.requiresAccountExistenceCheck(currentMode.networkName)) {
                    val existenceResult = validator.checkAccountExists(address, currentMode.networkName)
                    if (!existenceResult.exists) {
                        finalResult = addressResult.copy(
                            isValid = false,
                            errorMessage = existenceResult.errorMessage ?: "Account does not exist"
                        )
                    }
                }
                
                if (finalResult.isValid) {
                    val finalAddress = finalResult.formattedAddress ?: address

                    val activeFlow = _uiState.value.activeFlow
                    if (activeFlow != null) {
                        activeFlow.collectedData["address"] = finalAddress
                        activeFlow.collectedData["recipientAddress"] = finalAddress
                        resetInputMode()
                        saveUserMessage(address)
                        moveToNextFlowStep()
                    } else {
                        sendMessage(finalAddress)
                        resetInputMode()
                    }
                } else {
                    // Update validation state to show error
                    _uiState.update { currentState ->
                        currentState.copy(
                            inputState = currentState.inputState.copy(
                                addressValidation = currentState.inputState.addressValidation?.copy(
                                    isValid = false,
                                    errorMessage = finalResult.errorMessage ?: "Invalid address format"
                                )
                            )
                        )
                    }
                }
            } else {
                // No validator found, send anyway but show warning
                val activeFlow = _uiState.value.activeFlow
                if (activeFlow != null) {
                    // Store the address in the flow's collected data
                    activeFlow.collectedData["address"] = address

                    // Reset input mode
                    resetInputMode()

                    // Progress to next step in the flow
                    moveToNextFlowStep()
                } else {
                    // Legacy behavior: send the address as a regular message
                    sendMessage(address)
                    resetInputMode()
                }
            }
        }
    }

    /**
     * Send the memo input
     */
    fun sendMemo(memo: String) {
        val currentMode = _uiState.value.inputState.mode
        if (currentMode !is InputMode.EnterMemo) return

        screenModelScope.launch {
            val activeFlow = _uiState.value.activeFlow
            if (activeFlow != null) {
                activeFlow.collectedData["memo"] = memo
                resetInputMode()
                moveToNextFlowStep()
            } else {
                sendMessage(memo)
                resetInputMode()
            }
        }
    }

    fun sendContactName(contactName: String) {
        val currentMode = _uiState.value.inputState.mode
        if (currentMode !is InputMode.ContactName) return

        screenModelScope.launch {
            // Get validation config from the current step config stored in collected data
            val stepConfigStr = _uiState.value.activeFlow?.collectedData?.get("_currentStepConfig") as? String
            val stepConfig = stepConfigStr?.let { 
                try {
                    Json.parseToJsonElement(it).jsonObject
                } catch (e: Exception) {
                    null
                }
            }
            
            val field = stepConfig?.get("ui")?.jsonObject?.get("field")?.jsonObject
            val validation = field?.get("validation")?.jsonObject
            val pattern = validation?.get("pattern")?.jsonPrimitive?.content
            val minLength = validation?.get("minLength")?.jsonPrimitive?.content?.toIntOrNull() ?: 1
            val maxLength = validation?.get("maxLength")?.jsonPrimitive?.content?.toIntOrNull() ?: 50
            
            when {
                contactName.isBlank() -> {
                    // Contact name is required
                    return@launch
                }
                contactName.length < minLength -> {
                    // Name too short
                    return@launch
                }
                contactName.length > maxLength -> {
                    // Name too long
                    return@launch
                }
                pattern != null && !Regex(pattern).matches(contactName) -> {
                    // Name doesn't match pattern
                    return@launch
                }
                else -> {
                    // Valid contact name
                    val activeFlow = _uiState.value.activeFlow
                    if (activeFlow != null) {
                        activeFlow.collectedData["contactName"] = contactName
                        saveUserMessage(contactName)
                        resetInputMode()
                        moveToNextFlowStep()
                    } else {
                        sendMessage(contactName)
                        resetInputMode()
                    }
                }
            }
        }
    }

    fun sendAmount(amount: String) {
        val currentMode = _uiState.value.inputState.mode
        if (currentMode !is InputMode.EnterAmount) return

        screenModelScope.launch {
            validateAmount(amount) // Re-validate before sending

            val validationState = _uiState.value.inputState.amountValidation
            if (validationState?.isValid == true) {
                val activeFlow = _uiState.value.activeFlow
                if (activeFlow != null) {
                    activeFlow.collectedData["sendAmount"] = amount

                    saveUserMessage(amount)
                    resetInputMode()
                    moveToNextFlowStep()
                } else {
                    sendMessage(amount)
                    resetInputMode()
                }
            } else {
                // If not valid, the error message is already set by validateAmount
                Napier.w("Attempted to send invalid amount: $amount. Error: ${validationState?.errorMessage}")
            }
        }
    }

    fun getIsDebugBuild(): Boolean {
        return buildEnvironmentProvider.isDevelopmentEnvironment()
    }

    fun handleQrCodeResult(result: String) {
        val currentMode = _uiState.value.inputState.mode
        if (currentMode !is InputMode.EnterAddress) return

        screenModelScope.launch {
            updateMessageText(result)
            validateAddress(result)
            _uiState.update { currentState ->
                currentState.copy(
                    inputState = currentState.inputState.copy(
                        addressValidation = currentState.inputState.addressValidation?.copy(
                            formattedAddress = result
                        )
                    )
                )
            }
            // If valid, send it automatically
            val validationState = _uiState.value.inputState.addressValidation
            if (validationState?.isValid == true) {
                sendAddress(result)
            }
        }
    }

    fun pasteAddressFromClipboard(text: String) {
        val currentMode = _uiState.value.inputState.mode
        if (currentMode !is InputMode.EnterAddress) return

        screenModelScope.launch {
            updateMessageText(text)
            validateAddress(text)
            _uiState.update { currentState ->
                currentState.copy(
                    inputState = currentState.inputState.copy(
                        addressValidation = currentState.inputState.addressValidation?.copy(
                            formattedAddress = text
                        )
                    )
                )
            }
        }
    }

    fun updateMessageText(text: String) {
        val currentMode = _uiState.value.inputState.mode

        val filteredText = when (currentMode) {
            is InputMode.EnterAmount -> filterAmountInput(text)
            else -> text
        }

        _uiState.update { currentState ->
            currentState.copy(
                inputState = currentState.inputState.copy(
                    messageText = filteredText
                )
            )
        }

        if (currentMode is InputMode.EnterAddress) {
            validateAddress(filteredText)
        } else if (currentMode is InputMode.EnterAmount) {
            validateAmount(filteredText)
        }
    }
    
    private fun filterAmountInput(input: String): String {
        // Get selected asset decimals from flow data, default to 18
        val selectedAssetDecimals = _uiState.value.activeFlow?.collectedData?.get("selectedAssetDecimals")?.toString()?.toIntOrNull() ?: 18
        
        // If input doesn't contain a decimal point, allow it
        if (!input.contains('.')) {
            return input
        }
        
        // Split by decimal point
        val parts = input.split('.')
        if (parts.size > 2) {
            // Multiple decimal points - only keep first part and first decimal
            return "${parts[0]}.${parts[1]}"
        }
        
        // Limit decimal places to selectedAssetDecimals
        val decimalPart = parts.getOrNull(1) ?: ""
        val limitedDecimalPart = if (decimalPart.length > selectedAssetDecimals) {
            decimalPart.substring(0, selectedAssetDecimals)
        } else {
            decimalPart
        }
        
        return "${parts[0]}.${limitedDecimalPart}"
    }

    private fun initializeStompWebSocket() {
        screenModelScope.launch {
            try {
                Napier.i("Initializing STOMP WebSocket connection...")

                // First check if we have a valid session
                val authSession = sessionManager.sessionState.value
                if (authSession == null) {
                    Napier.e("No active session found, cannot connect to WebSocket")
                    _uiState.update {
                        it.copy(error = "Please login first to connect to chat")
                    }
                    return@launch
                }

                // Log JWT token info for debugging
                val token = authSession.token.accessToken
                Napier.d("JWT Token (FULL): $token")

                // Initialize STOMP WebSocket connection
                val connected = stompWebSocketService.connect()
                if (connected) {
                    Napier.i("WebSocket connected successfully!")
                    // Start a new conversation when connected
                    stompWebSocketService.startConversation(
                        title = "Chat Session",
                        metadata = mapOf("source" to JsonPrimitive("mobile_app"))
                    )
                } else {
                    Napier.e("WebSocket connection failed")
                    _uiState.update {
                        it.copy(error = "Failed to connect to chat server. Please check your connection and try again.")
                    }
                }
            } catch (e: Exception) {
                if (e is WebSocketAuthenticationException) {
                    authFlowManager.logout()
                    _navigationEvents.emit(NavigationEvent.NavigateToSignIn)
                    return@launch
                }
                Napier.e("Failed to initialize WebSocket", e)
                _uiState.update {
                    it.copy(error = "Failed to initialize WebSocket: ${e.message}")
                }
            }
        }
    }

    private fun observeStompMessages() {
        screenModelScope.launch {
            Napier.d(
                tag = "ConversationUiScreenModel",
                message = "Start observing STOMP messages $stompWebSocketService"
            )

            stompWebSocketService.messages.collect { response ->
                MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.CONVERSATION_MESSAGE_RECEIVED)

                response.metadata?.get("uiCommands")?.let { uiCommandsElement ->
                    try {
                        val uiCommands = uiCommandsElement.jsonArray
                        processUiCommands(uiCommands, response.id)
                    } catch (e: Exception) {
                        Napier.e("Error parsing uiCommands", e)
                    }
                }
            }
        }

        // Observe streaming messages
        screenModelScope.launch {
            var currentStreamMessage: TextMessage? = null
            var accumulatedContent = ""

            stompWebSocketService.streamChunks.collect { chunk ->
                when (chunk.type) {
                    ChunkType.START -> {
                        // Start of a new streaming message
                        accumulatedContent = chunk.content ?: ""
                        currentStreamMessage = TextMessage(
                            isFromUser = false,
                            senderId = "ai",
                            text = accumulatedContent,
                            timestamp = chunk.timestamp ?: Clock.System.now()
                        )
                    }

                    ChunkType.CONTENT,
                    ChunkType.DELTA -> {
                        // Accumulate content
                        accumulatedContent += chunk.deltaContent ?: chunk.content ?: ""
                        currentStreamMessage = currentStreamMessage?.copy(text = accumulatedContent)

                        // Update UI with partial content for live streaming effect
                        _uiState.update { state ->
                            state.copy(
                                streamingMessage = currentStreamMessage
                            )
                        }
                    }

                    ChunkType.END -> {
                        // Stream complete, parse UI tags and save the final message
                        _uiState.value.conversationSession?.id?.let { sessionId ->
                            currentStreamMessage?.let { message ->
                                val parseResult = TagParser.parseMessage(message.text)
                                val finalMessage = message.copy(
                                    text = parseResult.cleanedText,
                                    uiTags = listOfNotNull(parseResult.uiTag)
                                )

                                // Process UI tags when the streaming message is complete
                                parseResult.uiTag?.let { tag ->
                                    handleUiTagAction(tag, "")
                                }

                                chatHistoryRepository.saveMessage(sessionId, finalMessage)
                                // Messages will be updated automatically via the Flow collector
                            }
                        }

                        // Clear streaming state
                        _uiState.update { state ->
                            state.copy(streamingMessage = null)
                        }

                        currentStreamMessage = null
                        accumulatedContent = ""
                    }

                    else -> {
                        // Handle ERROR, HEARTBEAT, etc.
                        println("Stream chunk type: ${chunk.type}, content: ${chunk.content}")
                    }
                }
            }
        }

        // Observe errors
        screenModelScope.launch {
            stompWebSocketService.errors.collect { error ->
                _uiState.update {
                    it.copy(error = "WebSocket error: ${error.errorMessage}")
                }
            }
        }
    }

    private fun observeStompConnectionState() {
        screenModelScope.launch {
            stompWebSocketService.connectionState.collect { state ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isWebSocketConnected = state == StompWebSocketService.ConnectionState.AUTHENTICATED
                    )
                }
            }
        }
    }

    private fun sendStompMessage(text: String, message: Message, sessionId: String) {
        screenModelScope.launch {
            val conversationId = _uiState.value.conversationSession?.id
            val result = sendSocketMessageUseCase(
                userId = defaultUserId,
                sessionId = sessionId,
                message = message,
                text = text,
                conversationId = conversationId
            )

            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        error = error.message ?: "Failed to send message",
                        isLoading = false
                    )
                }
            }

            MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.CONVERSATION_MESSAGE_SENT)
        }
    }

    /**
     * Retry sending a failed message
     * @param messageId The ID of the message to retry
     */
    fun retryMessage(messageId: String) {
        screenModelScope.launch {
            Napier.d("Retrying message: $messageId")

            // Find the message in the current messages list
            val message = _uiState.value.messages.find { it.id == messageId }
            if (message == null) {
                Napier.e("Message not found for retry: $messageId")
                return@launch
            }

            // Only retry failed messages
            val sendingStatus = when (message) {
                is TextMessage -> message.sendingStatus
                is ImageMessage -> message.sendingStatus
                is MultiModalMessage -> message.sendingStatus
                else -> null
            }

            Napier.d("Message status before retry: $sendingStatus")

            if (sendingStatus != MessageSendingStatus.FAILED) {
                Napier.w("Message is not in failed state, cannot retry: $messageId")
                return@launch
            }

            // Update the message status to PENDING before retrying
            Napier.d("Updating message status to PENDING")

            // Show loading state immediately by updating the UI state
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    error = null
                )
            }

            chatHistoryRepository.updateMessageSendingStatus(
                messageId,
                MessageSendingStatus.PENDING
            )

            // Force a small delay to ensure the database update is processed
            delay(100)

            // Extract the text content from the message
            val text = when (message) {
                is TextMessage -> message.text
                is MultiModalMessage -> {
                    // For multimodal messages, extract the text part
                    message.messages.filterIsInstance<TextMessage>().firstOrNull()?.text ?: ""
                }

                else -> ""
            }

            if (text.isEmpty()) {
                Napier.e("Cannot retry message with empty text content: $messageId")
                chatHistoryRepository.updateMessageSendingStatus(
                    messageId,
                    MessageSendingStatus.FAILED
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Cannot retry message with empty content"
                    )
                }
                return@launch
            }

            // Retry sending the message through the use case
            _uiState.value.conversationSession?.id?.let { sessionId ->
                val result = sendSocketMessageUseCase(
                    userId = defaultUserId,
                    sessionId = sessionId,
                    message = message,
                    text = text,
                    conversationId = _uiState.value.conversationSession?.id
                )

                result.onFailure { error ->
                    Napier.e("Failed to retry message", error)
                    _uiState.update {
                        it.copy(
                            error = "Failed to retry message: ${error.message}",
                            isLoading = false // Clear loading state when retry fails
                        )
                    }
                }

                result.onSuccess {
                    Napier.d("Message retry successful: $messageId")
                }
            }
        }
    }

    /**
     * Extract token symbol from a condition string like "{context.availableTokens.find(t => t.symbol === 'EOS')}"
     */
    private fun extractTokenSymbolFromCondition(condition: String): String {
        // Pattern to match token symbols in find expressions
        val patterns = listOf(
            "t\\.symbol\\s*===?\\s*['\"]([^'\"]+)['\"]".toRegex(),  // t.symbol === 'EOS' or t.symbol == 'EOS'
            "t\\.symbol\\s*===?\\s*['\"]([^'\"]+)['\"]".toRegex(),  // t.symbol === "EOS" or t.symbol == "EOS"
            "symbol\\s*===?\\s*['\"]([^'\"]+)['\"]".toRegex(),      // symbol === 'EOS'
            "['\"]([^'\"]+)['\"]\\s*===?\\s*t\\.symbol".toRegex(),  // 'EOS' === t.symbol
            "['\"]([^'\"]+)['\"]\\s*===?\\s*symbol".toRegex()       // 'EOS' === symbol
        )
        
        for (pattern in patterns) {
            pattern.find(condition)?.let { matchResult ->
                return matchResult.groupValues[1]
            }
        }
        
        // Fallback: try to extract any quoted string that looks like a token symbol
        val fallbackPattern = "['\"]([A-Z]{2,10})['\"]".toRegex()
        fallbackPattern.find(condition)?.let { matchResult ->
            return matchResult.groupValues[1]
        }
        
        // If no pattern matches, return empty string
        return ""
    }

    /**
     * Replace template variables in a string with values from the provided map
     */
    private fun replaceTemplateVariables(template: String, variables: Map<String, Any>): String {
        var result = template

        // Replace nested property access (e.g., {selectedNetwork.name})
        val nestedPattern = Regex("\\{([^}]+)\\.([^}]+)\\}")
        result = nestedPattern.replace(result) { matchResult ->
            val objName = matchResult.groupValues[1]
            val propName = matchResult.groupValues[2]
            val obj = variables[objName]

            when {
                obj is BlockchainNetworkData && propName == "name" -> obj.name
                obj is BlockchainNetworkData && propName == "exampleAddress" -> {
                    // Return example addresses based on network
                    when (obj.blockChainUid) {
                        "ethereum" -> "0x742d35Cc6634C0532925a3b844Bc9e7595f89595"
                        "binance-smart-chain" -> "0x742d35Cc6634C0532925a3b844Bc9e7595f89595"
                        "polygon" -> "0x742d35Cc6634C0532925a3b844Bc9e7595f89595"
                        "eos" -> "eosaccountname"
                        else -> "example_address"
                    }
                }

                obj is BlockchainNetworkData && propName == "addressFormat" -> {
                    // Return address format based on network
                    when (obj.blockChainUid) {
                        "ethereum", "binance-smart-chain", "polygon" -> "0x followed by 40 hexadecimal characters"
                        "eos" -> "1-12 characters (a-z, 1-5, .)"
                        else -> "Network specific format"
                    }
                }

                else -> matchResult.value
            }
        }

        // Replace simple variables (e.g., {contactName})
        val simplePattern = Regex("\\{([^}]+)\\}")
        result = simplePattern.replace(result) { matchResult ->
            val varName = matchResult.groupValues[1]
            // Handle OR expressions (e.g., {extractedData.targetContact || targetContact})
            if (varName.contains("||")) {
                val parts = varName.split("||").map { it.trim() }
                for (part in parts) {
                    val value = if (part.contains(".")) {
                        // Handle nested property
                        val objPath = part.split(".")
                        variables[objPath[0]]?.toString()
                    } else {
                        variables[part]?.toString()
                    }
                    if (!value.isNullOrEmpty()) {
                        return@replace value
                    }
                }
                matchResult.value
            } else {
                variables[varName]?.toString() ?: matchResult.value
            }
        }

        return result
    }

    override fun onDispose() {
        super.onDispose()

        screenModelScope.launch {
            stompWebSocketService.disconnect()
        }
    }

    /**
     * Show quick actions for a message with optional auto-dismiss
     */
    fun showQuickActions(
        messageId: String,
        actions: List<QuickAction>,
        @Suppress("UNUSED_PARAMETER") context: Map<String, Any>,
        autoDismissAfterSeconds: Int = 30
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                quickActionsForMessages = currentState.quickActionsForMessages + (messageId to actions),
                expandedQuickActionMessageId = messageId
            )
        }

        // Auto-dismiss after specified time
        if (autoDismissAfterSeconds > 0) {
            screenModelScope.launch {
                delay(autoDismissAfterSeconds * 1000L)
                // Only dismiss if this message is still the expanded one
                if (_uiState.value.expandedQuickActionMessageId == messageId) {
                    dismissQuickActions(messageId)
                }
            }
        }
    }

    fun dismissQuickActions(messageId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                quickActionsForMessages = currentState.quickActionsForMessages - messageId,
                expandedQuickActionMessageId = if (currentState.expandedQuickActionMessageId == messageId) null else currentState.expandedQuickActionMessageId
            )
        }
    }

    fun handleQuickAction(action: QuickAction) {
        // For now, emit the action as a navigation event
        // In a full implementation, this would route to specific handlers based on action type
        val actionType = action.actionType
        when (actionType) {
            is QuickActionType.Navigate -> {
                emitNavigationEvent(actionType.destination)
            }

            is QuickActionType.ExecuteFunction -> {
                // TODO: Handle function execution
                println("Execute function: ${actionType.functionName}")
            }

            is QuickActionType.ShowDialog -> {
                // TODO: Handle dialog showing
                println("Show dialog: ${actionType.dialogType}")
            }
        }
    }

    private suspend fun processUiCommands(uiCommands: JsonArray, messageId: String?) {
        uiCommands.forEach { commandElement ->
            try {
                val command = commandElement.jsonObject
                val type = command["type"]?.jsonPrimitive?.content
                val action = try {
                    command["config"]?.jsonObject?.get("actions")?.jsonArray?.firstOrNull {
                        it.jsonObject["action"]?.jsonPrimitive?.content == "CREATE_LOCAL_CONTACT"
                    }
                } catch (e: Exception) {
                    Napier.e("Error parsing actions from command", e)
                    null
                }

                when (type) {
                    "CHAT_MESSAGE" -> {
                        val data = command["data"]?.jsonObject
                        val text = data?.get("text")?.jsonPrimitive?.content ?: ""
                        val sender = data?.get("sender")?.jsonPrimitive?.content ?: "ai"
                        
                        val textMessage = TextMessage(
                            isFromUser = false,
                            senderId = sender,
                            text = text,
                            timestamp = Clock.System.now(),
                            uiTags = emptyList()
                        )

                        _uiState.value.conversationSession?.id?.let { sessionId ->
                            chatHistoryRepository.saveMessage(sessionId, textMessage)

                            _uiState.update { currentState ->
                                currentState.copy(
                                    isLoading = false
                                )
                            }
                        }
                    }
                    "EXECUTE_LOCAL_FLOW" -> {
                        // Handle local flow execution
                        val data = command["data"]?.jsonObject
                        if (data != null) {
                            val flowId = data["flowId"]?.jsonPrimitive?.content ?: "unknown_flow"
                            val flowType = data["flowType"]?.jsonPrimitive?.content
                            val steps = (data["steps"])?.jsonArray ?: (data["flow"]?.jsonObject?.get("steps"))?.jsonArray
                            val extractedData = data["extractedData"]?.jsonObject
                            val parameters = data["parameters"]?.jsonObject

                            if (flowType == "RECEIVE_TOKEN") {
                                handleReceiveTokenFlow(data, messageId)
                                return@forEach
                            }

                            if (flowType == "SEND_TOKEN") {
                                handleSendTokenFlow(data, messageId)
                                return@forEach
                            }

                            if (flowType == "CREATE_ACCOUNT") {
                                handleCreateAccountFlow(data, messageId)
                                return@forEach
                            }

                            if (flowType == "IMPORT_ACCOUNT") {
                                handleImportAccountFlow(data, messageId)
                                return@forEach
                            }

                            // Try to parse using the new FlowDefinitionParser for enhanced flow types
                            if (shouldUseEnhancedParser(data)) {
                                try {
                                    processExecuteLocalFlowWithParser(data, messageId)
                                    return@forEach
                                } catch (e: Exception) {
                                    Napier.e(
                                        "Error using enhanced flow parser, falling back to legacy",
                                        e
                                    )
                                    // Fall through to legacy handling
                                }
                            }

                            // Create a flow state
                            val flowState = FlowState(
                                flowId = flowId,
                                currentStepIndex = 0,
                                flowDefinition = data,
                                collectedData = mutableMapOf()
                            )

                            // Store extracted data if available
                            extractedData?.let { extractedObj ->
                                extractedObj.keys.forEach { key ->
                                    val value = extractedObj[key]?.jsonPrimitive?.content
                                    if (value != null) {
                                        flowState.collectedData[key] = value
                                    }
                                }
                            }

                            // Store parameters if available
                            parameters?.let { params ->
                                params.keys.forEach { key ->
                                    when (key) {
                                        "preFilledName" -> {
                                            val value = params[key]?.jsonPrimitive?.content
                                            if (value != null) {
                                                flowState.collectedData["contactName"] = value
                                            }
                                        }

                                        else -> {
                                            val value = params[key]?.jsonPrimitive?.content
                                            if (value != null) {
                                                flowState.collectedData[key] = value
                                            }
                                        }
                                    }
                                }
                            }

                            // Update UI state with the new flow
                            _uiState.update { currentState ->
                                currentState.copy(activeFlow = flowState)
                            }

                            // Save flow state to session metadata
                            saveFlowState()

                            // Execute the first step if available
                            steps?.firstOrNull()?.let { firstStep ->
                                val step = firstStep.jsonObject
                                val stepId = step["stepId"]?.jsonPrimitive?.content
                                val stepType = step["type"]?.jsonPrimitive?.content

                                if (stepId != null && stepType != null) {
                                    executeFlowStep(
                                        flowId = flowId,
                                        stepId = stepId,
                                        stepType = stepType,
                                        stepConfig = step["config"]?.jsonObject,
                                        showWhen = step["showWhen"]?.jsonPrimitive?.content,
                                        messageId = messageId
                                    )
                                }
                            }
                        }
                    }

                    "CONTACT_PREVIEW_CARD" -> {
                        if (action != null) {
                            // Extract contact data
                            val data = command["data"]?.jsonObject?.get("data")?.jsonObject
                            if (data != null) {
                                val name = data["name"]?.jsonPrimitive?.content ?: ""
                                val address = data["address"]?.jsonPrimitive?.content ?: ""
                                val network = data["network"]?.jsonPrimitive?.content ?: ""

                                // Create FunctionCallRequest for add_contact
                                val functionCall = FunctionCallRequest(
                                    name = "add_contact",
                                    parameters = mapOf(
                                        "name" to name,
                                        "blockchain_address_or_account_name" to address,
                                        "blockchain_network" to network
                                    ),
                                    callId = Uuid.random().toString(),
                                    securityLevel = SecurityLevel.defaultSecurityLevel
                                )

                                val confirmationMessage = FunctionCallConfirmationRequiredMessage(
                                    id = Uuid.random().toString(),
                                    senderId = "ai",
                                    isFromUser = false,
                                    parentMessageId = messageId,
                                    functionCall = functionCall,
                                    confirmationPrompt = "Please confirm the contact details",
                                    functionDescription = "Add $name to your contacts on $network network"
                                )

                                // Save the confirmation message to the repository
                                _uiState.value.conversationSession?.id?.let { sessionId ->
                                    chatHistoryRepository.saveMessage(
                                        sessionId,
                                        confirmationMessage
                                    )
                                }
                            }
                        }
                    }

                    "NAME_UPDATE_FLOW" -> {
                        // Extract flow data
                        val data = command["data"]?.jsonObject
                        if (data != null) {
                            val flowId = data["flowId"]?.jsonPrimitive?.content
                            val steps = data["steps"]?.jsonArray
                            val parameters = data["parameters"]?.jsonObject

                            // Extract parameters from the flow
                            val searchName =
                                parameters?.get("searchName")?.jsonPrimitive?.content ?: ""
                            val targetName =
                                parameters?.get("targetName")?.jsonPrimitive?.content ?: ""

                            // Parse flow steps to understand the flow structure
                            steps?.forEach { stepElement ->
                                val step = stepElement.jsonObject
                                val stepId = step["stepId"]?.jsonPrimitive?.content
                                val stepType = step["type"]?.jsonPrimitive?.content

                                Napier.d("Flow step: $stepId of type $stepType")

                                // For now, we'll focus on the first step which finds the contact
                                if (stepType == "FIND_LOCAL_CONTACT") {
                                    val searchCriteria =
                                        step["config"]?.jsonObject?.get("searchCriteria")?.jsonObject
                                    val searchQuery =
                                        searchCriteria?.get("query")?.jsonPrimitive?.content
                                            ?: searchName

                                    // Store the flow definition in UI state for later processing
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            activeFlow = FlowState(
                                                flowId = flowId ?: "update_contact_name",
                                                currentStepIndex = 0,
                                                flowDefinition = data,
                                                collectedData = mutableMapOf(
                                                    "searchName" to searchQuery,
                                                    "targetName" to targetName
                                                )
                                            )
                                        )
                                    }

                                    // Execute the first step - find the contact
                                    executeFlowStep(
                                        flowId = flowId ?: "update_contact_name",
                                        stepId = stepId ?: "step_1_find_contact",
                                        stepType = stepType,
                                        stepConfig = step["config"]?.jsonObject,
                                        showWhen = step["showWhen"]?.jsonPrimitive?.content,
                                        messageId = messageId
                                    )
                                    return@forEach
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Napier.e("Error processing UI command", e)
            }
        }
    }

    private fun handleCreateAccountFlow(flowData: JsonObject, messageId: String?) {
        screenModelScope.launch {
            val functionCall = FunctionCallRequest(
                name = "navigate_to_create_account",
                parameters = emptyMap(),
                callId = Uuid.random().toString(),
                securityLevel = SecurityLevel.RequireConfirmation
            )

            val confirmationMessage = FunctionCallConfirmationRequiredMessage(
                id = Uuid.random().toString(),
                senderId = "ai",
                isFromUser = false,
                parentMessageId = messageId,
                functionCall = functionCall,
                confirmationPrompt = "I can help you create a new account. Would you like to start the process?",
                functionDescription = "Navigates to the account creation screen to start the new account flow."
            )

            _uiState.value.conversationSession?.id?.let { sessionId ->
                chatHistoryRepository.saveMessage(
                    sessionId,
                    confirmationMessage
                )
            }
        }
    }

    private fun handleImportAccountFlow(flowData: JsonObject, messageId: String?) {
        screenModelScope.launch {
            val functionCall = FunctionCallRequest(
                name = "navigate_to_import_account",
                parameters = emptyMap(),
                callId = Uuid.random().toString(),
                securityLevel = SecurityLevel.RequireConfirmation
            )

            val confirmationMessage = FunctionCallConfirmationRequiredMessage(
                id = Uuid.random().toString(),
                senderId = "ai",
                isFromUser = false,
                parentMessageId = messageId,
                functionCall = functionCall,
                confirmationPrompt = "I can help you import an existing account. Would you like to start the process?",
                functionDescription = "Navigates to the account import screen to start the import account flow."
            )

            _uiState.value.conversationSession?.id?.let { sessionId ->
                chatHistoryRepository.saveMessage(
                    sessionId,
                    confirmationMessage
                )
            }
        }
    }

    private fun executeFlowStep(
        flowId: String,
        stepId: String,
        stepType: String,
        showWhen: String?,
        stepConfig: JsonObject?,
        messageId: String?
    ) {
        screenModelScope.launch {
            if (shouldExecuteStep(showWhen).not()) {
                moveToNextFlowStep()
                return@launch
            }

            Napier.d("Executing flow step: $stepId of type $stepType")

            when (stepType) {
                "FIND_CONTACT_LOCAL" -> {
                    handleFindContactLocal(stepConfig)
                }
                "SHOW_LOCAL_SELECTOR" -> {
                    moveToNextFlowStep()
                }
                "TRANSACTION_REVIEW" -> {
                    confirmTransaction(messageId)
                }
                "CALCULATE_FEES_LOCAL" -> {
                    moveToNextFlowStep()
                }
                "ADDRESS_INPUT" -> {
                    val blockchainUid = _uiState.value.activeFlow?.collectedData?.get("blockchainType") as? String
                    val networkName = BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true)
                        .find { it.blockChainUid == blockchainUid }?.name ?: return@launch
                    setAddressInputMode(networkName)
                }
                "MEMO_INPUT" -> {
                    createMemoConfirmationDialog()
                }
                "FIND_LOCAL_CONTACT", "SEARCH_LOCAL_CONTACTS" -> {
                    // Extract search query from step configuration or flow data
                    val searchQuery = stepConfig?.get("query")?.jsonPrimitive?.content
                        ?: _uiState.value.activeFlow?.collectedData?.get("searchName") as? String
                        ?: _uiState.value.activeFlow?.collectedData?.get("targetContact") as? String
                        ?: ""

                    // Extract response configuration from stepConfig
                    val responses = stepConfig?.get("behaviors")?.jsonObject
                    val extractedData = _uiState.value.activeFlow?.collectedData
                    val targetContact =
                        extractedData?.get("targetContact") as? String ?: searchQuery

                    searchContacts(searchQuery, responses, targetContact, flowId, stepConfig)
                }

                "NAME_UPDATE_PREVIEW" -> {
                    val selectedName =
                        _uiState.value.activeFlow?.collectedData?.get("selectedContactName") as? String
                            ?: ""
                    val targetName =
                        _uiState.value.activeFlow?.collectedData?.get("targetName") as? String ?: ""

                    val selectedContactId =
                        _uiState.value.activeFlow?.collectedData?.get("selectedContactId") as? String
                            ?: ""

                    if (selectedContactId.isNotEmpty()) {
                        screenModelScope.launch {
                            try {
                                val walletAddresses =
                                    getWalletAddressesWithBlockchainByContactIdUseCase(
                                        selectedContactId
                                    )

                                _uiState.update { currentState ->
                                    currentState.copy(
                                        isLoading = false
                                    )
                                }

                                val parameters = mutableMapOf(
                                    "contact_id" to selectedContactId,
                                    "old_name" to selectedName,
                                    "new_name" to targetName,
                                    "blockchain_address_or_account_name" to "",
                                    "blockchain_network" to ""
                                )

                                if (walletAddresses.isNotEmpty()) {
                                    val firstAddress = walletAddresses.first()
                                    parameters["blockchain_address_or_account_name"] =
                                        firstAddress.walletAddress.address
                                    parameters["blockchain_network"] =
                                        firstAddress.blockchainType.name
                                }

                                val functionCall = FunctionCallRequest(
                                    name = "edit_contact_name",
                                    parameters = parameters,
                                    callId = Uuid.random().toString(),
                                    securityLevel = SecurityLevel.RequireConfirmation
                                )

                                val confirmationMessage = FunctionCallConfirmationRequiredMessage(
                                    id = Uuid.random().toString(),
                                    senderId = "ai",
                                    isFromUser = false,
                                    parentMessageId = messageId,
                                    functionCall = functionCall,
                                    confirmationPrompt = "Update $selectedName's name to $targetName?",
                                    functionDescription = "Update contact name from $selectedName to $targetName"
                                )

                                // Save the confirmation message
                                _uiState.value.conversationSession?.id?.let { sessionId ->
                                    chatHistoryRepository.saveMessage(
                                        sessionId,
                                        confirmationMessage
                                    )
                                }
                            } catch (e: Exception) {
                                Napier.e("Error fetching contact addresses for edit", e)

                                val functionCall = FunctionCallRequest(
                                    name = "edit_contact_name",
                                    parameters = mapOf(
                                        "contact_id" to selectedContactId,
                                        "old_name" to selectedName,
                                        "new_name" to targetName,
                                        "blockchain_address_or_account_name" to "",
                                        "blockchain_network" to ""
                                    ),
                                    callId = Uuid.random().toString(),
                                    securityLevel = SecurityLevel.RequireConfirmation
                                )

                                val confirmationMessage = FunctionCallConfirmationRequiredMessage(
                                    id = Uuid.random().toString(),
                                    senderId = "ai",
                                    isFromUser = false,
                                    parentMessageId = messageId,
                                    functionCall = functionCall,
                                    confirmationPrompt = "Update $selectedName's name to $targetName?",
                                    functionDescription = "Update contact name from $selectedName to $targetName"
                                )

                                _uiState.value.conversationSession?.id?.let { sessionId ->
                                    chatHistoryRepository.saveMessage(
                                        sessionId,
                                        confirmationMessage
                                    )
                                }
                            }
                        }
                    }
                }

                "EXECUTE_CONTACT_SEARCH" -> {
                    val searchCriteria = stepConfig?.get("searchCriteria")?.jsonObject
                    val contactName =
                        searchCriteria?.get("contactName")?.jsonPrimitive?.content ?: ""
                    val searchQuery =
                        searchCriteria?.get("searchQuery")?.jsonPrimitive?.content ?: contactName

                    try {
                        val filterCriteria = FilterCriteria(
                            searchQuery = searchQuery
                        )

                        val contactsResult = filterContactsUseCase(
                            filterCriteria = filterCriteria,
                            page = 0,
                            pageSize = 50
                        )

                        when {
                            contactsResult.isSuccess -> {
                                val contacts = contactsResult.getOrThrow()

                                // Store the search results in the flow's collected data
                                _uiState.value.activeFlow?.collectedData?.put(
                                    "searchResults",
                                    contacts
                                )
                                _uiState.value.activeFlow?.collectedData?.put(
                                    "searchQuery",
                                    searchQuery
                                )
                                _uiState.value.activeFlow?.collectedData?.put(
                                    "resultCount",
                                    contacts.size
                                )

                                val execution = stepConfig?.get("execution")?.jsonObject
                                val onSuccess = execution?.get("onSuccess")?.jsonObject
                                onSuccess?.let { successConfig ->
                                    executeStepActions(
                                        successConfig, mapOf(
                                            "searchResults" to contacts,
                                            "resultCount" to contacts.size,
                                            "searchQuery" to searchQuery,
                                            "queryType" to (searchCriteria?.get("queryType")?.jsonPrimitive?.content
                                                ?: "IMMEDIATE_SEARCH"),
                                            "flowId" to flowId,
                                            "executionTime" to Clock.System.now()
                                                .toEpochMilliseconds()
                                        )
                                    )
                                }

                                // Move to next step if configured
                                moveToNextFlowStep()
                            }

                            else -> {
                                // Execute onError actions from step config
                                val execution = stepConfig?.get("execution")?.jsonObject
                                val onError = execution?.get("onError")?.jsonObject
                                onError?.let { errorConfig ->
                                    executeStepActions(
                                        errorConfig, mapOf(
                                            "errorMessage" to (contactsResult.exceptionOrNull()?.message
                                                ?: "Search failed"),
                                            "flowId" to flowId
                                        )
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Napier.e("Error executing EXECUTE_CONTACT_SEARCH step", e)

                        // Execute onError actions
                        val execution = stepConfig?.get("execution")?.jsonObject
                        val onError = execution?.get("onError")?.jsonObject
                        onError?.let { errorConfig ->
                            executeStepActions(
                                errorConfig, mapOf(
                                    "errorMessage" to (e.message ?: "Unknown error"),
                                    "flowId" to flowId
                                )
                            )
                        }
                    }
                }

                "CONFIRM_DELETION" -> {
                    confirmDelete(messageId)
                }

                "SELECT_NETWORK" -> {
                    val uiConfig = stepConfig?.get("ui")?.jsonObject
                    val title = replaceTemplateVariables(
                        uiConfig?.get("title")?.jsonPrimitive?.content
                            ?: "Select Blockchain Network",
                        _uiState.value.activeFlow?.collectedData ?: emptyMap()
                    )
                    val subtitle = replaceTemplateVariables(
                        uiConfig?.get("subtitle")?.jsonPrimitive?.content ?: "Which network?",
                        _uiState.value.activeFlow?.collectedData ?: emptyMap()
                    )

                    _uiState.update { currentState ->
                        currentState.copy(
                            inputState = currentState.inputState.copy(
                                mode = InputMode.SelectNetwork,
                                networkSelectionMessage = subtitle
                            ),
                            activeFlow = currentState.activeFlow?.copy(
                                collectedData = currentState.activeFlow.collectedData.apply {
                                    put("_currentStepConfig", stepConfig.toString())
                                }
                            ),
                            isLoading = false
                        )
                    }

                    val networkSelectionMessage = TextMessage(
                        isFromUser = false,
                        senderId = "ai",
                        text = title,
                        timestamp = Clock.System.now()
                    )

                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, networkSelectionMessage)
                    }
                }

                "INPUT_ADDRESS" -> {
                    // Handle address input step
                    val uiConfig = stepConfig?.get("ui")?.jsonObject
                    val field = uiConfig?.get("field")?.jsonObject

                    val selectedNetworkId =
                        _uiState.value.activeFlow?.collectedData?.get("selectedNetwork") as? String
                    val selectedNetwork = selectedNetworkId?.let { id ->
                        BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true)
                            .find { it.blockChainUid == id }
                    } ?: run {
                        val prefilledNetwork =  _uiState.value.activeFlow?.collectedData?.get("preFilledNetwork") as? String
                        val network = prefilledNetwork?.let { networkName ->
                            BlockchainNetworkData.getBlockchainByNameOrAbbreviation(networkName, includeDebugNetworks = true)
                        }
                        network?.let {
                            _uiState.value.activeFlow?.collectedData?.put("selectedNetwork", it.blockChainUid)
                        }
                        network
                    }

                    if (selectedNetwork == null) {
                        Napier.e("No network selected for address input")
                        return@launch
                    }

                    val contactName =
                        _uiState.value.activeFlow?.collectedData?.get("contactName") as? String
                            ?: ""

                    val title = replaceTemplateVariables(
                        uiConfig?.get("title")?.jsonPrimitive?.content ?: "Enter Address",
                        mapOf(
                            "selectedNetwork.name" to selectedNetwork.name,
                            "contactName" to contactName
                        )
                    )

                    val subtitle = replaceTemplateVariables(
                        uiConfig?.get("subtitle")?.jsonPrimitive?.content ?: "Address for contact",
                        mapOf(
                            "selectedNetwork.name" to selectedNetwork.name,
                            "contactName" to contactName
                        )
                    )

                    // Set address input mode
                    setAddressInputMode(selectedNetwork.name)

                    // Show address input message
                    val addressInputMessage = TextMessage(
                        isFromUser = false,
                        senderId = "ai",
                        text = "$title\n$subtitle",
                        timestamp = Clock.System.now()
                    )

                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, addressInputMessage)
                    }
                }

                "CONFIRM_CREATION" -> {
                    // Handle contact creation confirmation
                    val uiConfig = stepConfig?.get("ui")?.jsonObject
                    val preview = uiConfig?.get("preview")?.jsonObject

                    val contactName =
                        _uiState.value.activeFlow?.collectedData?.get("contactName") as? String
                            ?: ""
                    val address =
                        _uiState.value.activeFlow?.collectedData?.get("address") as? String ?: ""
                    val selectedNetworkId =
                        _uiState.value.activeFlow?.collectedData?.get("selectedNetwork") as? String
                    val selectedNetwork = selectedNetworkId?.let { id ->
                        BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true)
                            .find { it.blockChainUid == id }
                    }

                    if (selectedNetwork == null || address.isEmpty()) {
                        Napier.e("Missing required data for contact creation confirmation")
                        return@launch
                    }

                    // Create function call for contact creation confirmation
                    val functionCall = FunctionCallRequest(
                        name = "add_contact",
                        parameters = mapOf(
                            "name" to contactName,
                            "blockchain_address_or_account_name" to address,
                            "blockchain_network" to selectedNetwork.name
                        ),
                        callId = Uuid.random().toString(),
                        securityLevel = SecurityLevel.RequireConfirmation
                    )

                    val confirmationMessage = FunctionCallConfirmationRequiredMessage(
                        id = Uuid.random().toString(),
                        senderId = "ai",
                        isFromUser = false,
                        parentMessageId = messageId,
                        functionCall = functionCall,
                        confirmationPrompt = "Create contact '$contactName' on ${selectedNetwork.name}?",
                        functionDescription = "Create new contact with the provided details"
                    )

                    // Save the confirmation message
                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, confirmationMessage)
                    }
                }

                "EXECUTE_LOCAL_CREATION" -> {
                    val contactName =
                        _uiState.value.activeFlow?.collectedData?.get("contactName") as? String
                            ?: ""
                    val address =
                        _uiState.value.activeFlow?.collectedData?.get("address") as? String ?: ""
                    val selectedNetworkId =
                        _uiState.value.activeFlow?.collectedData?.get("selectedNetwork") as? String
                    val selectedNetwork = selectedNetworkId?.let { id ->
                        BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true)
                            .find { it.blockChainUid == id }
                    }

                    if (selectedNetwork == null || address.isEmpty() || contactName.isEmpty()) {
                        Napier.e("Missing required data for contact creation")
                        val errorMessage = TextMessage(
                            isFromUser = false,
                            senderId = "ai",
                            text = "Error: Missing required information to create contact.",
                            timestamp = Clock.System.now()
                        )
                        _uiState.value.conversationSession?.id?.let { sessionId ->
                            chatHistoryRepository.saveMessage(sessionId, errorMessage)
                        }
                        _uiState.update { currentState ->
                            currentState.copy(activeFlow = null)
                        }
                        return@launch
                    }

                    try {
                        val result = createContactWithAddressUseCase(
                            contactName = contactName,
                            address = address,
                            blockchainTypeId = selectedNetwork.blockChainUid
                        )

                        if (result.isFailure) {
                            throw result.exceptionOrNull() ?: Exception("Failed to create contact")
                        }

                        val successMessage = TextMessage(
                            isFromUser = false,
                            senderId = "ai",
                            text = "✓ Contact '$contactName' created successfully on ${selectedNetwork.name} network!",
                            timestamp = Clock.System.now()
                        )

                        _uiState.value.conversationSession?.id?.let { sessionId ->
                            chatHistoryRepository.saveMessage(sessionId, successMessage)
                        }

                        _uiState.update { currentState ->
                            currentState.copy(activeFlow = null)
                        }

                    } catch (e: Exception) {
                        Napier.e("Error creating contact", e)

                        val errorMessage = TextMessage(
                            isFromUser = false,
                            senderId = "ai",
                            text = "Error creating contact: ${e.message}",
                            timestamp = Clock.System.now()
                        )

                        _uiState.value.conversationSession?.id?.let { sessionId ->
                            chatHistoryRepository.saveMessage(sessionId, errorMessage)
                        }

                        // Clear the flow
                        _uiState.update { currentState ->
                            currentState.copy(activeFlow = null)
                        }
                    }
                }

                "GET_LOCAL_DATA" -> {
                    val blockchainUid = getSelectedNetworkUseCase().blockChainUid
                    val blockchainType = BlockchainType.fromUid(blockchainUid)

                    when(flowId) {
                        "receive_native_token" -> {
                            showAccountSelectorForNetwork(
                                blockchainType,
                                onSingleAccountSelected = {
                                    navigateToReceiveTokenScreen(it)
                                },
                                onMultipleAccountsMatch = { accounts ->
                                    _uiState.update {
                                        it.copy(
                                            accountSelectorState = it.accountSelectorState.copy(
                                                isLoading = false,
                                                accounts = accounts,
                                                error = null
                                            )
                                        )
                                    }
                                }
                            )
                        }
                        else -> {
                            _uiState.value.activeFlow?.collectedData?.put("blockchainType", blockchainUid)
                            val title = "Select account to send tokens from"
                            showAccountSelectorForNetwork(
                                blockchainType = blockchainType,
                                onSingleAccountSelected = { account ->
                                    _uiState.value.activeFlow?.collectedData?.put("selectedAccount", account)
                                    moveToNextFlowStep()
                                },
                                onMultipleAccountsMatch = { accounts ->
                                    _uiState.update {
                                        it.copy(
                                            accountSelectorState = it.accountSelectorState.copy(
                                                isLoading = false,
                                                accounts = accounts,
                                                error = null
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                "CONDITIONAL_LOGIC" -> {
                    // Handle conditional logic step
                    val condition = stepConfig?.get("condition")?.jsonPrimitive?.content
                    val field = stepConfig?.get("field")?.jsonPrimitive?.content
                    val operator = stepConfig?.get("operator")?.jsonPrimitive?.content
                    val value = stepConfig?.get("value")?.jsonPrimitive?.content
                    val branches = stepConfig?.get("branches")?.jsonObject

                    if (stepId.contains("validate_token")) {
                        try {
                            val conditionMet = if (condition != null && condition.contains("availableTokens.find")) {
                                // Handle complex token validation condition
                                val tokenSymbol = extractTokenSymbolFromCondition(condition)

                                if (tokenSymbol.isEmpty()) {
                                    showAssetPicker(stepConfig, branches, flowId, stepId)
                                    return@launch
                                }

                                val flowData = _uiState.value.activeFlow?.collectedData ?: return@launch
                                val blockchainTypeUid = flowData["blockchainType"] as? String ?: return@launch
                                val blockchainType = BlockchainType.fromUid(blockchainTypeUid)

                                val availableTokens = getAntelopeAccountTokenBalanceUseCase(
                                    accountName = (_uiState.value.activeFlow?.collectedData?.get("selectedAccount") as? AntelopeAccount)?.accountName.orEmpty(),
                                    blockchainType = blockchainType,
                                    forceRefresh = false
                                ).getOrElse {
                                    emptyList()
                                }

                                val tokenExists = availableTokens.any { it.symbol == tokenSymbol }

                                if (tokenExists) {
                                    val foundToken = availableTokens.find { it.symbol == tokenSymbol }
                                    foundToken?.let { token ->
                                        _uiState.update { currentState ->
                                            val updatedFlow = currentState.activeFlow?.copy(
                                                collectedData = currentState.activeFlow.collectedData.apply {
                                                    put("selectedToken", tokenSymbol)
                                                    put("tokenSymbol", tokenSymbol) // For send flow
                                                    put("selectedAssetBalance", token.amount.toString())
                                                    put("contract", token.contract)
                                                }
                                            )
                                            currentState.copy(activeFlow = updatedFlow)
                                        }
                                    }
                                }

                                tokenExists
                            } else {
                                // Handle simple field-based conditions
                                val flowData = _uiState.value.activeFlow?.collectedData
                                val fieldValue = flowData?.get(field)?.toString()

                                when (operator) {
                                    "exists" -> !fieldValue.isNullOrEmpty()
                                    "equals" -> fieldValue == value
                                    "not_equals" -> fieldValue != value
                                    "contains" -> fieldValue?.contains(value ?: "") == true
                                    else -> false
                                }
                            }

                            if (conditionMet) {
                                // Handle success branch
                                val defaultBranch = branches?.get("default")?.jsonObject
                                val action = defaultBranch?.get("action")?.jsonPrimitive?.content

                                when (action) {
                                    "SET_VARIABLE" -> {
                                        val variable = defaultBranch.get("variable")?.jsonPrimitive?.content
                                        val variableValue = defaultBranch.get("value")?.jsonPrimitive?.content
                                        val processedVariableValue = if (variable == "selectedToken") {
                                            // Hardcoded to fix an issue from BE
                                            extractTokenSymbolFromCondition(variableValue.orEmpty())
                                        } else {
                                            variableValue
                                        }

                                        if (variable != null) {
                                            _uiState.update { currentState ->
                                                val updatedFlow = currentState.activeFlow?.copy(
                                                    collectedData = currentState.activeFlow.collectedData.apply {
                                                        put(variable, processedVariableValue.orEmpty())
                                                    }
                                                )
                                                currentState.copy(activeFlow = updatedFlow)
                                            }
                                        }
                                    }
                                }

                                moveToNextFlowStep()
                            } else {
                                val nullBranch = branches?.get("null")?.jsonObject
                                val onFalse = stepConfig?.get("onFalse")?.jsonObject
                                val branchToUse = nullBranch ?: onFalse
                                val action = branchToUse?.get("action")?.jsonPrimitive?.content
                                val shouldAbort = branchToUse?.get("abort")?.jsonPrimitive?.boolean ?: false

                                when (action) {
                                    "TERMINATE_FLOW" -> {
                                        _uiState.update { currentState ->
                                            currentState.copy(activeFlow = null)
                                        }
                                    }

                                    "SHOW_ERROR" -> {
                                        val errorMessage = branchToUse.get("message")?.jsonPrimitive?.content
                                            ?: "Condition not met: $condition"

                                        val textMessage = TextMessage(
                                            isFromUser = false,
                                            senderId = "ai",
                                            text = errorMessage,
                                            timestamp = Clock.System.now()
                                        )

                                        _uiState.value.conversationSession?.id?.let { sessionId ->
                                            chatHistoryRepository.saveMessage(sessionId, textMessage)
                                        }

                                        if (shouldAbort) {
                                            // Clear flow if abort is true
                                            _uiState.update { currentState ->
                                                currentState.copy(activeFlow = null)
                                            }
                                        } else {
                                            moveToNextFlowStep()
                                        }
                                    }

                                    else -> {
                                        moveToNextFlowStep()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Napier.e("Error executing conditional logic", e)
                            moveToNextFlowStep()
                        }
                    } else if (stepId.contains("validate_balance")) {
                        val selectedToken = (_uiState.value.activeFlow?.collectedData?.get("selectedToken") ?: _uiState.value.activeFlow?.collectedData?.get("selectedAssetSymbol")) as? String
                        val sendAmount = _uiState.value.activeFlow?.collectedData?.get("sendAmount") as? String
                        val balance = _uiState.value.activeFlow?.collectedData?.get("selectedAssetBalance") as? String ?: "0"
                        
                        if (selectedToken == null) {
                            moveToNextFlowStep()
                            return@launch
                        }
                        
                        if (sendAmount.isNullOrEmpty()) {
                            val session = _uiState.value.conversationSession ?: return@launch
                            val updatedMetadata = session.metadata + ("amount_in_progress" to "true")
                            chatHistoryRepository.updateSessionMetadata(session.id, updatedMetadata)
                            setAmountInputMode(selectedToken, balance)
                            
                            val amountMessage = TextMessage(
                                isFromUser = false,
                                senderId = "ai",
                                text = "Enter the amount of $selectedToken to send",
                                timestamp = Clock.System.now()
                            )
                            
                            _uiState.value.conversationSession?.id?.let { sessionId ->
                                chatHistoryRepository.saveMessage(sessionId, amountMessage)
                            }
                            return@launch
                        }
                        
                        // We have sendAmount, validate balance
                        val tokenBalance = when (selectedToken) {
                            is Map<*, *> -> (selectedToken["balance"] as? String)?.toDoubleOrNull() ?: 0.0
                            else -> 0.0
                        }
                        
                        val amountToSend = sendAmount.toDoubleOrNull() ?: 0.0
                        
                        if (amountToSend > tokenBalance) {
                            val falseBranch = branches?.get("false")?.jsonObject
                            val action = falseBranch?.get("action")?.jsonPrimitive?.content
                            val message = falseBranch?.get("message")?.jsonPrimitive?.content
                            val shouldAbort = falseBranch?.get("abort")?.jsonPrimitive?.boolean ?: false
                            
                            when (action) {
                                "SHOW_ERROR" -> {
                                    // Replace template variables in message
                                    val errorMsg = message?.replace("{selectedToken.balance}", tokenBalance.toString())
                                        ?.replace("{sendAmount}", sendAmount) ?: "Insufficient balance"
                                    
                                    val errorMessage = TextMessage(
                                        isFromUser = false,
                                        senderId = "ai",
                                        text = errorMsg,
                                        timestamp = Clock.System.now()
                                    )
                                    
                                    _uiState.value.conversationSession?.id?.let { sessionId ->
                                        chatHistoryRepository.saveMessage(sessionId, errorMessage)
                                    }
                                    
                                    if (shouldAbort) {
                                        _uiState.update { currentState ->
                                            currentState.copy(activeFlow = null)
                                        }
                                    }
                                }
                            }
                        } else {
                            val trueBranch = branches?.get("true")?.jsonObject
                            val action = trueBranch?.get("action")?.jsonPrimitive?.content
                            val variable = trueBranch?.get("variable")?.jsonPrimitive?.content
                            val value = trueBranch?.get("value")?.jsonPrimitive?.content
                            
                            when (action) {
                                "SET_VARIABLE" -> {
                                    if (variable == "sendAmount" && !value.isNullOrEmpty()) {
                                        _uiState.value.activeFlow?.collectedData?.put("sendAmount", value)
                                    }
                                }
                            }
                            
                            moveToNextFlowStep()
                        }
                    }
                }

                "SECURITY_AUTHENTICATION" -> {
                    val securityLevel =
                        stepConfig?.get("securityLevel")?.jsonPrimitive?.content ?: "PIN"

                    val authMessage = TextMessage(
                        isFromUser = false,
                        senderId = "ai",
                        text = "Please authenticate with your $securityLevel to continue with the transaction.",
                        timestamp = Clock.System.now()
                    )

                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, authMessage)
                    }

                    // For demo purposes, auto-proceed (in real implementation, this would trigger biometric/PIN auth)
                    _uiState.value.activeFlow?.collectedData?.put("authenticated", true)
                    moveToNextFlowStep()
                }

                "EXECUTE_BLOCKCHAIN_TRANSACTION" -> {
                    // Handle actual blockchain transaction execution
                    val flowData = _uiState.value.activeFlow?.collectedData
                    val amount = flowData?.get("sendAmount") as? String ?: "0"
                    val tokenSymbol = flowData?.get("tokenSymbol") as? String ?: "EOS"
                    val recipientAddress = flowData?.get("recipientAddress") as? String ?: ""
                    val memo = flowData?.get("memo") as? String ?: ""
                    val authenticated = flowData?.get("authenticated") as? Boolean ?: false

                    if (!authenticated) {
                        val errorMessage = TextMessage(
                            isFromUser = false,
                            senderId = "ai",
                            text = "Transaction failed: User not authenticated",
                            timestamp = Clock.System.now()
                        )
                        _uiState.value.conversationSession?.id?.let { sessionId ->
                            chatHistoryRepository.saveMessage(sessionId, errorMessage)
                        }
                        _uiState.update { currentState ->
                            currentState.copy(activeFlow = null)
                        }
                        return@launch
                    }

                    try {
                        // Mock transaction execution for demonstration
                        // In real implementation, this would call the actual blockchain transaction service
                        val transactionId = "tx_${Clock.System.now().toEpochMilliseconds()}"

                        val successMessage = TextMessage(
                            isFromUser = false,
                            senderId = "ai",
                            text = "✅ Transaction successful!\n\nTransaction ID: $transactionId\nSent: $amount $tokenSymbol\nTo: $recipientAddress\nMemo: ${if (memo.isNotEmpty()) memo else "(none)"}",
                            timestamp = Clock.System.now()
                        )

                        _uiState.value.conversationSession?.id?.let { sessionId ->
                            chatHistoryRepository.saveMessage(sessionId, successMessage)
                        }

                        // Clear the flow after successful transaction
                        _uiState.update { currentState ->
                            currentState.copy(activeFlow = null)
                        }

                    } catch (e: Exception) {
                        Napier.e("Error executing blockchain transaction", e)
                        val errorMessage = TextMessage(
                            isFromUser = false,
                            senderId = "ai",
                            text = "❌ Transaction failed: ${e.message}",
                            timestamp = Clock.System.now()
                        )
                        _uiState.value.conversationSession?.id?.let { sessionId ->
                            chatHistoryRepository.saveMessage(sessionId, errorMessage)
                        }

                        // Clear the flow on error
                        _uiState.update { currentState ->
                            currentState.copy(activeFlow = null)
                        }
                    }
                }

                "INPUT_CONTACT_NAME" -> {
                    val uiConfig = stepConfig?.get("ui")?.jsonObject
                    val field = uiConfig?.get("field")?.jsonObject
                    
                    val title = replaceTemplateVariables(
                        uiConfig?.get("title")?.jsonPrimitive?.content ?: "Enter Contact Name",
                        _uiState.value.activeFlow?.collectedData ?: emptyMap()
                    )
                    
                    val placeholder = replaceTemplateVariables(
                        field?.get("placeholder")?.jsonPrimitive?.content ?: "Enter contact name",
                        _uiState.value.activeFlow?.collectedData ?: emptyMap()
                    )
                    
                    val label = replaceTemplateVariables(
                        field?.get("label")?.jsonPrimitive?.content ?: "Contact Name",
                        _uiState.value.activeFlow?.collectedData ?: emptyMap()
                    )
                    
                    val defaultValue = field?.get("defaultValue")?.jsonPrimitive?.content ?: ""
                    
                    _uiState.update { currentState ->
                        currentState.copy(
                            inputState = currentState.inputState.copy(
                                mode = InputMode.ContactName(
                                    placeholder = placeholder,
                                    label = label,
                                    defaultValue = defaultValue
                                ),
                                messageText = defaultValue
                            ),
                            activeFlow = currentState.activeFlow?.copy(
                                collectedData = currentState.activeFlow.collectedData.apply {
                                    put("_currentStepConfig", stepConfig.toString())
                                }
                            ),
                            isLoading = false
                        )
                    }
                    
                    // Create and save the input request message
                    val inputMessage = TextMessage(
                        isFromUser = false,
                        senderId = "ai",
                        text = title,
                        timestamp = Clock.System.now()
                    )
                    
                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, inputMessage)
                    }
                }

                "FLOW_COMPLETION" -> {
                    _uiState.update { currentState ->
                        currentState.copy(activeFlow = null)
                    }
                }

                else -> {
                    Napier.w("Unknown flow step type: $stepType")
                }
            }
        }
    }

    private suspend fun handleFindContactLocal(stepConfig: JsonObject?) {
        val config = stepConfig ?: JsonObject(emptyMap())
        val query = config["searchQuery"]?.jsonPrimitive?.content
        val resultHandling = config["resultHandling"]?.jsonObject

        if (query.isNullOrEmpty()) {
            val allContactsResult = filterContactsUseCase(
                filterCriteria = FilterCriteria(searchQuery = ""),
                page = 0,
                pageSize = 50
            )
            val allContacts = allContactsResult.getOrNull() ?: emptyList()

            if (allContacts.isEmpty()) {
                val action =
                    resultHandling?.get("0")?.jsonObject?.get("action")?.jsonPrimitive?.content
                val message =
                    resultHandling?.get("0")?.jsonObject?.get("message")?.jsonPrimitive?.content

                if (action == "SHOW_ADDRESS_INPUT" && message != null) {
                    val errorMessage = TextMessage(
                        isFromUser = false,
                        senderId = "ai",
                        text = message,
                        timestamp = Clock.System.now()
                    )
                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, errorMessage)
                    }
                    val blockchainUid = _uiState.value.activeFlow?.collectedData?.get("blockchainType") as? String
                    val networkName = BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true)
                        .find { it.blockChainUid == blockchainUid }?.name ?: return

                    setAddressInputMode(networkName)
                }
            } else {
                showContactSelector(
                    allContacts,
                    "Select a recipient",
                    "Choose a contact to send tokens to",
                    allowManualAddressInput = true
                )
            }
            return
        }

        val flowData = _uiState.value.activeFlow?.collectedData ?: return
        val blockchainTypeUid = flowData["blockchainType"] as? String

        val filterCriteria = FilterCriteria(
            searchQuery = query,
            blockchainIds = blockchainTypeUid?.let { listOf(it) } ?: emptyList(),
        )
        val contactsResult = filterContactsUseCase(
            filterCriteria = filterCriteria,
            page = 0,
            pageSize = 10
        )
        val contacts = contactsResult.getOrNull() ?: emptyList()

        when (contacts.size) {
            0 -> {
                // No matches found
                val action =
                    resultHandling?.get("0")?.jsonObject?.get("action")?.jsonPrimitive?.content
                val message =
                    resultHandling?.get("0")?.jsonObject?.get("message")?.jsonPrimitive?.content

                if (action == "SHOW_ADDRESS_INPUT" && message != null) {
                    val errorMessage = TextMessage(
                        isFromUser = false,
                        senderId = "ai",
                        text = message,
                        timestamp = Clock.System.now()
                    )
                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, errorMessage)
                    }

                    val blockchainUid = _uiState.value.activeFlow?.collectedData?.get("blockchainType") as? String
                    val networkName = BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true)
                        .find { it.blockChainUid == blockchainUid }?.name ?: return

                    setAddressInputMode(networkName)
                }
            }

            1 -> {
                val contact = contacts.first()
                val flowData = _uiState.value.activeFlow?.collectedData
                val senderAccountName =
                    (flowData?.get("selectedAccount") as? AntelopeAccount)?.accountName.orEmpty()
                val recipientAddress = contact.walletAddress

                if (senderAccountName.equals(recipientAddress, ignoreCase = true)) {
                    val errorMessage = TextMessage(
                        isFromUser = false,
                        senderId = "ai",
                        text = "❌ You cannot send tokens to yourself. The recipient account name must be different from your account name.",
                        timestamp = Clock.System.now()
                    )
                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, errorMessage)
                    }
                    _uiState.update { currentState ->
                        currentState.copy(activeFlow = null)
                    }
                    return
                }

                // Store contact data and move to next step
                _uiState.value.activeFlow?.collectedData?.put(
                    "selectedContactId",
                    contact.contactId
                )
                _uiState.value.activeFlow?.collectedData?.put(
                    "selectedContactName",
                    contact.contactName
                )
                _uiState.value.activeFlow?.collectedData?.put(
                    "recipientAddress",
                    contact.walletAddress
                )
                moveToNextFlowStep()
            }

            else -> {
                val action =
                    resultHandling?.get("multiple")?.jsonObject?.get("action")?.jsonPrimitive?.content

                if (action == "SHOW_CONTACT_SELECTOR") {
                    showContactSelector(
                        contacts,
                        "Multiple contacts found",
                        "Select the recipient for your transaction"
                    )
                }
            }
        }
    }

    private fun shouldExecuteStep(showWhen: String?): Boolean {
        if (showWhen == null) return true

        return _uiState.value.activeFlow?.collectedData?.get(showWhen) as? Boolean ?: false
    }

    private suspend fun ConversationUiScreenModel.searchContacts(
        searchQuery: String,
        responses: JsonObject?,
        targetContact: String,
        flowId: String,
        stepConfig: JsonObject?
    ) {
        try {
            val filterCriteria = FilterCriteria(searchQuery = searchQuery)

            val contactsResult = filterContactsUseCase(
                filterCriteria = filterCriteria,
                page = 0,
                pageSize = 10
            )

            when {
                contactsResult.isSuccess -> {
                    val contacts = contactsResult.getOrThrow()

                    when {
                        contacts.isEmpty() -> {
                            // Check if the database is completely empty
                            val allContactsResult = getAllContactsUseCase()
                            val allContacts = allContactsResult.first()
                            val totalContactCount = allContacts.size

                            if (totalContactCount == 0) {
                                // Database is empty, generate some test contacts
                                val generateMessage = TextMessage(
                                    isFromUser = false,
                                    senderId = "ai",
                                    text = "I notice you don't have any contacts yet.",
                                    timestamp = Clock.System.now()
                                )

                                _uiState.value.conversationSession?.id?.let { sessionId ->
                                    chatHistoryRepository.saveMessage(
                                        sessionId,
                                        generateMessage
                                    )
                                }

                                // Generate test contacts with names that match common search queries
                                try {
                                    val retryResult = filterContactsUseCase(
                                        filterCriteria = filterCriteria,
                                        page = 0,
                                        pageSize = 10
                                    )

                                    val retryContacts =
                                        retryResult.getOrElse { emptyList() }
                                    if (retryContacts.isNotEmpty()) {
                                        // Process the found contacts normally
    //                                                    handleFoundContacts(retryContacts, responses, targetContact, flowId)
                                        return
                                    }
                                } catch (e: Exception) {
                                    Napier.e("Error generating test contacts", e)
                                    val errorMessage = TextMessage(
                                        isFromUser = false,
                                        senderId = "ai",
                                        text = "I had trouble finding contact",
                                        timestamp = Clock.System.now()
                                    )

                                    _uiState.value.conversationSession?.id?.let { sessionId ->
                                        chatHistoryRepository.saveMessage(
                                            sessionId,
                                            errorMessage
                                        )
                                    }
                                }
                            }

                            // No contacts found - use dynamic message from JSON config
                            val noMatchResponse = responses?.get("noMatch")?.jsonObject
                            val uiConfig = noMatchResponse?.get("ui")?.jsonObject
                            val messageTemplate =
                                uiConfig?.get("message")?.jsonPrimitive?.content
                                    ?: "No contact found with name '$targetContact'"

                            // Replace template variables
                            val finalMessage = messageTemplate
                                .replace(
                                    "{extractedData.targetContact || targetContact}",
                                    targetContact
                                )
                                .replace("{targetContact}", targetContact)

                            val noContactMessage = TextMessage(
                                isFromUser = false,
                                senderId = "ai",
                                text = finalMessage,
                                timestamp = Clock.System.now()
                            )

                            _uiState.value.conversationSession?.id?.let { sessionId ->
                                chatHistoryRepository.saveMessage(
                                    sessionId,
                                    noContactMessage
                                )
                            }

                            // Check if there are actions to execute for noMatch
                            val actions = noMatchResponse?.get("actions")?.jsonArray
                            if (actions != null) {
                                executeResponseActions(
                                    actions, mapOf(
                                        "searchQuery" to searchQuery,
                                        "targetContact" to targetContact
                                    )
                                )
                            } else {
                                // Clear the flow since no contact was found and no actions specified
                                _uiState.update { currentState ->
                                    currentState.copy(activeFlow = null)
                                }
                            }
                        }

                        contacts.size == 1 -> {
                            // Single contact match - use dynamic response from JSON config
                            val singleMatchResponse =
                                responses?.get("singleMatch")?.jsonObject
                            val contact = contacts.first()

                            // Store contact data
                            _uiState.value.activeFlow?.collectedData?.put(
                                "selectedContactId",
                                contact.contactId
                            )
                            _uiState.value.activeFlow?.collectedData?.put(
                                "selectedContactName",
                                contact.contactName
                            )

                            // Check for actions in the response
                            val actions = singleMatchResponse?.get("actions")?.jsonArray
                            if (actions != null) {
                                executeResponseActions(
                                    actions, mapOf(
                                        "selectedContact" to contact,
                                        "contactId" to contact.contactId,
                                        "contactName" to contact.contactName
                                    )
                                )
                            } else {
                                // Default behavior: move to next step
                                moveToNextFlowStep()
                            }

                            // Check if there's a UI message to display
                            val uiConfig = singleMatchResponse?.get("ui")?.jsonObject
                            val messageTemplate =
                                uiConfig?.get("message")?.jsonPrimitive?.content
                            if (messageTemplate != null) {
                                val finalMessage = messageTemplate
                                    .replace("{contactName}", contact.contactName)
                                    .replace("{targetContact}", targetContact)

                                val singleMatchMessage = TextMessage(
                                    isFromUser = false,
                                    senderId = "ai",
                                    text = finalMessage,
                                    timestamp = Clock.System.now()
                                )

                                _uiState.value.conversationSession?.id?.let { sessionId ->
                                    chatHistoryRepository.saveMessage(
                                        sessionId,
                                        singleMatchMessage
                                    )
                                }
                            }
                        }

                        else -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isLoading = false
                                )
                            }
                            showContactSelector(
                                contacts,
                                title = "Multiple contacts found",
                                "Select the contact you want to edit"
                            )
                        }
                    }
                }

                else -> {
                    // Handle search error - check for error response config
                    val errorResponse = responses?.get("error")?.jsonObject
                    val uiConfig = errorResponse?.get("ui")?.jsonObject
                    val messageTemplate =
                        uiConfig?.get("message")?.jsonPrimitive?.content
                            ?: "Error searching for contacts: {errorMessage}"

                    val errorMsg =
                        contactsResult.exceptionOrNull()?.message ?: "Unknown error"
                    val finalMessage = messageTemplate
                        .replace("{errorMessage}", errorMsg)
                        .replace("{targetContact}", targetContact)

                    val errorMessage = TextMessage(
                        isFromUser = false,
                        senderId = "ai",
                        text = finalMessage,
                        timestamp = Clock.System.now()
                    )

                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, errorMessage)
                    }

                    // Execute error actions if specified
                    val actions = errorResponse?.get("actions")?.jsonArray
                    if (actions != null) {
                        executeResponseActions(
                            actions, mapOf(
                                "errorMessage" to errorMsg,
                                "targetContact" to targetContact
                            )
                        )
                    } else {
                        // Clear the flow on error
                        _uiState.update { currentState ->
                            currentState.copy(activeFlow = null)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Napier.e("Error executing FIND_LOCAL_CONTACT step", e)

            // Handle exception - check for error response config
            val responses = stepConfig?.get("responses")?.jsonObject
            val errorResponse = responses?.get("error")?.jsonObject
            val uiConfig = errorResponse?.get("ui")?.jsonObject
            val messageTemplate = uiConfig?.get("message")?.jsonPrimitive?.content
                ?: "Error searching for contacts: {errorMessage}"

            val finalMessage = messageTemplate
                .replace("{errorMessage}", e.message ?: "Unknown error")
                .replace("{targetContact}", targetContact)

            val errorMessage = TextMessage(
                isFromUser = false,
                senderId = "ai",
                text = finalMessage,
                timestamp = Clock.System.now()
            )

            _uiState.value.conversationSession?.id?.let { sessionId ->
                chatHistoryRepository.saveMessage(sessionId, errorMessage)
            }

            // Execute error actions if specified
            val actions = errorResponse?.get("actions")?.jsonArray
            if (actions != null) {
                executeResponseActions(
                    actions, mapOf(
                        "errorMessage" to (e.message ?: "Unknown error"),
                        "targetContact" to targetContact
                    )
                )
            } else {
                // Clear the flow on error
                _uiState.update { currentState ->
                    currentState.copy(activeFlow = null)
                }
            }
        }
    }

    private suspend fun executeResponseActions(
        actions: JsonArray,
        @Suppress("UNUSED_PARAMETER") context: Map<String, Any>
    ) {
        actions.forEach { actionElement ->
            try {
                when (val actionType = actionElement.jsonObject["action"]?.jsonPrimitive?.content) {
                    "STORE_AND_PROCEED" -> {
                        // Store the selected contact and move to next step
                        Napier.d("Executing STORE_AND_PROCEED action")
                        moveToNextFlowStep()
                    }

                    "SHOW_SELECTION_UI" -> {
                        // Show UI for user to select from multiple contacts
                        Napier.d("Executing SHOW_SELECTION_UI action")
                        // UI handling is already done in the main flow logic
                        // This action type is for documentation/configuration purposes
                    }

                    "TERMINATE_FLOW" -> {
                        // Terminate the current flow
                        Napier.d("Executing TERMINATE_FLOW action")
                        _uiState.update { currentState ->
                            currentState.copy(activeFlow = null)
                        }
                    }

                    "SEND_MESSAGE" -> {
                        // Send a custom message
                        val message = actionElement.jsonObject["message"]?.jsonPrimitive?.content
                        if (message != null) {
                            val textMessage = TextMessage(
                                isFromUser = false,
                                senderId = "ai",
                                text = message,
                                timestamp = Clock.System.now()
                            )

                            _uiState.value.conversationSession?.id?.let { sessionId ->
                                chatHistoryRepository.saveMessage(sessionId, textMessage)
                            }
                        }
                    }

                    else -> {
                        Napier.w("Unknown response action type: $actionType")
                    }
                }
            } catch (e: Exception) {
                Napier.e("Error executing response action", e)
            }
        }
    }

    private suspend fun executeStepActions(actionConfig: JsonObject, context: Map<String, Any>) {
        val actions = actionConfig["actions"]?.jsonArray

        actions?.forEach { actionElement ->
            try {
                val action = actionElement.jsonObject
                val actionType = action["type"]?.jsonPrimitive?.content
                val event = action["event"]?.jsonObject

                when (actionType) {
                    "SEND_EVENT" -> {
                        // Send an event with the search results
                        val eventType = event?.get("type")?.jsonPrimitive?.content
                        val eventData = event?.get("data")?.jsonObject

                        Napier.d("Sending event: $eventType with data: $eventData")

                        // Create a response message based on the event type
                        when (eventType) {
                            "EXECUTED_SUCCESS" -> {
                                val operation =
                                    eventData?.get("operation")?.jsonPrimitive?.content ?: "unknown"
                                val resultCount = context["resultCount"] as? Int ?: 0
                                val searchQuery = context["searchQuery"] as? String ?: ""

                                when (operation) {
                                    "GET_CONTACT" -> {
                                        // Create a ContactResultsMessage for contact search results
                                        @Suppress("UNCHECKED_CAST")
                                        val searchResults =
                                            context["searchResults"] as? List<ContactModel>

                                        if (searchResults != null) {
                                            // Convert ContactModel objects to ContactInfo for display
                                            val contactInfoList = searchResults.map { contact ->
                                                ContactInfo(
                                                    id = contact.contactId,
                                                    name = contact.contactName,
                                                    notes = null, // ContactModel doesn't have notes field
                                                    addresses = listOf(
                                                        ContactAddress(
                                                            address = contact.walletAddress,
                                                            network = contact.blockchainName
                                                        )
                                                    )
                                                )
                                            }

                                            val contactResultsMessage = ContactResultsMessage(
                                                id = Uuid.random().toString(),
                                                senderId = "ai",
                                                query = searchQuery,
                                                contacts = contactInfoList,
                                                totalCount = resultCount,
                                                displayText = if (resultCount > 0) {
                                                    "Found $resultCount contact(s) matching '$searchQuery'"
                                                } else {
                                                    "No contacts found matching '$searchQuery'"
                                                }
                                            )

                                            _uiState.value.conversationSession?.id?.let { sessionId ->
                                                chatHistoryRepository.saveMessage(
                                                    sessionId,
                                                    contactResultsMessage
                                                )
                                            }
                                        } else {
                                            // Fallback to simple text message if we don't have search results
                                            val responseText = if (resultCount > 0) {
                                                "Found $resultCount contact(s) matching '$searchQuery'."
                                            } else {
                                                "No contacts found matching '$searchQuery'."
                                            }

                                            val successMessage = TextMessage(
                                                isFromUser = false,
                                                senderId = "ai",
                                                text = responseText,
                                                timestamp = Clock.System.now()
                                            )

                                            _uiState.value.conversationSession?.id?.let { sessionId ->
                                                chatHistoryRepository.saveMessage(
                                                    sessionId,
                                                    successMessage
                                                )
                                            }
                                        }
                                    }

                                    else -> {
                                        // For other operations, use simple text message
                                        val responseText =
                                            "Operation $operation completed successfully. Found $resultCount results."
                                        val successMessage = TextMessage(
                                            isFromUser = false,
                                            senderId = "ai",
                                            text = responseText,
                                            timestamp = Clock.System.now()
                                        )

                                        _uiState.value.conversationSession?.id?.let { sessionId ->
                                            chatHistoryRepository.saveMessage(
                                                sessionId,
                                                successMessage
                                            )
                                        }
                                    }
                                }
                            }

                            "EXECUTED_FAILED" -> {
                                val operation =
                                    eventData?.get("operation")?.jsonPrimitive?.content ?: "unknown"
                                val errorMessage =
                                    context["errorMessage"] as? String ?: "Unknown error"

                                val failureMessage = TextMessage(
                                    isFromUser = false,
                                    senderId = "ai",
                                    text = "Failed to execute $operation: $errorMessage",
                                    timestamp = Clock.System.now()
                                )

                                _uiState.value.conversationSession?.id?.let { sessionId ->
                                    chatHistoryRepository.saveMessage(sessionId, failureMessage)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Napier.e("Error executing step action", e)
            }
        }
    }

    private suspend fun moveToNextFlowStep() {
        val activeFlow = _uiState.value.activeFlow ?: return

        val steps = activeFlow.flowDefinition["steps"]?.jsonArray
        if (steps == null || activeFlow.currentStepIndex >= steps.size - 1) {
            _uiState.update { currentState ->
                currentState.copy(activeFlow = null)
            }

            saveFlowState()
            return
        }

        val nextStepIndex = activeFlow.currentStepIndex + 1
        _uiState.update { currentState ->
            currentState.copy(
                activeFlow = activeFlow.copy(currentStepIndex = nextStepIndex)
            )
        }

        saveFlowState()

        val nextStep = steps[nextStepIndex].jsonObject
        val stepId = nextStep["stepId"]?.jsonPrimitive?.content
        val stepType = nextStep["type"]?.jsonPrimitive?.content
        val showWhen = nextStep["showWhen"]?.jsonPrimitive?.content

        if (stepId != null && stepType != null) {
            executeFlowStep(
                flowId = activeFlow.flowId,
                stepId = stepId,
                stepType = stepType,
                stepConfig = nextStep["config"]?.jsonObject,
                showWhen = showWhen,
                messageId = null
            )
        }
    }

    fun selectContact(contactId: String) {
        screenModelScope.launch {
            val selectorState = _uiState.value.contactSelectorState
            val activeFlow = _uiState.value.activeFlow

            if (!selectorState.isVisible || activeFlow == null) {
                return@launch
            }

            val selectedContact = selectorState.contacts.find { it.contactId == contactId }
            if (selectedContact == null) {
                Napier.e("Selected contact not found: $contactId")
                return@launch
            }

            val contactName = selectedContact.contactName
            activeFlow.collectedData["selectedContactId"] = selectedContact.contactId
            activeFlow.collectedData["selectedContactName"] = contactName
            activeFlow.collectedData["recipientAddress"] = selectedContact.walletAddress

            saveUserMessage(contactName)

            _uiState.update { currentState ->
                currentState.copy(
                    contactSelectorState = currentState.contactSelectorState.copy(
                        isVisible = false,
                        selectedContactId = contactId
                    )
                )
            }

            when (selectorState.onSelectAction) {
                "PROCEED_TO_STEP" -> {
                    val nextStepId = selectorState.nextStep
                    if (nextStepId != null) {
                        val steps = activeFlow.flowDefinition["steps"]?.jsonArray
                        steps?.forEachIndexed { index, stepElement ->
                            val step = stepElement.jsonObject
                            if (step["stepId"]?.jsonPrimitive?.content == nextStepId) {
                                _uiState.update { currentState ->
                                    currentState.copy(
                                        activeFlow = activeFlow.copy(currentStepIndex = index)
                                    )
                                }

                                val stepType = step["type"]?.jsonPrimitive?.content
                                if (stepType != null) {
                                    executeFlowStep(
                                        flowId = activeFlow.flowId,
                                        stepId = nextStepId,
                                        stepType = stepType,
                                        stepConfig = step["config"]?.jsonObject,
                                        showWhen = step["showWhen"]?.jsonPrimitive?.content,
                                        messageId = null
                                    )
                                }
                                return@forEachIndexed
                            }
                        }
                    }
                }

                else -> {
                    moveToNextFlowStep()
                }
            }
        }
    }

    fun cancelContactSelector() {
        val selectorState = _uiState.value.contactSelectorState

        if (!selectorState.isVisible) {
            return
        }

        // Hide the contact selector
        _uiState.update { currentState ->
            currentState.copy(
                contactSelectorState = currentState.contactSelectorState.copy(isVisible = false)
            )
        }

        // Handle the cancel action
        when (selectorState.onCancelAction) {
            "ABORT_FLOW" -> {
                // Clear the active flow
                _uiState.update { currentState ->
                    currentState.copy(activeFlow = null)
                }

                // Send a cancellation message if configured
                val cancelReason = selectorState.cancelReason
                if (cancelReason != null) {
                    val cancelMessage = TextMessage(
                        isFromUser = false,
                        senderId = "ai",
                        text = "Contact selection cancelled.",
                        timestamp = Clock.System.now()
                    )

                    screenModelScope.launch {
                        _uiState.value.conversationSession?.id?.let { sessionId ->
                            chatHistoryRepository.saveMessage(sessionId, cancelMessage)
                        }
                    }
                }
            }

            else -> {
                // Default: just hide the selector
                Napier.d("Contact selector cancelled with action: ${selectorState.onCancelAction}")
            }
        }
    }

    fun handleManualAddressInput() {
        screenModelScope.launch {
            val selectorState = _uiState.value.contactSelectorState

            if (!selectorState.isVisible) {
                return@launch
            }

            _uiState.update { currentState ->
                currentState.copy(
                    contactSelectorState = currentState.contactSelectorState.copy(isVisible = false)
                )
            }

            val activeFlow = _uiState.value.activeFlow
            activeFlow?.collectedData["NO_CONTACT_FOUND"] = true

            moveToNextFlowStep()
        }
    }

    fun storeFunctionCallSelection(messageId: String, selectionData: Map<String, Any?>) {
        _uiState.update { currentState ->
            currentState.copy(
                functionCallSelections = currentState.functionCallSelections + (messageId to selectionData)
            )
        }
    }

    private fun handleContactSelectionConfirm(
        messageId: String,
        functionCallRequest: FunctionCallRequest
    ) {
        val params = functionCallRequest.parameters
        val flowId = params["flowId"] as? String
        val nextStep = params["nextStep"] as? String
        val onSelectAction = params["onSelectAction"] as? String

        println("handleContactSelectionConfirm: function call parameters = $params")

        _uiState.update { currentState ->
            currentState.copy(
                processingMessageIds = currentState.processingMessageIds + messageId,
            )
        }

        // Try to get the selected contact ID from multiple sources
        val selectedContactId = params["selectedContactId"] as? String // Added by renderer
            ?: _uiState.value.functionCallSelections[messageId]?.get("selectedContactId") as? String // Fallback to stored selections

        println("handleContactSelectionConfirm: selectedContactId = $selectedContactId")

        if (selectedContactId != null && _uiState.value.activeFlow != null) {
            // Store the selected contact in flow data
            val activeFlow = _uiState.value.activeFlow!!

            // Try to get contact name from multiple sources
            screenModelScope.launch {
                try {
                    // First try from contacts parameter if available
                    @Suppress("UNCHECKED_CAST")
                    val contacts = params["contacts"] as? List<Map<String, Any>> ?: emptyList()
                    val selectedContact = contacts.find { it["contactId"] == selectedContactId }

                    val selectedContactName = if (selectedContact != null) {
                        selectedContact["contactName"] as? String ?: ""
                    } else {
                        // Fallback: get contact name from database
                        try {
                            val allContacts = getAllContactsUseCase.observeContacts().first()
                            allContacts.find { it.id == selectedContactId }?.name
                                ?: "Unknown Contact"
                        } catch (e: Exception) {
                            Napier.e("Error fetching contact name for ID $selectedContactId", e)
                            "Unknown Contact"
                        }
                    }

                    // Store the selection in flow data
                    activeFlow.collectedData["selectedContactId"] = selectedContactId
                    activeFlow.collectedData["selectedContactName"] = selectedContactName

                    println("handleContactSelectionConfirm: stored selectedContactId = $selectedContactId, selectedContactName = $selectedContactName")

                    // Update the function call execution status in the database
                    chatHistoryRepository.updateMessageExecutionStatus(
                        messageId,
                        ExecutionStatus.CONFIRMED
                    )

                    // Store the selection in the session metadata to persist across restarts
                    _uiState.value.conversationSession?.let { session ->
                        val updatedMetadata = session.metadata + mapOf(
                            "function_call_selection_$messageId" to buildJsonObject {
                                put("selectedContactId", JsonPrimitive(selectedContactId))
                                put("selectedContactName", JsonPrimitive(selectedContactName))
                            }.toString()
                        )
                        val updatedSession = session.copy(metadata = updatedMetadata)
                        chatHistoryRepository.updateSession(updatedSession)
                    }

                    _uiState.update { currentState ->
                        currentState.copy(
                            processingMessageIds = currentState.processingMessageIds - messageId
                        )
                    }

                    // Handle the action based on configuration
                    when (onSelectAction) {
                        "PROCEED_TO_STEP" -> {
                            if (nextStep != null && flowId != null) {
                                // Find the step index
                                val steps = activeFlow.flowDefinition["steps"]?.jsonArray
                                steps?.forEachIndexed { index, stepElement ->
                                    val step = stepElement.jsonObject
                                    if (step["stepId"]?.jsonPrimitive?.content == nextStep) {
                                        // Update flow to the specific step
                                        _uiState.update { currentState ->
                                            currentState.copy(
                                                activeFlow = activeFlow.copy(currentStepIndex = index)
                                            )
                                        }

                                        // Execute the step
                                        val stepType = step["type"]?.jsonPrimitive?.content
                                        if (stepType != null) {
                                            executeFlowStep(
                                                flowId = flowId,
                                                stepId = nextStep,
                                                stepType = stepType,
                                                stepConfig = step["config"]?.jsonObject,
                                                showWhen = step["showWhen"]?.jsonPrimitive?.content,
                                                messageId = null
                                            )
                                        }
                                        return@forEachIndexed
                                    }
                                }
                            }
                        }

                        else -> {
                            // Default: move to next step
                            moveToNextFlowStep()
                        }
                    }
                } catch (e: Exception) {
                    Napier.e("Error handling contact selection", e)

                    _uiState.update { currentState ->
                        currentState.copy(
                            processingMessageIds = currentState.processingMessageIds - messageId,
                            error = "Error processing contact selection: ${e.message}"
                        )
                    }
                }
            }
        } else {
            // Mark as FAILED if no contact selected
            _uiState.update { currentState ->
                currentState.copy(
                    processingMessageIds = currentState.processingMessageIds - messageId,
                    error = "Please select a contact to continue"
                )
            }
        }
    }

    private fun handleAssetSelectionConfirm(
        messageId: String,
        functionCallRequest: FunctionCallRequest
    ) {
        val params = functionCallRequest.parameters

        _uiState.update { currentState ->
            currentState.copy(
                processingMessageIds = currentState.processingMessageIds + messageId,
            )
        }

        val selectedAssetSymbol = params["selectedAssetSymbol"] as? String // Added by renderer
            ?: _uiState.value.functionCallSelections[messageId]?.get("selectedAssetSymbol") as? String // Fallback to stored selections

        if (selectedAssetSymbol != null && _uiState.value.activeFlow != null) {
            val activeFlow = _uiState.value.activeFlow!!

            screenModelScope.launch {
                try {
                    saveUserMessage(selectedAssetSymbol)

                    val selectedAssetDecimals = params["selectedAssetDecimals"] as? Int ?: 18
                    val selectedAssetFromPicker = (params["selectedAssetFromPicker"] as? Boolean) == true
                    val selectedAssetBalance = (params["selectedAssetBalance"] as? String) ?: false
                    val contract = (params["contract"] as? String) ?: false

                    activeFlow.collectedData["selectedAssetSymbol"] = selectedAssetSymbol
                    activeFlow.collectedData["selectedAssetDecimals"] = selectedAssetDecimals.toString()
                    activeFlow.collectedData["selectedAssetFromPicker"] = selectedAssetFromPicker
                    activeFlow.collectedData["selectedAssetBalance"] = selectedAssetBalance
                    activeFlow.collectedData["contract"] = contract

                    println("handleAssetSelectionConfirm: stored selectedAssetSymbol = $selectedAssetSymbol, selectedAssetDecimals = $selectedAssetDecimals")

                    chatHistoryRepository.updateMessageExecutionStatus(
                        messageId,
                        ExecutionStatus.CONFIRMED
                    )
                    val session = chatHistoryRepository.getSession(sessionId.orEmpty())
                    session?.let { session ->
                        val updatedMetadata = session.metadata + mapOf(
                            "function_call_selection_$messageId" to buildJsonObject {
                                put("selectedAssetSymbol", JsonPrimitive(selectedAssetSymbol))
                                put("selectedAssetDecimals", JsonPrimitive(selectedAssetDecimals))
                                put("selectedAssetFromPicker", JsonPrimitive(selectedAssetFromPicker))
                            }.toString()
                        )
                        val updatedSession = session.copy(metadata = updatedMetadata)
                        chatHistoryRepository.updateSession(updatedSession)
                        saveFlowState()
                    }

                    _uiState.update { currentState ->
                        currentState.copy(
                            processingMessageIds = currentState.processingMessageIds - messageId
                        )
                    }

                    if (activeFlow.currentStepIndex + 1 < (activeFlow.flowDefinition["steps"]?.jsonArray?.size ?: 0)) {
                        moveToNextFlowStep()
                    } else {
                        _uiState.update { currentState ->
                            currentState.copy(activeFlow = null)
                        }
                    }
                } catch (e: Exception) {
                    Napier.e("Error in handleAssetSelectionConfirm", e)
                    _uiState.update { currentState ->
                        currentState.copy(
                            processingMessageIds = currentState.processingMessageIds - messageId,
                            error = "Failed to process asset selection: ${e.message}"
                        )
                    }
                }
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    processingMessageIds = currentState.processingMessageIds - messageId,
                    error = "No asset selected or no active flow"
                )
            }
        }
    }

    private suspend fun handleSendTokenFlow(flowData: JsonObject, messageId: String?) {
        try {
            val parameters = flowData["parameters"]?.jsonObject
            val extractedData = flowData["extractedData"]?.jsonObject
            val steps = flowData["steps"]?.jsonArray
            val flowId = flowData["flowId"]?.jsonPrimitive?.content ?: "send_token_flow"

            val transactionData = TransactionFlowData()

            val flowState = FlowState(
                flowId = flowId,
                currentStepIndex = 0,
                flowDefinition = flowData,
                collectedData = mutableMapOf(),
                transactionData = transactionData
            )

            extractedData?.let { extractedObj ->
                extractedObj.keys.forEach { key ->
                    when (key) {
                        "sendAmount" -> {
                            val value = extractedObj[key]?.jsonPrimitive?.content
                            if (value != null) {
                                flowState.collectedData[key] = value
                                flowState.transactionData?.run {
                                    // Update transaction data with extracted amount
                                    copy(sendAmount = value)
                                }
                            }
                        }

                        "recipientAddress" -> {
                            val value = extractedObj[key]?.jsonPrimitive?.content
                            if (value != null) {
                                flowState.collectedData[key] = value
                                flowState.transactionData?.run {
                                    copy(recipientAddress = value)
                                }
                            }
                        }

                        "tokenSymbol" -> {
                            val value = extractedObj[key]?.jsonPrimitive?.content
                            if (value != null) {
                                flowState.collectedData[key] = value
                            }
                        }

                        else -> {
                            val value = extractedObj[key]?.jsonPrimitive?.content
                            if (value != null) {
                                flowState.collectedData[key] = value
                            }
                        }
                    }
                }
            }

            parameters?.let { params ->
                params.keys.forEach { key ->
                    val value = params[key]?.jsonPrimitive?.content
                    if (value != null) {
                        flowState.collectedData[key] = value
                    }
                }
            }

            _uiState.update { currentState ->
                currentState.copy(activeFlow = flowState)
            }

            saveFlowState()

            steps?.firstOrNull()?.let { firstStep ->
                val step = firstStep.jsonObject
                val stepId = step["stepId"]?.jsonPrimitive?.content
                val stepType = step["type"]?.jsonPrimitive?.content

                if (stepId != null && stepType != null) {
                    executeFlowStep(
                        flowId = flowId,
                        stepId = stepId,
                        stepType = stepType,
                        stepConfig = step["config"]?.jsonObject,
                        showWhen = step["showWhen"]?.jsonPrimitive?.content,
                        messageId = messageId
                    )
                }
            }
        } catch (e: Exception) {
            Napier.e("Error handling send token flow", e)

            // Show error message to user
            val errorMessage = TextMessage(
                isFromUser = false,
                senderId = "ai",
                text = "Error processing send token request: ${e.message}",
                timestamp = Clock.System.now()
            )

            _uiState.value.conversationSession?.id?.let { sessionId ->
                chatHistoryRepository.saveMessage(sessionId, errorMessage)
            }

            // Clear any active flow
            _uiState.update { currentState ->
                currentState.copy(activeFlow = null)
            }
        }
    }

    private suspend fun handleReceiveTokenFlow(flowData: JsonObject, messageId: String?) {
        try {
            val parameters = flowData["parameters"]?.jsonObject
            val sessionId =
                parameters?.get("sessionId")?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }
            val network =
                parameters?.get("network")?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }
            val tokenSymbol =
                parameters?.get("tokenSymbol")?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }
            val flowId = flowData["flowId"]?.jsonPrimitive?.content ?: "receive_token"
            val steps = flowData["steps"]?.jsonArray

            val transactionData = TransactionFlowData()

            val flowState = FlowState(
                flowId = flowId,
                currentStepIndex = 0,
                flowDefinition = flowData,
                collectedData = mutableMapOf(),
                transactionData = transactionData
            )

            parameters.let { params ->
                params?.keys?.forEach { key ->
                    val value = params[key]?.jsonPrimitive?.content
                    if (value != null) {
                        flowState.collectedData[key] = value
                    }
                }
            }

            _uiState.update { currentState ->
                currentState.copy(activeFlow = flowState)
            }

            saveFlowState()

            steps?.firstOrNull()?.let { firstStep ->
                val step = firstStep.jsonObject
                val stepId = step["stepId"]?.jsonPrimitive?.content
                val stepType = step["type"]?.jsonPrimitive?.content

                if (stepId != null && stepType != null) {
                    executeFlowStep(
                        flowId = flowId,
                        stepId = stepId,
                        stepType = stepType,
                        stepConfig = step["config"]?.jsonObject,
                        showWhen = step["showWhen"]?.jsonPrimitive?.content,
                        messageId = messageId
                    )
                }
            }
        } catch (e: Exception) {
            Napier.e("Error handling receive token flow", e)
            hideAccountSelector()
            showReceiveTokenError("Failed to process receive token request: ${e.message}")
        }
    }

    /**
     * Show account selector and fetch accounts
     */
    private suspend fun showAccountSelector(title: String = "Select Account") {
        try {
            _uiState.update {
                it.copy(
                    accountSelectorState = it.accountSelectorState.copy(
                        isVisible = true,
                        isLoading = true,
                        title = title,
                        error = null
                    )
                )
            }

            // Fetch accounts using GetAccountsUseCase
            val accounts = getAccountsUseCase.invoke(
                includeTempAccounts = false,
                includeIapInitializedAccounts = false
            )

            when {
                accounts.isEmpty() -> {
                    // No accounts found - show error
                    _uiState.update {
                        it.copy(
                            accountSelectorState = it.accountSelectorState.copy(
                                isLoading = false,
                                error = "No accounts found for current network",
                                accounts = emptyList()
                            )
                        )
                    }
                }

                accounts.size == 1 -> {
                    // Single account - auto-select and navigate
                    val account = accounts.first()
                    hideAccountSelector()
                    navigateToReceiveTokenScreen(account)
                }

                else -> {
                    // Multiple accounts - show selector
                    _uiState.update {
                        it.copy(
                            accountSelectorState = it.accountSelectorState.copy(
                                isLoading = false,
                                accounts = accounts,
                                error = null
                            )
                        )
                    }
                }
            }

        } catch (e: Exception) {
            Napier.e("Error fetching accounts", e)
            _uiState.update {
                it.copy(
                    accountSelectorState = it.accountSelectorState.copy(
                        isLoading = false,
                        error = "Failed to load accounts: ${e.message}",
                        accounts = emptyList()
                    )
                )
            }
        }
    }

    /**
     * Hide account selector
     */
    private fun hideAccountSelector() {
        _uiState.update {
            it.copy(
                accountSelectorState = AccountSelectorState()
            )
        }
    }

    /**
     * Handle account selection
     */
    fun onAccountSelected(account: AntelopeAccount) {
        hideAccountSelector()
        
        // Check if we're in an active flow and this is part of a flow step
        val activeFlow = _uiState.value.activeFlow
        if (activeFlow != null) {
            val steps = activeFlow.flowDefinition["steps"]?.jsonArray
            if (steps != null && activeFlow.currentStepIndex < steps.size) {
                val currentStep = steps[activeFlow.currentStepIndex].jsonObject
                val stepType = currentStep["type"]?.jsonPrimitive?.content
                
                // If this is a GET_LOCAL_DATA step, store the selected account and move to next step
                if (stepType == "GET_LOCAL_DATA") {
                    // Store the selected account in the flow data
                    activeFlow.collectedData["selectedAccount"] = account
                    
                    // Move to the next flow step
                    screenModelScope.launch {
                        moveToNextFlowStep()
                    }
                    return
                }
            }
        }
        
        navigateToReceiveTokenScreen(account)
    }

    private fun navigateToReceiveTokenScreen(account: AntelopeAccount) {
        screenModelScope.launch {
            try {
                val receiveTokenScreen = ReceiveNavigationEvent.NavigateToReceiveQr(
                    accountId = account.accountName,
                    networkType = NetworkType.ANTELOPE,
                    initialBlockchainUid = null
                )

                _navigationEvents.emit(NavigationEvent.GenericNavigationEvent(receiveTokenScreen))
            } catch (e: Exception) {
                Napier.e("Error navigating to ReceiveTokenScreen", e)
            }
        }
    }

    private suspend fun showAccountSelectorForNetwork(
        blockchainType: BlockchainType,
        onSingleAccountSelected: (suspend (AntelopeAccount) -> Unit)? = null,
        onMultipleAccountsMatch: ((List<AntelopeAccount>) -> Unit)? = null
    ) {
        try {
            val allAccounts = getAccountsUseCase.invoke(
                includeTempAccounts = false,
                includeIapInitializedAccounts = false,
                blockchainType
            )

            when {
                allAccounts.isEmpty() -> {
                    val errorMessage = TextMessage(
                        isFromUser = false,
                        senderId = "ai",
                        text = "No accounts found for ${blockchainType.name} network",
                        timestamp = Clock.System.now()
                    )

                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, errorMessage)
                    }
                }

                allAccounts.size == 1 -> {
                    val account = allAccounts.first()
                    onSingleAccountSelected?.invoke(account)
                }

                else -> {
                    onMultipleAccountsMatch?.invoke(allAccounts)
                }
            }

        } catch (e: Exception) {
            showReceiveTokenError("Failed to load accounts: ${e.message}")
        }
    }

    private fun showReceiveTokenError(errorMessage: String) {
        _uiState.update {
            it.copy(
                accountSelectorState = it.accountSelectorState.copy(
                    isVisible = true,
                    isLoading = false,
                    error = errorMessage,
                    accounts = emptyList()
                )
            )
        }
    }

    private suspend fun confirmTransaction(messageId: String?) {
        val flowData = _uiState.value.activeFlow?.collectedData
        val amount = flowData?.get("sendAmount") as? String ?: "0"
        val tokenLogoUrl = flowData?.get("tokenLogoUrl") as? String ?: ""
        val tokenSymbol = flowData?.get("tokenSymbol") as? String ?: (flowData?.get("selectedAssetSymbol") as? String).orEmpty()
        val recipientAddress = (flowData?.get("recipientAddress") as? String).orEmpty()
        val senderAccountName = (flowData?.get("selectedAccount") as? AntelopeAccount)?.accountName.orEmpty()
        val selectedContactName = flowData?.get("selectedContactName") as? String ?: recipientAddress
        val memo = flowData?.get("memo") as? String ?: ""
        val blockchainTypeUid = flowData?.get("blockchainType") as? String
        
        var preparedTransaction: Transaction? = null
        
        val calculatedFees = try {
            val contract = flowData?.get("contract") as? String ?: return
            val blockchainType = BlockchainType.fromUid(blockchainTypeUid.orEmpty())
            
            if (senderAccountName.isNotEmpty() && recipientAddress.isNotEmpty() && amount.isNotEmpty()) {
                val quantity = Balance(amount.toDoubleOrNull() ?: 0.0, tokenSymbol)

                _uiState.update { it.copy(isLoading = true) }
                
                val feeEstimationResult = antelopeSendCryptoUseCase.requestSendTransaction(
                    blockchainType = blockchainType,
                    senderAccountName = senderAccountName,
                    recipientAccountName = recipientAddress,
                    quantity = quantity,
                    memo = memo,
                    contract = contract
                )
                
                feeEstimationResult.getOrNull()?.let { resourceResponse ->
                    when (resourceResponse) {
                        is ResourceProviderResponse.FeeRequired -> {
                            // Store prepared transaction for execution
                            preparedTransaction = resourceResponse.newTransaction
                            FeeCalculation(
                                networkFee = resourceResponse.fee,
                                networkFeeUSD = "0.01", // TODO: Convert to USD
                                cpuUsage = resourceResponse.feeBreakdown.cpu,
                                netUsage = resourceResponse.feeBreakdown.net,
                                ramUsage = resourceResponse.feeBreakdown.ram
                            )
                        }
                        is ResourceProviderResponse.ResourcePaidForFree -> {
                            // Store prepared transaction for execution
                            preparedTransaction = resourceResponse.newTransaction
                            FeeCalculation(
                                networkFee = null,
                                networkFeeUSD = "0.00",
                                cpuUsage = "0",
                                netUsage = "0",
                                ramUsage = "0"
                            )
                        }
                        is ResourceProviderResponse.ResourceNotRequired -> {
                            FeeCalculation(
                                networkFee = null,
                                networkFeeUSD = "0.00",
                                cpuUsage = "0",
                                netUsage = "0",
                                ramUsage = "0"
                            )
                        }
                        is ResourceProviderResponse.InvalidRequest -> {
                            null
                        }
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Napier.e("Fee estimation failed", e)
            null
        } ?: flowData?.get("calculatedFees") as? FeeCalculation

        val functionCallParameters = mutableMapOf<String, Any?>(
            SEND_TRANSACTION_PARAM_AMOUNT to amount,
            SEND_TRANSACTION_PARAM_ASSET to tokenSymbol,
            SEND_TRANSACTION_PARAM_ASSET_LOGO to tokenLogoUrl,
            SEND_TRANSACTION_PARAM_RECIPIENT_ADDRESS to recipientAddress,
            SEND_TRANSACTION_PARAM_MEMO to memo,
            SEND_TRANSACTION_PARAM_FEE to calculatedFees?.networkFee,
            SEND_TRANSACTION_SENDER_ADDRESS to senderAccountName,
            SEND_TRANSACTION_RECIPIENT_CONTACT_NAME to selectedContactName,
            SEND_TRANSACTION_BLOCKCHAIN_UID to blockchainTypeUid
        )
        
        preparedTransaction?.let { transaction ->
            functionCallParameters[SEND_TRANSACTION_PARAM_TRANSACTION] = Json.encodeToString(transaction)
        }

        _uiState.update { it.copy(isLoading = false) }

        val functionCall = FunctionCallRequest(
            name = SEND_TRANSACTION_FUNCTION_NAME,
            parameters = functionCallParameters,
            callId = Uuid.random().toString(),
            securityLevel = SecurityLevel.RequireConfirmation
        )

        val confirmationMessage = FunctionCallConfirmationRequiredMessage(
            id = Uuid.random().toString(),
            senderId = "ai",
            isFromUser = false,
            parentMessageId = messageId,
            functionCall = functionCall,
            confirmationPrompt = "Review and confirm transaction:\n\nSend: $amount $tokenSymbol\nTo: $recipientAddress\nMemo: ${if (memo.isNotEmpty()) memo else "(none)"}\nNetwork fee: ${calculatedFees?.networkFee ?: "0.0001"} EOS",
            functionDescription = "Send $amount $tokenSymbol to $recipientAddress"
        )

        _uiState.value.conversationSession?.id?.let { sessionId ->
            chatHistoryRepository.saveMessage(sessionId, confirmationMessage)
        }
    }

    private suspend fun confirmDelete(messageId: String?) {
        val selectedContactId =
            _uiState.value.activeFlow?.collectedData?.get("selectedContactId") as? String
        val selectedContactName =
            _uiState.value.activeFlow?.collectedData?.get("selectedContactName") as? String
        val targetContact =
            _uiState.value.activeFlow?.collectedData?.get("targetContact") as? String

        if (selectedContactId != null && selectedContactName != null) {
            screenModelScope.launch {
                try {
                    val walletAddresses =
                        getWalletAddressesWithBlockchainByContactIdUseCase(selectedContactId)

                    val parameters = mutableMapOf(
                        "contact_id" to selectedContactId,
                        "contact_name" to selectedContactName
                    )

                    // Add addresses to parameters for display in confirmation
                    if (walletAddresses.isNotEmpty()) {
                        // For simplicity, we'll just add the first address info
                        // The renderer can handle displaying just the contact card
                        val firstAddress = walletAddresses.first()
                        parameters["blockchain_address_or_account_name"] =
                            firstAddress.walletAddress.address
                        parameters["blockchain_network"] = firstAddress.blockchainType.name
                    }

                    val functionCall = FunctionCallRequest(
                        name = "delete_contact",
                        parameters = parameters,
                        callId = Uuid.random().toString(),
                        securityLevel = SecurityLevel.RequireConfirmation
                    )

                    val confirmationMessage = FunctionCallConfirmationRequiredMessage(
                        id = Uuid.random().toString(),
                        senderId = "ai",
                        isFromUser = false,
                        parentMessageId = messageId,
                        functionCall = functionCall,
                        confirmationPrompt = "⚠️ Are you sure you want to delete contact '$selectedContactName'? This action cannot be undone.",
                        functionDescription = "Delete contact $selectedContactName permanently"
                    )

                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, confirmationMessage)
                    }

                    _uiState.update { it.copy(isLoading = false) }
                } catch (e: Exception) {
                    Napier.e("Error fetching contact addresses for deletion", e)

                    // Still proceed with deletion without addresses info
                    val functionCall = FunctionCallRequest(
                        name = "delete_contact",
                        parameters = mapOf(
                            "contact_id" to selectedContactId,
                            "contact_name" to selectedContactName
                        ),
                        callId = Uuid.random().toString(),
                        securityLevel = SecurityLevel.RequireConfirmation
                    )

                    val confirmationMessage = FunctionCallConfirmationRequiredMessage(
                        id = Uuid.random().toString(),
                        senderId = "ai",
                        isFromUser = false,
                        parentMessageId = messageId,
                        functionCall = functionCall,
                        confirmationPrompt = "⚠️ Are you sure you want to delete contact '$selectedContactName'? This action cannot be undone.",
                        functionDescription = "Delete contact $selectedContactName permanently"
                    )

                    _uiState.value.conversationSession?.id?.let { sessionId ->
                        chatHistoryRepository.saveMessage(sessionId, confirmationMessage)
                    }
                }
            }
        } else {
            val errorMessage = TextMessage(
                isFromUser = false,
                senderId = "ai",
                text = "Error: No contact selected for deletion. Flow terminated.",
                timestamp = Clock.System.now()
            )

            _uiState.value.conversationSession?.id?.let { sessionId ->
                chatHistoryRepository.saveMessage(sessionId, errorMessage)
            }

            // Clear the flow
            _uiState.update { currentState ->
                currentState.copy(activeFlow = null)
            }
        }
    }

    private fun shouldUseEnhancedParser(data: JsonObject): Boolean {
        val flowType = data["flowType"]?.jsonPrimitive?.content
        val flowVersion = data["version"]?.jsonPrimitive?.content
        val hasAdvancedSteps = data["steps"]?.jsonArray?.any { step ->
            step.jsonObject["type"]?.jsonPrimitive?.content?.let { stepType ->
                stepType.startsWith("ADVANCED_") || stepType.contains("CONDITIONAL") || stepType.contains(
                    "DYNAMIC"
                )
            } ?: false
        } ?: false

        return when {
            flowVersion != null && flowVersion.toDoubleOrNull()?.let { it >= 2.0 } == true -> true
            hasAdvancedSteps -> true
            flowType in listOf("CONDITIONAL_FLOW", "DYNAMIC_FLOW", "MULTI_STEP_VALIDATION") -> true
            else -> false
        }
    }

    private suspend fun processExecuteLocalFlowWithParser(data: JsonObject, messageId: String?) {
        val flowId = data["flowId"]?.jsonPrimitive?.content ?: "unknown_flow"
        val flowType = data["flowType"]?.jsonPrimitive?.content
        val steps = data["steps"]?.jsonArray
        val extractedData = data["extractedData"]?.jsonObject
        val parameters = data["parameters"]?.jsonObject

        val flowState = FlowState(
            flowId = flowId,
            currentStepIndex = 0,
            flowDefinition = data,
            collectedData = mutableMapOf()
        )

        extractedData?.let { extractedObj ->
            extractedObj.keys.forEach { key ->
                val value = extractedObj[key]?.jsonPrimitive?.content
                if (value != null) {
                    flowState.collectedData[key] = value
                }
            }
        }

        parameters?.let { params ->
            params.keys.forEach { key ->
                val value = params[key]?.jsonPrimitive?.content
                if (value != null) {
                    flowState.collectedData[key] = value
                }
            }
        }

        _uiState.update { currentState ->
            currentState.copy(activeFlow = flowState)
        }

        saveFlowState()

        steps?.firstOrNull()?.let { firstStep ->
            val step = firstStep.jsonObject
            val stepId = step["stepId"]?.jsonPrimitive?.content
            val stepType = step["type"]?.jsonPrimitive?.content

            if (stepId != null && stepType != null) {
                executeFlowStep(
                    flowId = flowId,
                    stepId = stepId,
                    stepType = stepType,
                    stepConfig = step["config"]?.jsonObject,
                    showWhen = step["showWhen"]?.jsonPrimitive?.content,
                    messageId = messageId
                )
            }
        }
    }

    private suspend fun saveUserMessage(text: String) {
        _uiState.value.conversationSession?.id?.let { sessionId ->
            val confirmationMessage = TextMessage(
                isFromUser = true,
                senderId = "user",
                text = text,
                timestamp = Clock.System.now()
            )
            chatHistoryRepository.saveMessage(sessionId, confirmationMessage)
        }
    }

    private fun showContactSelector(
        contacts: List<ContactModel>,
        title: String,
        subtitle: String,
        allowManualAddressInput: Boolean = false
    ) {
        val amountMessage = TextMessage(
            isFromUser = false,
            senderId = "ai",
            text = "Please select a contact to continue",
            timestamp = Clock.System.now()
        )

        screenModelScope.launch {
            _uiState.value.conversationSession?.id?.let { sessionId ->
                chatHistoryRepository.saveMessage(sessionId, amountMessage)
            }

            _uiState.update { currentState ->
                currentState.copy(
                    contactSelectorState = ContactSelectorState(
                        isVisible = true,
                        title = title,
                        subtitle = subtitle,
                        contacts = contacts,
                        selectedContactId = null,
                        onSelectAction = "STORE_AND_PROCEED",
                        onCancelAction = "ABORT_FLOW",
                        nextStep = null,
                        cancelReason = "User cancelled contact selection",
                        maxVisible = 10,
                        enableSearch = true,
                        showLastUsed = true,
                        showAddressCount = true,
                        allowManualAddressInput = allowManualAddressInput
                    )
                )
            }
        }
    }
    
    private suspend fun showAssetPicker(stepConfig: JsonObject?, branches: JsonObject?, flowId: String, stepId: String) {
        try {
            val flowData = _uiState.value.activeFlow?.collectedData ?: return
            val blockchainTypeUid = flowData["blockchainType"] as? String ?: return
            val blockchainType = BlockchainType.fromUid(blockchainTypeUid)

            val tokensBalance = getAntelopeAccountTokenBalanceUseCase(
                accountName = (_uiState.value.activeFlow?.collectedData?.get("selectedAccount") as? AntelopeAccount)?.accountName.orEmpty(),
                blockchainType = blockchainType,
                forceRefresh = false
            )
            val account = getAccountsUseCase(
                blockchainType = blockchainType,
                includeTempAccounts = false
            ).firstOrNull()

            val tokens = tokensBalance.getOrNull()
            if (tokens.isNullOrEmpty()) {
                val errorMessage = TextMessage(
                    isFromUser = false,
                    senderId = "ai",
                    text = "No tokens available in your wallet. Please add some tokens first.",
                    timestamp = Clock.System.now()
                )
                
                _uiState.value.conversationSession?.id?.let { sessionId ->
                    chatHistoryRepository.saveMessage(sessionId, errorMessage)
                }
                
                // Clear the flow
                _uiState.update { currentState ->
                    currentState.copy(activeFlow = null)
                }
                return
            }
            
            val assetSelectionParams = mutableMapOf<String, Any?>()
            val mappedTokens = tokens.map { token ->
                TokenInfo(
                    symbol = token.symbol,
                    contractAddress = token.contract,
                    decimals = token.decimals,
                    balance = token.amount,
                    logo = token.metadata.logo
                )
            } + listOf(
                TokenInfo(
                    symbol = account?.safeCoreBalance?.symbol.orEmpty(),
                    contractAddress = "",
                    decimals = account?.safeCoreBalance?.precision ?: 0,
                    balance = account?.safeCoreBalance?.amount ?: 0.0,
                    logo = null
                )
            )
            
            try {
                val tokensJson = Json.encodeToString(mappedTokens)
                assetSelectionParams["tokens"] = tokensJson
            } catch (e: Exception) {
                Napier.e("Failed to serialize tokens to JSON", e)
                assetSelectionParams["tokens"] = mappedTokens
            }
            
            assetSelectionParams["title"] = "Select Token"
            assetSelectionParams["subtitle"] = "Choose the token you want to send"
            assetSelectionParams["stepConfig"] = stepConfig?.toString()
            assetSelectionParams["branches"] = branches?.toString()
            assetSelectionParams["flowId"] = flowId
            assetSelectionParams["stepId"] = stepId
            
            val actions = branches?.get("actions")?.jsonObject
            val onSelectAction = actions?.get("onSelect")?.jsonObject
            val onCancelAction = actions?.get("onCancel")?.jsonObject
            
            assetSelectionParams["nextStep"] = onSelectAction?.get("nextStep")?.jsonPrimitive?.content
            assetSelectionParams["onSelectAction"] = onSelectAction?.get("action")?.jsonPrimitive?.content
            assetSelectionParams["onCancelAction"] = onCancelAction?.get("action")?.jsonPrimitive?.content
            
            val functionCall = FunctionCallRequest(
                name = "select_asset_for_transaction",
                parameters = assetSelectionParams,
                callId = Uuid.random().toString(),
                securityLevel = SecurityLevel.RequireConfirmation
            )
            
            val confirmationMessage = FunctionCallConfirmationRequiredMessage(
                id = Uuid.random().toString(),
                senderId = "system",
                isFromUser = false,
                functionCall = functionCall,
                confirmationPrompt = "Select the token you want to send",
                functionDescription = "Choose an asset from your available tokens",
                timestamp = Clock.System.now()
            )
            
            _uiState.value.conversationSession?.id?.let { sessionId ->
                chatHistoryRepository.saveMessage(sessionId, confirmationMessage)
            }
            
        } catch (e: Exception) {
            Napier.e("Error showing asset picker", e)
            
            // Show error and clear flow
            val errorMessage = TextMessage(
                isFromUser = false,
                senderId = "ai",
                text = "Error showing asset picker: ${e.message}",
                timestamp = Clock.System.now()
            )
            
            _uiState.value.conversationSession?.id?.let { sessionId ->
                chatHistoryRepository.saveMessage(sessionId, errorMessage)
            }
            
            _uiState.update { currentState ->
                currentState.copy(activeFlow = null)
            }
        }
    }

    companion object {
        const val CHAT_INLINE_ACTION_ADD_MEMO = "confirm_add_memo"
    }
}