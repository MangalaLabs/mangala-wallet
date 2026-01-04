package com.mangala.wallet.features.addressbook.presentation.privacy

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Privacy Mode Toggle Button Component
 * 
 * Features:
 * - Animated icon transitions (eye ↔ eye-off)
 * - Visual feedback (scale, alpha, color)
 * - Accessibility support
 * - Material Design 3 styling
 * - Loading state support
 * 
 * @param isEnabled Current privacy mode state
 * @param onToggle Callback when toggle is clicked
 * @param modifier Compose modifier
 * @param isLoading Show loading state (reduces opacity, disables click)
 * @param contentDescription Custom accessibility description
 */
@Composable
fun PrivacyModeToggle(
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentDescription: String? = null
) {
    // Animation states
    val scale by animateFloatAsState(
        targetValue = if (isLoading) 0.9f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "privacy_toggle_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isLoading) 0.6f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "privacy_toggle_alpha"
    )
    
    // Icon and color selection
    val iconColor = if (isEnabled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val accessibilityDescription = contentDescription ?: if (isEnabled) {
        "Privacy mode is enabled. Tap to disable."
    } else {
        "Privacy mode is disabled. Tap to enable."
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onToggle,
            enabled = !isLoading,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
                .semantics {
                    role = Role.Switch
                    this.contentDescription = accessibilityDescription
                }
        ) {
            AnimatedContent(
                targetState = isEnabled,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220, delayMillis = 90)) + 
                     scaleIn(initialScale = 0.8f, animationSpec = tween(220, delayMillis = 90)))
                        .togetherWith(
                            fadeOut(animationSpec = tween(90)) + 
                            scaleOut(targetScale = 0.8f, animationSpec = tween(90))
                        )
                },
                label = "privacy_icon_transition"
            ) { enabled ->
                Icon(
                    imageVector = if (enabled) {
                        Icons.Outlined.VisibilityOff
                    } else {
                        Icons.Outlined.Visibility
                    },
                    contentDescription = null, // Handled by parent semantics
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Compact Privacy Mode Toggle for smaller spaces
 * 
 * @param isEnabled Current privacy mode state
 * @param onToggle Callback when toggle is clicked
 * @param modifier Compose modifier
 * @param tint Custom icon tint color
 */
@Composable
fun CompactPrivacyModeToggle(
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "compact_toggle_scale"
    )
    
    IconButton(
        onClick = {
            isPressed = true
            onToggle()
            isPressed = false
        },
        modifier = modifier
            .scale(scale)
            .size(32.dp)
    ) {
        Icon(
            imageVector = if (isEnabled) {
                Icons.Outlined.VisibilityOff
            } else {
                Icons.Outlined.Visibility
            },
            contentDescription = if (isEnabled) {
                "Disable privacy mode"
            } else {
                "Enable privacy mode"
            },
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Privacy Mode State Indicator
 * 
 * Shows current privacy state with icon only (no click action)
 * 
 * @param isEnabled Current privacy mode state
 * @param modifier Compose modifier
 * @param showBackground Show background circle for emphasis
 */
@Composable
fun PrivacyModeIndicator(
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    showBackground: Boolean = false
) {
    val iconColor = if (isEnabled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isEnabled) {
                Icons.Outlined.VisibilityOff
            } else {
                Icons.Outlined.Visibility
            },
            contentDescription = if (isEnabled) {
                "Privacy mode is enabled"
            } else {
                "Privacy mode is disabled"
            },
            tint = iconColor,
            modifier = Modifier.size(16.dp)
        )
    }
}