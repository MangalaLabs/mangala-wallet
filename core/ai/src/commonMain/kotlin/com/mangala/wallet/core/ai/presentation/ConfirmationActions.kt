package com.mangala.wallet.core.ai.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun ConfirmationActions(
    message: FunctionCallConfirmationRequiredMessage,
    onConfirm: () -> Unit,
    onDeny: () -> Unit,
    isProcessing: Boolean,
    statusTexts: StatusFeedbackText,
    confirmButtonLabel: String = MR.strings.all_save.desc().localized(),
    cancelButtonLabel: String = "Cancel",
    modifier: Modifier = Modifier
) {
    if (message.executionStatus == ExecutionStatus.PENDING) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MangalaGradientButton(
                onClick = onDeny,
                enabled = !isProcessing,
                modifier = Modifier.defaultMinSize(minWidth = 114.dp),
                buttonStyle = MangalaButtonStyle.SOLID_GRAY,
                size = MangalaButtonSize.Small
            ) {
                Text(
                    cancelButtonLabel, 
                    style = MangalaTypography.Size13SemiBold(), 
                    color = Color(0xFFFFFFFF)
                )
            }

            MangalaGradientButton(
                label = confirmButtonLabel,
                onClick = onConfirm,
                enabled = !isProcessing,
                modifier = Modifier.defaultMinSize(minWidth = 114.dp),
                style = MangalaTypography.Size13SemiBold(),
                size = MangalaButtonSize.Small
            )
        }
    } else {
        StatusFeedback(
            status = message.executionStatus,
            statusTexts = statusTexts,
            modifier = modifier
        )
    }
}