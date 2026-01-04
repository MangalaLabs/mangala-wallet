package com.mangala.wallet.features.addressbook.domain.functioncalling.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.ui.theme.mangalaColors
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

class AddContactConfirmationRenderer : ConfirmationRenderer {
    override val functionName: String = "add_contact"

    @Composable
    override fun RenderConfirmation(
        message: FunctionCallConfirmationRequiredMessage,
        onConfirm: () -> Unit,
        onEdit: () -> Unit,
        onDeny: () -> Unit,
        isProcessing: Boolean
    ) {
        val parameters = message.functionCall.parameters
        val name = parameters["name"]?.toString() ?: ""
        val blockchainAddress = parameters["blockchain_address_or_account_name"]?.toString() ?: ""
        val blockchainNetwork = parameters["blockchain_network"]?.toString() ?: ""
        val contactInfo = ContactInfo(
            id = "",
            name = name,
            notes = null // We'll pass the address separately to format it properly
        )

        Column(modifier = Modifier.padding(horizontal = Dimensions.Padding.default)) {
            Text("Confirm Contact Information",
                style = MangalaTypography.Size14Regular(),
                color = MaterialTheme.mangalaColors.textPrimary
            )
            VerticalSpacer(Spacing.SMALL)
            
            ContactCard(
                contact = contactInfo,
                onClick = { /* No action needed for confirmation */ },
                style = ContactCardStyles.confirmationRenderer(),
                blockchainNetwork = blockchainNetwork,
                blockchainAddress = blockchainAddress,
                onCopyAddress = { /* Address copied */ }
            )

            VerticalSpacer(Spacing.XSMALL)

            ConfirmationActions(
                message = message,
                onConfirm = onConfirm,
                onDeny = onDeny,
                isProcessing = isProcessing,
                statusTexts = StatusFeedbackText(
                    confirmed = "✓ Contact saved",
                    executed = "✓ Contact saved",
                    failed = "⚠️ Failed to save contact"
                )
            )
        }
    }

}