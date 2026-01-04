package com.mangala.wallet.features.addressbook.domain.functioncalling.delete

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

class DeleteContactConfirmationRenderer : ConfirmationRenderer {
    override val functionName: String = "delete_contact"

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
        val contactName = parameters["contact_name"]?.toString() ?: ""
        val blockchainAddress = parameters["blockchain_address_or_account_name"]?.toString() ?: ""
        val blockchainNetwork = parameters["blockchain_network"]?.toString() ?: ""
        
        val contactInfo = ContactInfo(
            id = contactId,
            name = contactName,
            notes = null
        )

        Column(modifier = Modifier.padding(horizontal = Dimensions.Padding.default)) {
            Text("Confirm Contact Deletion",
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
                    Text(
                        text = "⚠️ This action cannot be undone. The contact and all associated addresses will be permanently deleted.",
                        style = MangalaTypography.Size13Regular(),
                        color = Color(0xFFEF4444)
                    )
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
                    confirmed = "✓ Contact deleted",
                    executed = "✓ Contact deleted",
                    failed = "⚠️ Failed to delete contact"
                )
            )
        }
    }

}