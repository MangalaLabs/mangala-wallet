package com.mangala.wallet.features.addressbook.domain.functioncalling.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRenderer
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.presentation.ConfirmationActions
import com.mangala.wallet.core.ai.presentation.StatusFeedbackText

class EditContactConfirmationRenderer : ConfirmationRenderer {
    override val functionName: String = "edit_contact"

    @Composable
    override fun RenderConfirmation(
        message: FunctionCallConfirmationRequiredMessage,
        onConfirm: () -> Unit,
        onEdit: () -> Unit,
        onDeny: () -> Unit,
        isProcessing: Boolean
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                Text(
                    text = "Confirm Contact Update",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                // Confirmation prompt from AI
                if (message.confirmationPrompt.isNotBlank()) {
                    Text(
                        text = message.confirmationPrompt,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Contact update details
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Update Details",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Divider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Display the parameters that will be updated
                        val parameters = message.functionCall.parameters

                        parameters["contact_id"]?.let { contactId ->
                            DetailRow("Contact ID", contactId.toString())
                        }

                        parameters["name"]?.let { name ->
                            DetailRow("Name", name.toString(), highlight = true)
                        }

                        parameters["blockchain_address_or_account_name"]?.let { address ->
                            DetailRow("Address/Account", address.toString(), highlight = true)
                        }

                        parameters["blockchain_network"]?.let { network ->
                            DetailRow("Network", network.toString(), highlight = true)
                        }

                        parameters["notes"]?.let { notes ->
                            DetailRow("Notes", notes.toString(), highlight = true)
                        }
                    }
                }

                // Warning text
                Text(
                    text = "⚠️ This action will update the contact information. Please review the changes carefully.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )

                // Action buttons
                ConfirmationActions(
                    message = message,
                    onConfirm = onConfirm,
                    onDeny = onDeny,
                    isProcessing = isProcessing,
                    statusTexts = StatusFeedbackText(
                        confirmed = "✓ Contact updated",
                        executed = "✓ Contact updated",
                        failed = "⚠️ Failed to update contact"
                    )
                )
            }
        }
    }

    @Composable
    private fun DetailRow(
        label: String,
        value: String,
        highlight: Boolean = false
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (highlight) FontWeight.Medium else FontWeight.Normal,
                color = if (highlight) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}