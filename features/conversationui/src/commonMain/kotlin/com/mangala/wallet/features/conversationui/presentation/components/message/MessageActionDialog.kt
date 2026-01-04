package com.mangala.wallet.features.conversationui.presentation.components.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import com.mangala.wallet.core.ai.domain.model.message.MultiModalMessage
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun MessageActionDialog(
    message: Message,
    tapPosition: Offset,
    onDismiss: () -> Unit,
    onCopyMessage: (String) -> Unit
) {
    val messageText = extractMessageText(message)

    Popup(
        offset = IntOffset(tapPosition.x.toInt(), tapPosition.y.toInt()),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (messageText.isNotEmpty()) {
                MessageActionItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.mangalaColors.textPrimary
                        )
                    },
                    title = "Copy",
                    onClick = { 
                        onCopyMessage(messageText)
                        onDismiss()
                    }
                )
                
//                MessageActionItem(
//                    icon = {
//                        Icon(
//                            imageVector = Icons.Default.SelectAll,
//                            contentDescription = "Select text",
//                            tint = MaterialTheme.mangalaColors.textPrimary
//                        )
//                    },
//                    title = "Select Text",
//                    onClick = { onSelectText(messageText) }
//                )
//
//                if (isUserMessage) {
//                    MessageActionItem(
//                        icon = {
//                            Icon(
//                                imageVector = Icons.Default.Edit,
//                                contentDescription = "Edit message",
//                                tint = MaterialTheme.mangalaColors.textPrimary
//                            )
//                        },
//                        title = "Edit Message",
//                        onClick = { onEditMessage(messageText) }
//                    )
//                }
            }
        }
    }
}

@Composable
private fun MessageActionItem(
    icon: @Composable () -> Unit,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.mangalaColors.textPrimary
        )
    }
}

private fun extractMessageText(message: Message): String {
    return when (message) {
        is TextMessage -> message.text
        is MultiModalMessage -> {
            message.messages.filterIsInstance<TextMessage>()
                .joinToString("\n") { it.text }
        }
        else -> ""
    }
}