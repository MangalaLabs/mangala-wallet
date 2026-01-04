package com.mangala.wallet.features.conversationui.presentation.components.transaction

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ProgressStep(
    val id: String,
    val title: String,
    val description: String,
    val status: ProgressStepStatus = ProgressStepStatus.PENDING,
    val error: String? = null
)

enum class ProgressStepStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

@Composable
fun TransactionProgressComponent(
    steps: List<ProgressStep>,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Transaction Progress",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            steps.forEachIndexed { index, step ->
                ProgressStepItem(
                    step = step,
                    isActive = index == currentStep,
                    isComplete = index < currentStep,
                    isLast = index == steps.lastIndex
                )
            }
        }
    }
}

@Composable
fun ProgressStepItem(
    step: ProgressStep,
    isActive: Boolean,
    isComplete: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProgressStepIndicator(
                status = when {
                    step.status == ProgressStepStatus.FAILED -> ProgressStepStatus.FAILED
                    isComplete -> ProgressStepStatus.COMPLETED
                    isActive -> ProgressStepStatus.IN_PROGRESS
                    else -> ProgressStepStatus.PENDING
                }
            )
            
            if (!isLast) {
                ProgressStepConnector(
                    isActive = isComplete || isActive,
                    isComplete = isComplete
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                ),
                color = when {
                    step.status == ProgressStepStatus.FAILED -> MaterialTheme.colorScheme.error
                    isActive -> MaterialTheme.colorScheme.primary
                    isComplete -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            step.error?.let { error ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            if (!isLast) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProgressStepIndicator(
    status: ProgressStepStatus,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (status) {
            ProgressStepStatus.PENDING -> {
                Canvas(modifier = Modifier.size(24.dp)) {
                    drawCircle(
                        color = Color.LightGray,
                        radius = 12.dp.toPx(),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                    )
                }
            }
            
            ProgressStepStatus.IN_PROGRESS -> {
                val infiniteTransition = rememberInfiniteTransition(label = "progress")
                val angle by infiniteTransition.animateFloat(
                    initialValue = 0F,
                    targetValue = 360F,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ), label = "rotation"
                )
                
                Canvas(
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(angle)
                ) {
                    drawArc(
                        color = Color.Blue,
                        startAngle = 0f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
            
            ProgressStepStatus.COMPLETED -> {
                Canvas(modifier = Modifier.size(24.dp)) {
                    drawCircle(
                        color = Color.Green,
                        radius = 12.dp.toPx()
                    )
                    
                    val checkPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(6.dp.toPx(), 12.dp.toPx())
                        lineTo(10.dp.toPx(), 16.dp.toPx())
                        lineTo(18.dp.toPx(), 8.dp.toPx())
                    }
                    
                    drawPath(
                        path = checkPath,
                        color = Color.White,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
            
            ProgressStepStatus.FAILED -> {
                Canvas(modifier = Modifier.size(24.dp)) {
                    drawCircle(
                        color = Color.Red,
                        radius = 12.dp.toPx()
                    )
                    
                    val crossSize = 6.dp.toPx()
                    val center = Offset(12.dp.toPx(), 12.dp.toPx())
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x - crossSize, center.y - crossSize),
                        end = Offset(center.x + crossSize, center.y + crossSize),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x + crossSize, center.y - crossSize),
                        end = Offset(center.x - crossSize, center.y + crossSize),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressStepConnector(
    isActive: Boolean,
    isComplete: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .width(2.dp)
            .height(32.dp)
    ) {
        drawLine(
            color = when {
                isComplete -> Color.Green
                isActive -> Color.Blue
                else -> Color.LightGray
            },
            start = Offset(1.dp.toPx(), 0f),
            end = Offset(1.dp.toPx(), size.height),
            strokeWidth = 2.dp.toPx()
        )
    }
}