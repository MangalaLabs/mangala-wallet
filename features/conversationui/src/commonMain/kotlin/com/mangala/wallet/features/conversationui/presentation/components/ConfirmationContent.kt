package com.mangala.wallet.features.conversationui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererRegistry
import com.mangala.wallet.core.ai.domain.model.function.renderer.DefaultConfirmationRenderer
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus

/**
 * Renders confirmation content using the appropriate renderer from the registry
 */
@Composable
fun ConfirmationContent(
    message: FunctionCallConfirmationRequiredMessage,
    registry: ConfirmationRendererRegistry,
    onConfirm: () -> Unit,
    onEdit: () -> Unit,
    onDeny: () -> Unit,
    isProcessing: Boolean = false,
    confirmationStatus: ExecutionStatus = ExecutionStatus.PENDING
) {
    val renderer = registry.getRenderer(message.functionCall.name) ?: DefaultConfirmationRenderer()
    
    Box {
        renderer.RenderConfirmation(
            message = message,
            onConfirm = onConfirm,
            onEdit = onEdit,
            onDeny = onDeny,
            isProcessing = isProcessing
        )
        
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}