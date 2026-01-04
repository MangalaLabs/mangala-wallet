package com.mangala.wallet.core.ai.presentation

import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus

data class StatusFeedbackText(
    val confirmed: String = "✓ Confirmed",
    val executed: String = "✓ Executed",
    val cancelled: String = "✗ Cancelled",
    val failed: String = "⚠️ Failed",
    val pending: String = "⏳ Pending",
    val expired: String = "⏱️ Expired"
) {
    fun getTextForStatus(status: ExecutionStatus): String = when (status) {
        ExecutionStatus.CONFIRMED -> confirmed
        ExecutionStatus.EXECUTED -> executed
        ExecutionStatus.CANCELLED -> cancelled
        ExecutionStatus.FAILED -> failed
        ExecutionStatus.PENDING -> pending
        ExecutionStatus.EXPIRED -> expired
    }
}