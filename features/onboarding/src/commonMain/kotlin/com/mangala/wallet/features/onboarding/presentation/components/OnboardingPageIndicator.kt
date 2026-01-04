package com.mangala.wallet.features.onboarding.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingPageIndicator(
    pageCount: Int,
    currentPage: Int,
    currentPageOffset: Float = 0f,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val distance = kotlin.math.abs(currentPage + currentPageOffset - index)
            val isActive = index == currentPage
            
            // Calculate progress based on distance from current position
            val progress = (1f - distance.coerceIn(0f, 1f))
            
            // Animate width with smooth interpolation
            val width by animateDpAsState(
                targetValue = (8.dp + (12.dp * progress)),
                animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = 400f
                ),
                label = "indicator_width"
            )
            
            // Animate opacity based on distance
            val alpha by animateFloatAsState(
                targetValue = 0.6f + (0.4f * progress),
                animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = 400f
                ),
                label = "indicator_alpha"
            )
            
            Box(
                modifier = Modifier
                    .size(
                        width = width,
                        height = 8.dp
                    )
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xFF7487FE).copy(alpha = alpha))
            )
        }
    }
}