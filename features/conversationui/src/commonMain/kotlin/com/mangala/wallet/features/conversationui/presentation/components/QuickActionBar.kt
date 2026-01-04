package com.mangala.wallet.features.conversationui.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.core.ai.domain.model.action.QuickAction
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun QuickActionBar(
    actions: List<QuickAction>,
    isVisible: Boolean = true,
    onActionClick: (QuickAction) -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible && actions.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(actions) { action ->
                QuickActionChip(
                    action = action,
                    onClick = { onActionClick(action) }
                )
            }
            
            if (onDismiss != null) {
                item {
                    IconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.mangalaColors.border.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss quick actions",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.mangalaColors.iconPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = action.label,
                fontSize = 12.sp,
                color = MaterialTheme.mangalaColors.textPrimary
            )
        },
        leadingIcon = action.icon?.let { iconName ->
            {
                Icon(
                    imageVector = getIconForName(iconName),
                    contentDescription = action.label,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.mangalaColors.iconPrimary
                )
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.mangalaColors.border.copy(alpha = 0.3f),
            labelColor = MaterialTheme.mangalaColors.textPrimary,
            leadingIconContentColor = MaterialTheme.mangalaColors.iconPrimary
        ),
        border = BorderStroke(1.dp, MaterialTheme.mangalaColors.border),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(32.dp)
    )
}

private fun getIconForName(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "person", "contact", "user" -> Icons.Default.Person
        "edit", "edit_contact" -> Icons.Default.Edit
        "send", "send_crypto" -> Icons.Default.Send
        else -> Icons.Default.Person
    }
}