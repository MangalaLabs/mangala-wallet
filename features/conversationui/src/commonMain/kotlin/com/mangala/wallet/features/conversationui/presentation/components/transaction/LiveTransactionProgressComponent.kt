package com.mangala.wallet.features.conversationui.presentation.components.transaction

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Enhanced transaction progress component that integrates with real-time transaction broadcasting
 */
@Composable
fun LiveTransactionProgressComponent(
    transactionId: String?,
    onProgressUpdate: (ProgressStep) -> Unit,
    onComplete: (TransactionResult) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var steps by remember { mutableStateOf<List<ProgressStep>>(emptyList()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var isComplete by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    
    // Initialize default steps
    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            steps = createDefaultTransactionSteps()
            simulateTransactionProgress(
                onStepUpdate = { step ->
                    steps = steps.map { if (it.id == step.id) step else it }
                    onProgressUpdate(step)
                },
                onStepChange = { stepIndex ->
                    currentStepIndex = stepIndex
                },
                onComplete = { result ->
                    isComplete = true
                    onComplete(result)
                },
                onError = { error ->
                    hasError = true
                    onError(error)
                }
            )
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        isComplete -> "Transaction Complete"
                        hasError -> "Transaction Failed"
                        else -> "Processing Transaction"
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = when {
                        isComplete -> MaterialTheme.colorScheme.primary
                        hasError -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                
                if (!isComplete && !hasError) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
            
            // Transaction ID (if available)
            transactionId?.let {
                TransactionIdSection(
                    transactionId = it,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Divider()
            
            // Progress steps
            steps.forEachIndexed { index, step ->
                ProgressStepItem(
                    step = step,
                    isActive = index == currentStepIndex && !isComplete && !hasError,
                    isComplete = step.status == ProgressStepStatus.COMPLETED,
                    isLast = index == steps.lastIndex
                )
            }
            
            // Action buttons (for completed/failed states)
            if (isComplete || hasError) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (hasError) {
                        Arrangement.spacedBy(12.dp)
                    } else {
                        Arrangement.Center
                    }
                ) {
                    if (hasError) {
                        OutlinedButton(
                            onClick = { /* Retry logic */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Retry")
                        }
                    }
                    
                    Button(
                        onClick = { /* Close/View details logic */ },
                        modifier = if (hasError) Modifier.weight(1f) else Modifier.wrapContentWidth()
                    ) {
                        Text(if (isComplete) "View Details" else "Close")
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionIdSection(
    transactionId: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Transaction ID",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = transactionId,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun createDefaultTransactionSteps(): List<ProgressStep> {
    return listOf(
        ProgressStep(
            id = "validate",
            title = "Validating Transaction",
            description = "Checking transaction parameters and balances",
            status = ProgressStepStatus.PENDING
        ),
        ProgressStep(
            id = "estimate_fees",
            title = "Estimating Network Fees",
            description = "Calculating optimal gas/fees for the transaction",
            status = ProgressStepStatus.PENDING
        ),
        ProgressStep(
            id = "sign",
            title = "Signing Transaction",
            description = "Creating cryptographic signature",
            status = ProgressStepStatus.PENDING
        ),
        ProgressStep(
            id = "broadcast",
            title = "Broadcasting to Network",
            description = "Submitting transaction to blockchain network",
            status = ProgressStepStatus.PENDING
        ),
        ProgressStep(
            id = "confirm",
            title = "Awaiting Confirmation",
            description = "Waiting for network confirmation",
            status = ProgressStepStatus.PENDING
        )
    )
}

private suspend fun simulateTransactionProgress(
    onStepUpdate: (ProgressStep) -> Unit,
    onStepChange: (Int) -> Unit,
    onComplete: (TransactionResult) -> Unit,
    onError: (String) -> Unit
) {
    val steps = createDefaultTransactionSteps()
    
    try {
        steps.forEachIndexed { index, step ->
            onStepChange(index)
            
            // Start step
            onStepUpdate(step.copy(status = ProgressStepStatus.IN_PROGRESS))
            
            // Simulate processing time
            delay(when (step.id) {
                "validate" -> 1000L
                "estimate_fees" -> 1500L
                "sign" -> 2000L
                "broadcast" -> 3000L
                "confirm" -> 4000L
                else -> 1000L
            })
            
            // Complete step
            onStepUpdate(step.copy(
                status = ProgressStepStatus.COMPLETED,
                description = when (step.id) {
                    "validate" -> "Transaction validated successfully"
                    "estimate_fees" -> "Network fees estimated: 0.0021 ETH"
                    "sign" -> "Transaction signed with private key"
                    "broadcast" -> "Transaction submitted to network"
                    "confirm" -> "Transaction confirmed in block #1234567"
                    else -> "Step completed"
                }
            ))
        }
        
        // Transaction complete
        onComplete(
            TransactionResult.Success(
                transactionHash = "0x1234567890abcdef",
                blockNumber = "1234567",
                gasUsed = "21000",
                status = "confirmed"
            )
        )
        
    } catch (e: Exception) {
        onError("Transaction failed: ${e.message}")
    }
}

/**
 * Result of a transaction operation
 */
sealed class TransactionResult {
    data class Success(
        val transactionHash: String,
        val blockNumber: String,
        val gasUsed: String,
        val status: String
    ) : TransactionResult()
    
    data class Failed(
        val error: String,
        val code: String? = null
    ) : TransactionResult()
}

/**
 * Compact version for displaying transaction progress in chat bubbles
 */
@Composable
fun CompactTransactionProgress(
    currentStep: String,
    totalSteps: Int,
    currentStepIndex: Int,
    isComplete: Boolean,
    hasError: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Progress indicator
            when {
                isComplete -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Complete",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                hasError -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
                else -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
            
            // Status text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        isComplete -> "Transaction Complete"
                        hasError -> "Transaction Failed"
                        else -> currentStep
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                
                if (!isComplete && !hasError) {
                    Text(
                        text = "Step ${currentStepIndex + 1} of $totalSteps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}