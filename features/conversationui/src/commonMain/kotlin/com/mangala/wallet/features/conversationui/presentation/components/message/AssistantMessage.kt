package com.mangala.wallet.features.conversationui.presentation.components.message

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import com.mangala.wallet.common.mokoresources.Dimensions
import androidx.compose.ui.Modifier
import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererRegistry
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import com.mangala.wallet.core.ai.domain.model.message.UiTag
import com.mangala.wallet.core.ai.domain.model.renderer.MessageRendererRegistry
import com.mangala.wallet.core.ai.domain.model.action.ActionHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.action.ActionResult
import com.mangala.wallet.core.ai.domain.model.action.QuickAction
import com.mangala.wallet.core.ai.domain.model.dialog.DialogProviderRegistry
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.features.conversationui.presentation.components.ConfirmationContent
import com.mangala.wallet.features.conversationui.presentation.components.QuickActionBar
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography

@Composable
fun AssistantMessage(
    message: Message,
    confirmationRegistry: ConfirmationRendererRegistry,
    messageRendererRegistry: MessageRendererRegistry,
    actionHandlerRegistry: ActionHandlerRegistry,
    dialogProviderRegistry: DialogProviderRegistry,
    onConfirm: (messageId: String, functionCall: FunctionCallRequest) -> Unit,
    onEdit: (messageId: String, functionCall: FunctionCallRequest) -> Unit,
    onDeny: (messageId: String, functionCall: FunctionCallRequest) -> Unit,
    isProcessing: Boolean,
    onUiTagAction: (UiTag, Any) -> Unit = { _, _ -> },
    onActionResult: (ActionResult) -> Unit = { _ -> },
    confirmationStatus: ExecutionStatus = ExecutionStatus.PENDING,
    quickActions: List<QuickAction> = emptyList(),
    isQuickActionsExpanded: Boolean = false,
    onQuickActionClick: (QuickAction) -> Unit = { },
    onDismissQuickActions: () -> Unit = { },
    onLongPress: (Offset) -> Unit = { }
) {
    var globalPosition by remember { mutableStateOf(Offset.Zero) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.default),
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                globalPosition = coordinates.positionInRoot()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset -> 
                        onLongPress(globalPosition + offset)
                    }
                )
            }
    ) {
        when (message) {
            is TextMessage -> {
                Column(verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.default)) {
                    Markdown(
                        content = message.text,
                        colors = markdownColor(text = MaterialTheme.mangalaColors.textPrimary),
                        typography = markdownTypography(text = MangalaTypography.Size14Regular()),
                        modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                    )
                }
            }

            is FunctionCallConfirmationRequiredMessage -> {
                ConfirmationContent(
                    message,
                    registry = confirmationRegistry,
                    onConfirm = {
                        onConfirm(message.id, message.functionCall)
                    },
                    onEdit = {
                        onEdit(message.id, message.functionCall)
                    },
                    onDeny = {
                        onDeny(message.id, message.functionCall)
                    },
                    isProcessing = isProcessing,
                    confirmationStatus = confirmationStatus
                )
            }
            
            else -> {
                // Try to find a renderer for the message type
                val renderer = messageRendererRegistry.getRenderer(message)
                if (renderer != null) {
                    renderer.RenderMessage(
                        message = message,
                        onAction = { action, context ->
                            val result = actionHandlerRegistry.handleAction(action, context)
                            result?.let { onActionResult(it) }
                        },
                        modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                    )
                } else {
                    // Fallback for unsupported message types
                    Text(
                        text = "Unsupported message type: $message",
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                    )
                }
            }
        }
        
        if (confirmationStatus == ExecutionStatus.EXECUTED &&
            quickActions.isNotEmpty() && 
            isQuickActionsExpanded) {
            QuickActionBar(
                actions = quickActions,
                isVisible = true,
                onActionClick = onQuickActionClick,
                onDismiss = onDismissQuickActions,
                modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
            )
        }
    }
}