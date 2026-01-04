package com.mangala.wallet.features.conversationui.presentation.components.message

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.core.ai.domain.model.message.MessageSendingStatus
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun UserMessage(
    text: String,
    sendingStatus: MessageSendingStatus? = null,
    onRetryClick: (() -> Unit)? = null,
    onLongPress: (Offset) -> Unit = { }
) {
    var globalPosition by remember { mutableStateOf(Offset.Zero) }
    
    MaxWidthBox(Modifier.padding(horizontal = Dimensions.Padding.default)) {
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator on the left of the message
            when (sendingStatus) {
                MessageSendingStatus.PENDING, MessageSendingStatus.SENDING -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 8.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                }
                MessageSendingStatus.FAILED -> {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Failed to send",
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp)
                            .clickable { onRetryClick?.invoke() },
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                MessageSendingStatus.SENT, null -> {
                    // No indicator for sent messages
                }
            }
            
            Box(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color = when (sendingStatus) {
                            MessageSendingStatus.FAILED -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            else -> MaterialTheme.mangalaColors.bgInnerCard
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = when (sendingStatus) {
                            MessageSendingStatus.FAILED -> MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                            else -> MaterialTheme.mangalaColors.border
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .alpha(
                        when (sendingStatus) {
                            MessageSendingStatus.PENDING, MessageSendingStatus.SENDING -> 0.7f
                            else -> 1f
                        }
                    )
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    Text(
                        text = text,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        style = MangalaTypography.Size14Regular()
                    )
                    
                    // Show retry button for failed messages
                    if (sendingStatus == MessageSendingStatus.FAILED) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onRetryClick?.invoke() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tap to retry",
                                color = MaterialTheme.colorScheme.error,
                                style = MangalaTypography.Size12Regular()
                            )
                        }
                    }
                }
            }
        }
    }
}