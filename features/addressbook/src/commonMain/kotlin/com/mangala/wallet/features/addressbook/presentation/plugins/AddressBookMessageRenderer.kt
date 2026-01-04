package com.mangala.wallet.features.addressbook.presentation.plugins

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.renderer.MessageRenderer
import com.mangala.wallet.core.ai.domain.model.action.QuickAction
import com.mangala.wallet.core.ai.domain.model.action.QuickActionType
import com.mangala.wallet.features.addressbook.presentation.components.ContactResultsView
import com.mangala.wallet.features.addressbook.presentation.message.ContactResultsMessage
import com.mangala.wallet.features.addressbook.presentation.message.ContactCreatedMessage

class AddressBookMessageRenderer : MessageRenderer {
    override fun getSupportedMessageTypes(): Set<String> = setOf(
        ContactResultsMessage.CONTENT_TYPE,
        ContactCreatedMessage.CONTENT_TYPE,
        "contact_confirmation", 
        "contact_added",
        "contact_deleted"
    )
    
    override fun canRender(message: Message): Boolean {
        return message.getContentType() in getSupportedMessageTypes()
    }
    
    @Composable
    override fun RenderMessage(
        message: Message,
        onAction: (String, Map<String, Any>) -> Unit,
        modifier: Modifier
    ) {
        when (message.getContentType()) {
            ContactResultsMessage.CONTENT_TYPE -> {
                val contactMessage = message as ContactResultsMessage
                ContactResultsView(
                    message = contactMessage,
                    onContactClick = { contact ->
                        onAction("show_contact_dialog", mapOf("contact" to contact))
                    },
                    modifier = modifier
                )
            }
            ContactCreatedMessage.CONTENT_TYPE -> {
                val contactMessage = message as ContactCreatedMessage
                
                // Display the success message with quick actions directly
                Column(modifier = modifier.padding(16.dp)) {
                    Text(
                        text = contactMessage.successMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Contact: ${contactMessage.contactName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    // Display quick actions directly if enabled
                    if (contactMessage.triggerQuickActions) {
                        val quickActions = listOf(
                            QuickAction(
                                id = "view_contact_${contactMessage.contactId}",
                                label = "View Contact",
                                icon = "person",
                                actionType = QuickActionType.Navigate("contact_details"),
                                metadata = mapOf("contactId" to contactMessage.contactId)
                            ),
                            QuickAction(
                                id = "edit_contact_${contactMessage.contactId}",
                                label = "Edit Contact",
                                icon = "edit",
                                actionType = QuickActionType.Navigate("edit_contact"),
                                metadata = mapOf("contactId" to contactMessage.contactId)
                            ),
                            QuickAction(
                                id = "send_crypto_${contactMessage.contactId}",
                                label = "Send Crypto",
                                icon = "send",
                                actionType = QuickActionType.Navigate("send_crypto"),
                                metadata = mapOf("contactId" to contactMessage.contactId)
                            )
                        )
                        
                        // Simple quick actions bar
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            items(quickActions) { action ->
                                AssistChip(
                                    onClick = {
                                        // Handle quick action click via the existing onAction system
                                        val actionType = action.actionType
                                        when (actionType) {
                                            is QuickActionType.Navigate -> {
                                                onAction("navigate", mapOf(
                                                    "destination" to actionType.destination,
                                                    "contact_id" to contactMessage.contactId
                                                ))
                                            }
                                            else -> {
                                                // Handle other action types
                                            }
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = action.label,
                                            fontSize = 12.sp
                                        )
                                    },
                                    leadingIcon = action.icon?.let { iconName ->
                                        {
                                            Icon(
                                                imageVector = getIconForName(iconName),
                                                contentDescription = action.label
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            // Future: Handle other contact message types...
            else -> {
                // Fallback - this shouldn't happen if canRender is implemented correctly
            }
        }
    }
    
    private fun getIconForName(iconName: String): ImageVector {
        return when (iconName) {
            "person" -> Icons.Default.Person
            "edit" -> Icons.Default.Edit
            "send" -> Icons.AutoMirrored.Filled.Send
            else -> Icons.Default.Person // Default fallback
        }
    }
}