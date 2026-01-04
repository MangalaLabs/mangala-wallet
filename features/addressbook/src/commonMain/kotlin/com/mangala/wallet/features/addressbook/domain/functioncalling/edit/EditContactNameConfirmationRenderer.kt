package com.mangala.wallet.features.addressbook.domain.functioncalling.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRenderer
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.presentation.ConfirmationActions
import com.mangala.wallet.core.ai.presentation.StatusFeedbackText
import com.mangala.wallet.features.addressbook.domain.model.ContactInfo
import com.mangala.wallet.features.addressbook.presentation.components.ContactCard
import com.mangala.wallet.features.addressbook.presentation.components.ContactCardStyles
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography

class EditContactNameConfirmationRenderer : ConfirmationRenderer {
    override val functionName: String = "edit_contact_name"

    @Composable
    override fun RenderConfirmation(
        message: FunctionCallConfirmationRequiredMessage,
        onConfirm: () -> Unit,
        onEdit: () -> Unit,
        onDeny: () -> Unit,
        isProcessing: Boolean
    ) {
        val parameters = message.functionCall.parameters
        val contactId = parameters["contact_id"]?.toString() ?: ""
        val oldName = parameters["old_name"]?.toString() ?: ""
        val newName = parameters["new_name"]?.toString() ?: ""
        val blockchainAddress = parameters["blockchain_address_or_account_name"]?.toString() ?: ""
        val blockchainNetwork = parameters["blockchain_network"]?.toString() ?: ""
        
        val contactInfo = ContactInfo(
            id = contactId,
            name = newName,
            notes = null
        )

        Column(modifier = Modifier.padding(horizontal = Dimensions.Padding.default)) {
            Text("Confirm Name Change",
                style = MangalaTypography.Size14Regular(),
                color = MaterialTheme.mangalaColors.textPrimary
            )
            VerticalSpacer(Spacing.SMALL)
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.mangalaColors.bgInnerCard
                ),
                shape = RoundedCornerShape(CornerRadius.Tiny),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Dimensions.Padding.default),
                    verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Current name:",
                            style = MangalaTypography.Size13Regular(),
                            color = MaterialTheme.mangalaColors.textSecondary
                        )
                        Text(
                            text = oldName,
                            style = MangalaTypography.Size13SemiBold(),
                            color = MaterialTheme.mangalaColors.textPrimary
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "New name:",
                            style = MangalaTypography.Size13Regular(),
                            color = MaterialTheme.mangalaColors.textSecondary
                        )
                        Text(
                            text = newName,
                            style = MangalaTypography.Size13SemiBold(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            VerticalSpacer(Spacing.SMALL)
            
            ContactCard(
                contact = contactInfo,
                onClick = { /* No action needed for confirmation */ },
                style = ContactCardStyles.confirmationRenderer(),
                blockchainNetwork = blockchainNetwork,
                blockchainAddress = blockchainAddress,
                onCopyAddress = { /* Address copied */ },
            )

            VerticalSpacer(Spacing.XSMALL)

            ConfirmationActions(
                message = message,
                onConfirm = onConfirm,
                onDeny = onDeny,
                isProcessing = isProcessing,
                statusTexts = StatusFeedbackText(
                    confirmed = "✓ Contact name updated",
                    executed = "✓ Contact name updated",
                    failed = "⚠️ Failed to update contact name"
                )
            )
        }
    }

}