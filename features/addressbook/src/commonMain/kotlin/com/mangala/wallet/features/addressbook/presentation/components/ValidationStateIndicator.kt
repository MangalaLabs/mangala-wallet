package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import com.mangala.wallet.features.addressbook.domain.validation.ValidationErrorType
import com.mangala.wallet.features.addressbook.domain.validation.ValidationWarningType

/**
 * Validation state types
 */
enum class ValidationState {
    IDLE,
    VALIDATING,
    VALID,
    WARNING,
    ERROR
}

/**
 * Colors for validation states
 */
object ValidationColors {
    val success = Color(0xFF4CAF50)
    val warning = Color(0xFFFFA726)
    val error = Color(0xFFE53935)
    val validating = Color(0xFF2196F3)
    val info = Color(0xFF2196F3)
    val critical = Color(0xFFB71C1C)
}

/**
 * Component to show validation state with icon and message
 */
@Composable
fun ValidationStateIndicator(
    state: ValidationState,
    message: String? = null,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state != ValidationState.IDLE,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            when (state) {
                ValidationState.VALIDATING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = ValidationColors.validating
                    )
                }
                ValidationState.VALID -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Valid",
                        modifier = Modifier.size(16.dp),
                        tint = ValidationColors.success
                    )
                }
                ValidationState.WARNING -> {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        modifier = Modifier.size(16.dp),
                        tint = ValidationColors.warning
                    )
                }
                ValidationState.ERROR -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        modifier = Modifier.size(16.dp),
                        tint = ValidationColors.error
                    )
                }
                ValidationState.IDLE -> { /* No icon */ }
            }
            
            // Message
            message?.let { msg ->
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (state) {
                        ValidationState.VALIDATING -> ValidationColors.validating
                        ValidationState.VALID -> ValidationColors.success
                        ValidationState.WARNING -> ValidationColors.warning
                        ValidationState.ERROR -> ValidationColors.error
                        ValidationState.IDLE -> MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * Small validation icon for inline display
 */
@Composable
fun ValidationIcon(
    state: ValidationState,
    modifier: Modifier = Modifier
) {
    when (state) {
        ValidationState.VALIDATING -> {
            CircularProgressIndicator(
                modifier = modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = ValidationColors.validating
            )
        }
        ValidationState.VALID -> {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Valid",
                modifier = modifier.size(20.dp),
                tint = ValidationColors.success
            )
        }
        ValidationState.WARNING -> {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                modifier = modifier.size(20.dp),
                tint = ValidationColors.warning
            )
        }
        ValidationState.ERROR -> {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = modifier.size(20.dp),
                tint = ValidationColors.error
            )
        }
        ValidationState.IDLE -> { /* No icon */ }
    }
}

/**
 * Enhanced validation state indicator with specific error/warning types
 */
@Composable
fun EnhancedValidationStateIndicator(
    state: ValidationState,
    message: String? = null,
    errorType: ValidationErrorType? = null,
    warningType: ValidationWarningType? = null,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state != ValidationState.IDLE,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon based on state and type
            when (state) {
                ValidationState.VALIDATING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = ValidationColors.validating
                    )
                }
                ValidationState.VALID -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Valid",
                        modifier = Modifier.size(16.dp),
                        tint = ValidationColors.success
                    )
                }
                ValidationState.WARNING -> {
                    val icon = when (warningType) {
                        ValidationWarningType.EVM_NETWORK_INFO -> Icons.Default.Info
                        else -> Icons.Default.Warning
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Warning",
                        modifier = Modifier.size(16.dp),
                        tint = if (warningType == ValidationWarningType.EVM_NETWORK_INFO) 
                            ValidationColors.info else ValidationColors.warning
                    )
                }
                ValidationState.ERROR -> {
                    val icon = when (errorType) {
                        ValidationErrorType.BURN_ADDRESS -> Icons.Default.Cancel
                        ValidationErrorType.NETWORK_MISMATCH -> Icons.Default.Error
                        else -> Icons.Default.Error
                    }
                    val color = when (errorType) {
                        ValidationErrorType.BURN_ADDRESS -> ValidationColors.critical
                        else -> ValidationColors.error
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Error",
                        modifier = Modifier.size(16.dp),
                        tint = color
                    )
                }
                ValidationState.IDLE -> { /* No icon */ }
            }
            
            // Message
            message?.let { msg ->
                Spacer(modifier = Modifier.width(8.dp))
                val textColor = when (state) {
                    ValidationState.VALIDATING -> ValidationColors.validating
                    ValidationState.VALID -> ValidationColors.success
                    ValidationState.WARNING -> {
                        if (warningType == ValidationWarningType.EVM_NETWORK_INFO)
                            ValidationColors.info else ValidationColors.warning
                    }
                    ValidationState.ERROR -> {
                        if (errorType == ValidationErrorType.BURN_ADDRESS)
                            ValidationColors.critical else ValidationColors.error
                    }
                    ValidationState.IDLE -> MaterialTheme.colorScheme.onSurface
                }
                
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp
                )
            }
        }
    }
}