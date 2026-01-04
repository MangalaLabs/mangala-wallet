package com.mangala.wallet.wallet.presentation.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRenderer
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.presentation.ConfirmationActions
import com.mangala.wallet.core.ai.presentation.StatusFeedbackText
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

class ImportAccountConfirmationRenderer : ConfirmationRenderer {
    override val functionName: String = "navigate_to_import_account"

    @Composable
    override fun RenderConfirmation(
        message: FunctionCallConfirmationRequiredMessage,
        onConfirm: () -> Unit,
        onEdit: () -> Unit,
        onDeny: () -> Unit,
        isProcessing: Boolean
    ) {
        Column(modifier = Modifier.padding(horizontal = Dimensions.Padding.default)) {
            Text(
                message.functionDescription ?: "Import an existing account",
                style = MangalaTypography.Size14Regular(),
                color = MaterialTheme.mangalaColors.textPrimary
            )
            VerticalSpacer(Spacing.SMALL)

            ConfirmationActions(
                message = message,
                onConfirm = onConfirm,
                onDeny = onDeny,
                isProcessing = isProcessing,
                confirmButtonLabel = "Import Account",
                statusTexts = StatusFeedbackText(
                    confirmed = "✓ Navigating to import account...",
                    executed = "✓ Navigating to import account...",
                    failed = "⚠️ Navigation failed"
                )
            )
        }
    }
}
