package com.mangala.wallet.features.conversationui.presentation.components.message

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import com.mangala.wallet.core.ai.domain.model.message.UiTag
import com.mangala.wallet.core.ai.domain.model.message.ImageMessage
import com.mangala.wallet.core.ai.domain.model.message.MultiModalMessage
import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererRegistry
import com.mangala.wallet.core.ai.domain.model.renderer.MessageRendererRegistry
import com.mangala.wallet.core.ai.domain.model.action.ActionHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.action.ActionResult
import com.mangala.wallet.core.ai.domain.model.action.QuickAction
import com.mangala.wallet.core.ai.domain.model.dialog.DialogProviderRegistry
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import kotlinx.datetime.Instant

/**
 * Chat bubble component for messages in a conversation
 *
 * @param message The message text to display
 * @param isUserMessage Whether the message is from the user (true) or AI (false)
 * @param timestamp The time the message was sent
 * @param confirmationRegistry Optional registry for confirmation renderers
 * @param onConfirmFunction Optional callback when a function is confirmed
 * @param onDenyFunction Optional callback when a function is denied
 * @param modifier Optional modifier for the component
 */
@Composable
fun ChatBubble(
    message: Message,
    isUserMessage: Boolean,
    timestamp: Instant,
    confirmationRegistry: ConfirmationRendererRegistry,
    messageRendererRegistry: MessageRendererRegistry,
    actionHandlerRegistry: ActionHandlerRegistry,
    dialogProviderRegistry: DialogProviderRegistry,
    onConfirmFunction: (messageId: String, FunctionCallRequest) -> Unit,
    onEditFunction: (messageId: String, FunctionCallRequest) -> Unit,
    onDenyFunction: (messageId: String, FunctionCallRequest) -> Unit,
    processingMessageIds: Set<String> = emptySet(),
    onUiTagAction: (UiTag, Any) -> Unit = { _, _ -> },
    onActionResult: (ActionResult) -> Unit = { _ -> },
    modifier: Modifier = Modifier,
    quickActionsForMessages: Map<String, List<QuickAction>> = emptyMap(),
    expandedQuickActionMessageId: String? = null,
    onQuickActionClick: (QuickAction) -> Unit = { },
    onDismissQuickActions: (String) -> Unit = { },
    onRetryMessage: ((String) -> Unit)? = null,
    onLongPress: (Message, Offset) -> Unit = { _, _ -> }
) {
    if (isUserMessage) {
        val sendingStatus = when (message) {
            is TextMessage -> message.sendingStatus
            is ImageMessage -> message.sendingStatus
            is MultiModalMessage -> message.sendingStatus
            else -> null
        }
        
        val text = when (message) {
            is TextMessage -> message.text
            is MultiModalMessage -> message.messages.filterIsInstance<TextMessage>().firstOrNull()?.text ?: ""
            else -> ""
        }
        
        UserMessage(
            text = text,
            sendingStatus = sendingStatus,
            onRetryClick = if (onRetryMessage != null) {
                { onRetryMessage(message.id) }
            } else null,
            onLongPress = { offset -> onLongPress(message, offset) }
        )
    } else {
        AssistantMessage(
            message = message,
            confirmationRegistry = confirmationRegistry,
            messageRendererRegistry = messageRendererRegistry,
            actionHandlerRegistry = actionHandlerRegistry,
            dialogProviderRegistry = dialogProviderRegistry,
            onConfirm = onConfirmFunction,
            onEdit = onEditFunction,
            onDeny = onDenyFunction,
            isProcessing = message.id in processingMessageIds,
            onUiTagAction = onUiTagAction,
            onActionResult = onActionResult,
            confirmationStatus = (message as? FunctionCallConfirmationRequiredMessage)?.executionStatus ?: ExecutionStatus.PENDING,
            quickActions = quickActionsForMessages[message.id] ?: emptyList(),
            isQuickActionsExpanded = expandedQuickActionMessageId == message.id,
            onQuickActionClick = onQuickActionClick,
            onDismissQuickActions = { onDismissQuickActions(message.id) },
            onLongPress = { offset -> onLongPress(message, offset) }
        )
    }
}
