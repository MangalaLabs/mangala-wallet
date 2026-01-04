package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Error

/**
 * Shows a summary of validation errors and warnings at the bottom of the form
 */
@Composable
fun ValidationSummary(
    validationErrors: Map<String, String>,
    warnings: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    val hasErrors = validationErrors.isNotEmpty()
    val hasWarnings = warnings.isNotEmpty()
    val shouldShow = hasErrors || hasWarnings
    
    AnimatedVisibility(
        visible = shouldShow,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    hasErrors -> ValidationColors.error.copy(alpha = 0.1f)
                    hasWarnings -> ValidationColors.warning.copy(alpha = 0.1f)
                    else -> ValidationColors.success.copy(alpha = 0.1f)
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when {
                            hasErrors -> Icons.Default.Error
                            hasWarnings -> Icons.Default.Warning
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = when {
                            hasErrors -> ValidationColors.error
                            hasWarnings -> ValidationColors.warning
                            else -> ValidationColors.success
                        }
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = when {
                            hasErrors -> "Please fix the following errors:"
                            hasWarnings -> "Please review the following warnings:"
                            else -> "All validations passed"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = when {
                            hasErrors -> ValidationColors.error
                            hasWarnings -> ValidationColors.warning
                            else -> ValidationColors.success
                        }
                    )
                }
                
                // Error/Warning list
                if (hasErrors) {
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    validationErrors.forEach { (field, message) ->
                        ValidationSummaryItem(
                            message = message,
                            color = ValidationColors.error,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
                
                if (hasWarnings) {
                    if (hasErrors) {
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                    }
                    warnings.forEach { warning ->
                        ValidationSummaryItem(
                            message = warning,
                            color = ValidationColors.warning,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ValidationSummaryItem(
    message: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = color,
            modifier = Modifier.padding(end = 8.dp),
            fontSize = 12.sp
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontSize = 12.sp
        )
    }
}