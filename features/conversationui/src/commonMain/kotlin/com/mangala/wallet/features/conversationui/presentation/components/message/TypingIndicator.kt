package com.mangala.wallet.features.conversationui.presentation.components.message

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * A modern typing indicator component based on the screenshot design
 * Shows animated dots to indicate someone is typing
 *
 * @param modifier Optional modifier for the component
 */
@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.SMALL)
    ) {
        TypingBubble()
    }
}

@Composable
private fun TypingBubble() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.Medium))
            .background(MaterialTheme.mangalaColors.bgAlpha)
            .padding(horizontal = Spacing.TINY, vertical = Spacing.XSMALL)
    ) {
        AnimatedTypingDots()
    }
}

@Composable
private fun AnimatedTypingDots(modifier: Modifier = Modifier) {
    val dotCount = 3
    val activeDuration = 300 // How long each dot stays active
    val cycleDuration = activeDuration * dotCount // Total cycle time for all 3 dots

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "typing_dots")

            val progress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = cycleDuration,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "cycle_progress"
            )
            
            // Calculate if this dot should be active based on the progress
            val dotStartTime = index.toFloat() / dotCount
            val dotEndTime = (index + 1).toFloat() / dotCount
            val isActive = progress >= dotStartTime && progress < dotEndTime
            
            val color = if (isActive) {
                MaterialTheme.mangalaColors.textPrimary // Active color (bright)
            } else {
                MaterialTheme.mangalaColors.textSecondary // Inactive color (dim)
            }

            Box(
                modifier = Modifier
                    .size(8.dp) // Fixed size - no scaling
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}