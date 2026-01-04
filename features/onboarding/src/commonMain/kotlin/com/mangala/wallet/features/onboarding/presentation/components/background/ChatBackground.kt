package com.mangala.wallet.features.onboarding.presentation.components.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun OnboardingGradientBackground(
    circleBackgroundEnabled: Boolean = true, // Always enabled for onboarding
    modifier: Modifier = Modifier.fillMaxSize(),
    afterBackgroundModifier: Modifier = Modifier.safeDrawingPadding(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0A0E1A)) // Exact Figma background color
//            .background(Color.Black) // Exact Figma background color
            .then(afterBackgroundModifier)
            .drawBehind {
                if (circleBackgroundEnabled) {
                    // Circle 1: Top right circle with center at top right corner
                    drawGradientCircle(
                        center = Offset(
                            x = size.width, // Right edge of screen
                            y = 0f // Top edge of screen
                        ),
                        radius = 300.dp.toPx(), // Full radius as specified
                        color = Color(0xFF5307EB) // Exact color from Figma
                    )

                    // Circle 2: Bottom center circle with center at middle bottom
                    drawGradientCircle(
                        center = Offset(
                            x = size.width / 2f, // Center horizontally
                            y = size.height // Bottom edge of screen
                        ),
                        radius = 300.dp.toPx(), // Full radius as specified
                        color = Color(0xFF6013F8) // Exact color from Figma
                    )
                }
            }
    ) {
        content()
    }
}

/**
 * Extension function to draw a gradient circle with enhanced contrast
 * Creates a visible, impactful background effect
 */
private fun DrawScope.drawGradientCircle(
    center: Offset,
    radius: Float,
    color: Color
) {
    // Reduced blur radius for less contrast
    val blurRadius = radius + 300.dp.toPx() // Less blur for reduced contrast
    
    val gradient = Brush.radialGradient(
        colors = listOf(
            color.copy(alpha = 0.12f), // Very soft center for minimal contrast
            color.copy(alpha = 0.10f),
            color.copy(alpha = 0.08f),
            color.copy(alpha = 0.06f),
            color.copy(alpha = 0.04f),
            color.copy(alpha = 0.03f),
            color.copy(alpha = 0.02f),
            color.copy(alpha = 0.01f),
            color.copy(alpha = 0.005f),
            color.copy(alpha = 0f)
        ),
        center = center,
        radius = blurRadius
    )
    
    // Draw the circle with minimal visibility
    drawCircle(
        brush = gradient,
        radius = blurRadius,
        center = center
    )
}

/**
 * Alternative implementation using Box composables with gradient backgrounds
 */
@Composable
fun ChatBackgroundAlternative(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.mangalaColors.bg) // Dark base background
    ) {
        // Top gradient blur
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x992C0084), // Purple with transparency
                            Color(0x662C0084),
                            Color(0x332C0084),
                            Color(0x1A2C0084),
                            Color(0x0D2C0084),
                            Color.Transparent
                        ),
                        center = Offset(0.8f, -0.1f), // Top right
                        radius = 800f
                    )
                )
        )
        
        // Bottom gradient blur
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x99002293), // Blue with transparency
                            Color(0x66002293),
                            Color(0x33002293),
                            Color(0x1A002293),
                            Color(0x0D002293),
                            Color.Transparent
                        ),
                        center = Offset(0.4f, 1.3f), // Bottom left
                        radius = 800f
                    )
                )
        )
        
        // Content on top of background
        content()
    }
}

/**
 * Simplified chat background with solid gradients
 */
@Composable
fun SimpleChatBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.mangalaColors.bg, // Dark top
                        Color(0xFF0D1220), // Slightly lighter middle
                        MaterialTheme.mangalaColors.bg  // Dark bottom
                    )
                )
            )
    ) {
        content()
    }
}