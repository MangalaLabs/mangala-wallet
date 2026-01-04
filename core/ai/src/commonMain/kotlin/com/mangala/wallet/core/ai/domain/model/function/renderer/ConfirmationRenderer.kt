package com.mangala.wallet.core.ai.domain.model.function.renderer

import androidx.compose.runtime.Composable
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage

/**
 * Interface for rendering confirmation UI for function calls that require user confirmation
 */
interface ConfirmationRenderer {
    val functionName: String

    @Composable
    fun RenderConfirmation(
        message: FunctionCallConfirmationRequiredMessage,
        onConfirm: () -> Unit,
        onEdit: () -> Unit,
        onDeny: () -> Unit,
        isProcessing: Boolean
    )
}