package com.mangala.wallet.core.ai.domain.model.function.renderer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.presentation.ConfirmationActions
import com.mangala.wallet.core.ai.presentation.StatusFeedbackText

class DefaultConfirmationRenderer : ConfirmationRenderer {
    override val functionName: String = "*" // Wildcard to match any function
    
    @Composable
    override fun RenderConfirmation(
        message: FunctionCallConfirmationRequiredMessage,
        onConfirm: () -> Unit,
        onEdit: () -> Unit,
        onDeny: () -> Unit,
        isProcessing: Boolean
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = message.confirmationPrompt,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                message.functionDescription?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
                
                // Function details
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Function: ${message.functionCall.name}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (message.functionCall.parameters.isNotEmpty()) {
                            Text(
                                text = "Parameters:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            message.functionCall.parameters.forEach { (key, value) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = value.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
                
                ConfirmationActions(
                    message = message,
                    onConfirm = onConfirm,
                    onDeny = onDeny,
                    isProcessing = isProcessing,
                    statusTexts = StatusFeedbackText(
                        confirmed = "✓ Function executed successfully",
                        executed = "✓ Function executed successfully",
                        failed = "⚠️ Function execution failed"
                    )
                )
            }
        }
    }
}