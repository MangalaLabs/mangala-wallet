package com.mangala.wallet.core.ai.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.ui.theme.MangalaTypography

@Composable
fun StatusFeedback(
    status: ExecutionStatus,
    statusTexts: StatusFeedbackText = StatusFeedbackText(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (text, color) = when (status) {
            ExecutionStatus.CONFIRMED -> statusTexts.confirmed to Color(0xFF10B981)
            ExecutionStatus.EXECUTED -> statusTexts.executed to Color(0xFF10B981)
            ExecutionStatus.CANCELLED -> statusTexts.cancelled to Color(0xFF6B7280)
            ExecutionStatus.FAILED -> statusTexts.failed to Color(0xFFEF4444)
            ExecutionStatus.PENDING -> statusTexts.pending to Color(0xFFF59E0B)
            ExecutionStatus.EXPIRED -> statusTexts.expired to Color(0xFF6B7280)
        }
        
        Text(
            text = text,
            style = MangalaTypography.Size14Regular(),
            color = color
        )
    }
}