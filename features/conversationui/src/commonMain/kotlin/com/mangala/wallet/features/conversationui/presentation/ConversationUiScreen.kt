package com.mangala.wallet.features.conversationui.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeftNavigation
import com.mangala.wallet.core.ai.domain.model.action.ActionHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.action.ActionResult
import com.mangala.wallet.core.ai.domain.model.dialog.DialogProviderRegistry
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererRegistry
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.navigation.NavigationEvent
import com.mangala.wallet.core.ai.domain.model.navigation.NavigationHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.navigation.NavigationResult
import com.mangala.wallet.core.ai.domain.model.renderer.MessageRendererRegistry
import com.mangala.wallet.core.security.models.SecurityLevel
import com.mangala.wallet.features.conversationui.presentation.components.AccountSelector
import com.mangala.wallet.features.conversationui.presentation.components.ChatInputArea
import com.mangala.wallet.features.conversationui.presentation.components.ContactSelector
import com.mangala.wallet.features.conversationui.presentation.components.background.ChatBackground
import com.mangala.wallet.features.conversationui.presentation.components.input.ExpandableNetworkSelector
import com.mangala.wallet.features.conversationui.presentation.components.message.ChatBubble
import com.mangala.wallet.features.conversationui.presentation.components.message.MessageActionDialog
import com.mangala.wallet.features.conversationui.presentation.components.message.TypingIndicator
import com.mangala.wallet.features.conversationui.presentation.components.transaction.TransactionProgressComponent
import com.mangala.wallet.features.conversationui.presentation.components.transaction.TransactionReviewComponent
import com.mangala.wallet.features.conversationui.presentation.model.InputMode
import com.mangala.wallet.features.conversationui.presentation.sessionlist.ConversationSessionListScreen
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.clipEntryText
import com.mangala.wallet.ui.utils.clipEntryOf
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mohamedrejeb.calf.core.LocalPlatformContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class ConversationUiScreen(private val sessionId: String? = null) : BaseScreen<ConversationUiScreenModel>(), KoinComponent {

    @delegate:Transient
    private val scanQRCode: ScanQRCode by inject()
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ConversationUiScreenModel {
        return getScreenModel { parametersOf(sessionId) }
    }

    override val screenName: String = MangalaAnalytics.Screens.CONVERSATION_UI
    override val screenClassName: String = ConversationUiScreen::class.simpleName.orEmpty()

    @delegate:Transient
    private val confirmationRendererRegistry: ConfirmationRendererRegistry by inject()

    @delegate:Transient
    private val messageRendererRegistry: MessageRendererRegistry by inject()

    @delegate:Transient
    private val actionHandlerRegistry: ActionHandlerRegistry by inject()

    @delegate:Transient
    private val dialogProviderRegistry: DialogProviderRegistry by inject()

    @delegate:Transient
    private val navigationHandlerRegistry: NavigationHandlerRegistry by inject()

    @Composable
    override fun ScreenContent(screenModel: ConversationUiScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        LaunchedEffect(Unit) {
            screenModel.navigationEvents.collect { event ->
                when (event) {
                    is ConversationUiScreenModel.NavigationEvent.GenericNavigationEvent -> {
                        if (event.event is NavigationEvent) {
                            event.event.navigate(navigator)
                        } else {
                            handleGenericNavigationEvent(event.event, screenModel, navigator)
                        }
                    }

                    ConversationUiScreenModel.NavigationEvent.NavigateToSignIn -> {
                        navigator.popUntil { it is ConversationSessionListScreen }
                        navigator.replace(
                            ScreenRegistry.get(SharedScreen.SignInScreen(showTokenExpiredMessage = true))
                        )
                    }
                }
            }
        }

        ConversationUiScreen(
            viewModel = screenModel,
            onNavigateBack = if (navigator.canPop) {
                {
                    navigator.pop()
                }
            } else null,
            sessionId = sessionId
        )
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ConversationUiScreen(
        viewModel: ConversationUiScreenModel,
        onNavigateBack: (() -> Unit)? = null,
        modifier: Modifier = Modifier,
        sessionId: String? = null
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState by viewModel.uiState.collectAsState()
        val messages = uiState.messages

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val localClipboard = LocalClipboard.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val hapticFeedback = LocalHapticFeedback.current
        val focusRequester = remember { FocusRequester() }

        // Instantly scroll to the bottom for existing conversations
        LaunchedEffect(key1 = sessionId) {
            if (sessionId != null && messages.isNotEmpty()) {
                coroutineScope.launch {
                    listState.scrollToItem(messages.size - 1)
                }
            }
        }


        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                coroutineScope.launch {
                    listState.scrollToItem(messages.size - 1)
                }
            }
        }

        val isNewConversation = messages.isEmpty() && !uiState.isLoading
        LaunchedEffect(isNewConversation) {
            if (isNewConversation) {
                delay(100) // Small delay to ensure UI is ready
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        }

        val scope = rememberCoroutineScope()
        val context = LocalPlatformContext.current

        val isAtBottom by remember {
            derivedStateOf {
                val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                val totalItemsCount = listState.layoutInfo.totalItemsCount
                lastVisibleIndex != null && lastVisibleIndex >= totalItemsCount - 1 && totalItemsCount > 0
            }
        }

        var isDebugMenuExpanded by remember { mutableStateOf(false) }

        var currentDialog by remember { mutableStateOf<Pair<String, Map<String, Any>>?>(null) }
        var selectedMessageForAction by remember { mutableStateOf<Pair<Message, androidx.compose.ui.geometry.Offset>?>(null) }

        val handleDialogNavigation = { navigationResult: ActionResult.Navigate,
                                       viewModel: ConversationUiScreenModel,
                                       navigator: Navigator ->
            val result = navigationHandlerRegistry.handleNavigation(
                navigationResult.destination,
                navigationResult.context
            )

            when (result) {
                is NavigationResult.EmitNavigationEvent -> {
                    viewModel.emitNavigationEvent(result.event)
                }

                is NavigationResult.NavigateToScreen -> {
                    navigator.push(result.screenProvider())
                }

                NavigationResult.Handled -> {
                    // Navigation was handled by the registry
                }

                NavigationResult.NotHandled, null -> {
                    println("Navigation not handled for: ${navigationResult.destination}")
                }
            }
        }

        /*
        val pickerLauncher = rememberFilePickerLauncher(
            type = FilePickerFileType.Image,
            selectionMode = FilePickerSelectionMode.Single,
            onResult = { files ->
                viewModel.setImageLoading(true)
                scope.launch {
                    files.firstOrNull()?.let { file ->
                        try {
                            val imageData = file.readByteArray(context)
                            val mimeType = when {
                                file.getName(context)
                                    ?.endsWith(".jpg", ignoreCase = true) == true ||
                                        file.getName(context)?.endsWith(
                                            ".jpeg",
                                            ignoreCase = true
                                        ) == true -> "image/jpeg"

                                file.getName(context)
                                    ?.endsWith(".png", ignoreCase = true) == true -> "image/png"

                                file.getName(context)
                                    ?.endsWith(".gif", ignoreCase = true) == true -> "image/gif"

                                file.getName(context)
                                    ?.endsWith(".webp", ignoreCase = true) == true -> "image/webp"

                                file.getName(context)
                                    ?.endsWith(".bmp", ignoreCase = true) == true -> "image/bmp"

                                else -> "image/jpeg" // Default to JPEG if unknown
                            }
                            viewModel.setSelectedImage(imageData, mimeType)
                        } catch (e: Exception) {
                            println("Error reading image: ${e.message}")
                            viewModel.setImageLoading(false)
                        }
                    } ?: run {
                        viewModel.setImageLoading(false)
                    }
                }
            }
        )
        */

        OnboardingGradientBackground(
            afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    MaxWidthColumn(
                        Modifier.statusBarsPadding()
                    ) {
                        MaxWidthBox(Modifier.padding(vertical = 12.dp)) {
                            onNavigateBack?.let {
                                IconButton(
                                    onClick = onNavigateBack,
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(8.dp)
                                        .size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = MangalaWalletPack.ArrowLeftNavigation,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.mangalaColors.textPrimary
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Chat",
                                    color = MaterialTheme.mangalaColors.textPrimary
                                )
                                if (uiState.isWebSocketConnected) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(
                                                    color = Color(0xFF4CAF50),
                                                    shape = androidx.compose.foundation.shape.CircleShape
                                                )
                                        )
                                        Text(
                                            "Connected",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF4CAF50)
                                        )
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (viewModel.getIsDebugBuild()) {
                                    Box {
                                        IconButton(
                                            onClick = {
                                                isDebugMenuExpanded = !isDebugMenuExpanded
                                            },
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.BugReport,
                                                contentDescription = "Debug",
                                                tint = MaterialTheme.mangalaColors.textPrimary
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = isDebugMenuExpanded,
                                            onDismissRequest = { isDebugMenuExpanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Clear Messages") },
                                                onClick = {
                                                    viewModel.clearMessages()
                                                    isDebugMenuExpanded = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Show Debug Info") },
                                                onClick = {
                                                    viewModel.toggleDebugInfo()
                                                    isDebugMenuExpanded = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Export Chat") },
                                                onClick = {
                                                    viewModel.exportChatLog()
                                                    isDebugMenuExpanded = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Reset State") },
                                                onClick = {
                                                    viewModel.resetConversationState()
                                                    isDebugMenuExpanded = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Toggle Loading") },
                                                onClick = {
                                                    viewModel.toggleLoadingState()
                                                    isDebugMenuExpanded = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Setup PIN") },
                                                onClick = {
                                                    navigator.push(
                                                        ScreenRegistry.get(
                                                            SharedScreen.SetupPinScreen(pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET.name)
                                                        )
                                                    )
                                                    isDebugMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (messages.isEmpty() && !uiState.isLoading) {
                        ConversationUiEmptyState()
                    } else {
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            LazyColumn(
                                state = listState,
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                items(
                                    messages,
                                    key = { it.id }
                                ) { message ->
                                    Box {
                                        ChatBubble(
                                            message = message,
                                            isUserMessage = message.isFromUser,
                                            timestamp = message.timestamp,
                                            confirmationRegistry = confirmationRendererRegistry,
                                            messageRendererRegistry = messageRendererRegistry,
                                            actionHandlerRegistry = actionHandlerRegistry,
                                            dialogProviderRegistry = dialogProviderRegistry,
                                            onConfirmFunction = { messageId: String, functionCall ->
                                                when (functionCall.securityLevel) {
                                                    SecurityLevel.None, SecurityLevel.RequireConfirmation -> {
                                                        viewModel.confirmFunctionCall(
                                                            messageId,
                                                            functionCall
                                                        )
                                                    }

                                                    SecurityLevel.RequirePin, SecurityLevel.RequireBiometryOrPin -> {
                                                        val pinScreen = ScreenRegistry.get(
                                                            SharedScreen.UnlockPinScreen(
                                                                SharedScreen.UnlockPinScreen.CONFIRM_DAPP,
                                                                unlockPinCallback = {
//                                                                onConfirmComplete()

                                                                    if (it) {
                                                                        viewModel.confirmFunctionCall(
                                                                            messageId,
                                                                            functionCall
                                                                        )
                                                                    }
                                                                },
                                                                antelopeAccountName = null
                                                            )
                                                        )
                                                        navigator.push(pinScreen)
                                                    }

                                                    SecurityLevel.Require2FA -> TODO()
                                                }
                                            },
                                            onEditFunction = { messageId: String, functionCall ->
                                                viewModel.editFunctionCall(messageId, functionCall)
                                            },
                                            onDenyFunction = { messageId: String, functionCall ->
                                                viewModel.denyFunctionCall(messageId, functionCall)
                                            },
                                            processingMessageIds = uiState.processingMessageIds,
                                            onUiTagAction = { uiTag, data ->
                                                viewModel.handleUiTagAction(uiTag, data)
                                            },
                                            onActionResult = { result ->
                                                when (result) {
                                                    is ActionResult.ShowDialog -> {
                                                        currentDialog =
                                                            result.dialogType to result.context
                                                    }

                                                    is ActionResult.Navigate -> {
                                                        handleDialogNavigation(
                                                            result,
                                                            viewModel,
                                                            navigator
                                                        )
                                                    }

                                                    is ActionResult.ShowToast -> {
                                                        // Handle toast - could show snackbar
                                                        println("Toast: ${result.message}")
                                                    }

                                                    is ActionResult.UpdateState -> {
                                                        // Handle state updates if needed
                                                        println("Update state: ${result.updates}")
                                                    }

                                                    ActionResult.Handled, ActionResult.NotHandled -> {
                                                        // No additional action needed
                                                    }

                                                    is ActionResult.ShowQuickActions -> {
                                                        viewModel.showQuickActions(
                                                            result.messageId,
                                                            result.actions,
                                                            result.context
                                                        )
                                                    }
                                                }
                                            },
                                            quickActionsForMessages = uiState.quickActionsForMessages,
                                            expandedQuickActionMessageId = uiState.expandedQuickActionMessageId,
                                            onQuickActionClick = { action ->
                                                viewModel.handleQuickAction(action)
                                            },
                                            onDismissQuickActions = { messageId ->
                                                viewModel.dismissQuickActions(messageId)
                                            },
                                            onRetryMessage = { messageId ->
                                                viewModel.retryMessage(messageId)
                                            },
                                            onLongPress = { message, offset ->
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                selectedMessageForAction = message to offset
                                            }
                                        )
                                    }
                                }

                                // Show streaming message if present
                                uiState.streamingMessage?.let { streamingMsg ->
                                    item {
                                        ChatBubble(
                                            message = streamingMsg,
                                            isUserMessage = false,
                                            timestamp = streamingMsg.timestamp,
                                            confirmationRegistry = confirmationRendererRegistry,
                                            messageRendererRegistry = messageRendererRegistry,
                                            actionHandlerRegistry = actionHandlerRegistry,
                                            dialogProviderRegistry = dialogProviderRegistry,
                                            onConfirmFunction = { _, _ -> },
                                            onEditFunction = { _, _ -> },
                                            onDenyFunction = { _, _ -> },
                                            processingMessageIds = emptySet(),
                                            onUiTagAction = { _, _ -> }
                                        )
                                    }
                                }

                                if (uiState.isLoading) {
                                    item {
                                        TypingIndicator()
                                    }
                                }
                            }

                            // Scroll to bottom button - positioned above input area
                            androidx.compose.animation.AnimatedVisibility(
                                visible = !isAtBottom && messages.isNotEmpty(),
                                enter = fadeIn(animationSpec = tween(durationMillis = 200)),
                                exit = fadeOut(animationSpec = tween(durationMillis = 200)),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp)
                            ) {
                                SmallFloatingActionButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val lastIndex = listState.layoutInfo.totalItemsCount - 1
                                            if (lastIndex >= 0) {
                                                listState.animateScrollToItem(lastIndex)
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(36.dp),
                                    containerColor = MaterialTheme.mangalaColors.bgAlpha,
                                    contentColor = MaterialTheme.mangalaColors.textPrimary
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Scroll to bottom",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Network selector area - shown above input when requested
                    AnimatedVisibility(
                        visible = uiState.inputState.mode is InputMode.SelectNetwork,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeOut(animationSpec = tween(durationMillis = 300))
                    ) {
                        ExpandableNetworkSelector(
                            networks = BlockchainNetworkData.getAllBlockchainNetworkSupported(
                                includeDebugNetworks = true
                            ),
                            onNetworkSelected = { network ->
                                viewModel.handleNetworkSelection(network)
                            },
                            onMinimize = {
                                viewModel.minimizeNetworkSelection()
                            },
                            message = uiState.inputState.networkSelectionMessage
                                ?: "Please select the network for your contact",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }

                    // Contact selector - shown when multiple contacts are found
                    AnimatedVisibility(
                        visible = uiState.contactSelectorState.isVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeOut(animationSpec = tween(durationMillis = 300))
                    ) {
                        ContactSelector(
                            state = uiState.contactSelectorState,
                            onSelectContact = { contactId ->
                                viewModel.selectContact(contactId)
                            },
                            onCancel = {
                                viewModel.cancelContactSelector()
                            },
                            onManualAddressInput = {
                                viewModel.handleManualAddressInput()
                            }
                        )
                    }

                    // Account selector - shown when receive token flow is triggered
                    AnimatedVisibility(
                        visible = uiState.accountSelectorState.isVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeOut(animationSpec = tween(durationMillis = 300))
                    ) {
                        AccountSelector(
                            state = uiState.accountSelectorState,
                            onSelectAccount = { account ->
                                viewModel.onAccountSelected(account)
                            },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Transaction components - shown based on transaction flow state
                    when (val transactionFlow = uiState.transactionFlow) {
                        is TransactionFlowState.Review -> {
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(durationMillis = 300)
                                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                                exit = slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(durationMillis = 300)
                                ) + fadeOut(animationSpec = tween(durationMillis = 300))
                            ) {
                                TransactionReviewComponent(
                                    reviewData = transactionFlow.reviewData,
                                    onConfirm = {
                                        // TODO: Implement transaction confirmation in viewModel
                                        // viewModel.confirmTransaction()
                                    },
                                    onCancel = {
                                        // TODO: Implement transaction cancellation in viewModel
                                        // viewModel.cancelTransaction()
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        is TransactionFlowState.Progress -> {
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(durationMillis = 300)
                                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                                exit = slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(durationMillis = 300)
                                ) + fadeOut(animationSpec = tween(durationMillis = 300))
                            ) {
                                TransactionProgressComponent(
                                    steps = transactionFlow.steps,
                                    currentStep = transactionFlow.currentStep,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        TransactionFlowState.None -> {
                            // No transaction UI to show
                        }
                    }

                    // Input area - always at the bottom
                    ChatInputArea(
                        messageText = uiState.inputState.messageText,
                        onMessageTextChange = { text ->
                            viewModel.updateMessageText(text)
                        },
                        onSendMessage = { message ->
                            keyboardController?.hide()
                            focusManager.clearFocus()

                            when (uiState.inputState.mode) {
                                is InputMode.EnterAddress -> viewModel.sendAddress(message)
                                is InputMode.EnterMemo -> viewModel.sendMemo(message)
                                is InputMode.EnterAmount -> viewModel.sendAmount(message)
                                is InputMode.ContactName -> viewModel.sendContactName(message)
                                else -> viewModel.sendMessage(message)
                            }
                        },
                        onRecordAudio = {

                        },
                        placeholder = when (val mode = uiState.inputState.mode) {
                            is InputMode.Normal -> "Enter anything"
                            is InputMode.EnterAddress -> "Enter contact's address"
                            InputMode.SelectNetwork -> "Please select a network above"
                            InputMode.EnterMemo -> "Enter memo (optional)"
                            is InputMode.EnterAmount -> "Enter amount to send (${mode.tokenSymbol})"
                            is InputMode.ContactName -> mode.placeholder
                        },
                        inputMode = uiState.inputState.mode,
                        addressValidationState = uiState.inputState.addressValidation,
                        amountValidationState = uiState.inputState.amountValidation,
                        onScanQrCode = {
                            scanQRCode.scanQRCode(object : ScanQRCodeListener {
                                override fun onScanQRCodeResult(result: String) {
                                    viewModel.handleQrCodeResult(result)
                                }
                            })
                        },
                        onPasteFromClipboard = {
                            coroutineScope.launch {
                                val clipEntry = localClipboard.getClipEntry()
                                clipEntry?.let {
                                    viewModel.pasteAddressFromClipboard(clipEntryText(clipEntry).orEmpty())
                                }
                            }
                        },
                        focusRequester = focusRequester,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                    )
                }

                currentDialog?.let { (dialogType, context) ->
                    val provider = dialogProviderRegistry.getProvider(dialogType, context)
                    provider?.ProvideDialog(
                        type = dialogType,
                        context = context,
                        onAction = { action, actionContext ->
                            val result = actionHandlerRegistry.handleAction(action, actionContext)
                            result?.let {
                                when (it) {
                                    is ActionResult.ShowDialog -> {
                                        currentDialog = it.dialogType to it.context
                                    }

                                    is ActionResult.Navigate -> {
                                        handleDialogNavigation(it, viewModel, navigator)
                                        currentDialog = null // Close dialog after navigation
                                    }

                                    else -> {
                                    }
                                }
                            }
                        },
                        onDismiss = {
                            currentDialog = null
                        }
                    )
                }
                
                selectedMessageForAction?.let { (message, offset) ->
                    MessageActionDialog(
                        message = message,
                        tapPosition = offset,
                        onDismiss = {
                            selectedMessageForAction = null
                        },
                        onCopyMessage = { text ->
                            coroutineScope.launch {
                                localClipboard.setClipEntry(clipEntryOf(text))
                            }
                            selectedMessageForAction = null
                        }
                    )
                }
            }
        }
    }

    private fun handleDialogNavigation(
        navigationResult: ActionResult.Navigate,
        viewModel: ConversationUiScreenModel,
        navigator: Navigator
    ) {
        val result = navigationHandlerRegistry.handleNavigation(
            navigationResult.destination,
            navigationResult.context
        )

        when (result) {
            is NavigationResult.EmitNavigationEvent -> {
                viewModel.emitNavigationEvent(result.event)
            }

            is NavigationResult.NavigateToScreen -> {
                navigator.push(result.screenProvider())
            }

            NavigationResult.Handled -> {
                // Navigation was handled by the registry
            }

            NavigationResult.NotHandled, null -> {
                println("Navigation not handled for: ${navigationResult.destination}")
            }
        }
    }

    private fun handleGenericNavigationEvent(
        navigationEvent: Any,
        viewModel: ConversationUiScreenModel,
        navigator: Navigator
    ) {
        // For generic navigation events, we need to determine the destination and context
        // based on the event type. Since we don't know the specific event types in ConversationUI,
        // we'll try to extract common patterns or use reflection/toString to identify the destination

        val eventClassName = navigationEvent::class.simpleName ?: "Unknown"
        val destination = when {
            eventClassName.contains("Contact") -> {
                when {
                    eventClassName.contains("Details") -> "contact_details"
                    eventClassName.contains("Edit") -> "edit_contact"
                    eventClassName.contains("SendCrypto") || eventClassName.contains("Send") -> "send_crypto"
                    eventClassName.contains("Delete") || eventClassName.contains("Confirmation") -> "delete_contact_confirmation"
                    else -> null
                }
            }

            else -> null
        }

        destination?.let {
            // Try to extract context from the navigation event
            // This is a generic approach that attempts to extract properties using reflection
            val context = extractContextFromNavigationEvent(navigationEvent)

            val result = navigationHandlerRegistry.handleNavigation(it, context)
            when (result) {
                is NavigationResult.EmitNavigationEvent -> {
                    // Prevent infinite loops by not re-emitting the same event
                    if (result.event != navigationEvent) {
                        viewModel.emitNavigationEvent(result.event)
                    }
                }

                is NavigationResult.NavigateToScreen -> {
                    navigator.push(result.screenProvider())
                }

                NavigationResult.Handled -> {
                    // Navigation was handled successfully
                }

                NavigationResult.NotHandled, null -> {
                    println("Generic navigation event not handled: $eventClassName")
                }
            }
        } ?: run {
            println("Unknown generic navigation event: $eventClassName")
        }
    }

    private fun extractContextFromNavigationEvent(navigationEvent: Any): Map<String, Any> {
        val context = mutableMapOf<String, Any>()

        // Use toString approach to extract properties from navigation events
        // This works across all platforms and is simpler than reflection
        val eventString = navigationEvent.toString()

        // Look for common patterns in the toString() output
        when {
            eventString.contains("contactId=") -> {
                val contactId = eventString.substringAfter("contactId=")
                    .substringBefore(",").substringBefore(")")
                    .trim()
                if (contactId.isNotEmpty()) {
                    context["contactId"] = contactId
                }
            }

            eventString.contains("id=") -> {
                val id = eventString.substringAfter("id=")
                    .substringBefore(",").substringBefore(")")
                    .trim()
                if (id.isNotEmpty()) {
                    context["contactId"] = id // Map generic 'id' to 'contactId'
                }
            }
        }

        return context
    }
}