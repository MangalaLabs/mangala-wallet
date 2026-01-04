package com.mangala.wallet.features.send_base.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRenderer
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.presentation.ConfirmationActions
import com.mangala.wallet.core.ai.presentation.StatusFeedbackText
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

class MemoConfirmationRenderer : ConfirmationRenderer {
    override val functionName: String = "confirm_add_memo"
    
    @Composable
    override fun RenderConfirmation(
        message: FunctionCallConfirmationRequiredMessage,
        onConfirm: () -> Unit,
        onEdit: () -> Unit,
        onDeny: () -> Unit,
        isProcessing: Boolean
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f).padding(horizontal = Dimensions.Padding.default),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Add memo to transaction?",
                style = MangalaTypography.Size17SemiBold(),
                color = MaterialTheme.mangalaColors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (message.executionStatus == ExecutionStatus.PENDING) {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.mangalaColors.bgInnerCard,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "A memo is an optional message that will be included with your transaction. The recipient will be able to see this message.",
                            style = MangalaTypography.Size12Regular(),
                            color = MaterialTheme.mangalaColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            ConfirmationActions(
                message = message,
                onConfirm = onConfirm,
                onDeny = onDeny,
                isProcessing = isProcessing,
                statusTexts = StatusFeedbackText(
                    confirmed = "✓ Memo added",
                    executed = "✓ Memo added",
                    failed = "⚠️ Failed to add memo"
                ),
                confirmButtonLabel = "Add Memo",
                cancelButtonLabel = "Skip"
            )
        }
    }
}