package com.mangala.wallet.features.conversationui.presentation.components.background

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ChatBackground(
    circleBackgroundEnabled: Boolean = MaterialTheme.mangalaColors.circleGradientBackgroundEnabled,
    modifier: Modifier = Modifier.fillMaxSize(),
    afterBackgroundModifier: Modifier = Modifier.safeDrawingPadding(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.mangalaColors.bg)
            .then(afterBackgroundModifier)
            .drawBehind {
                if (circleBackgroundEnabled) {
                    drawGradientCircle(
                        center = Offset(
                            x = size.width * 0.8f, // Position towards right
                            y = -73.dp.toPx() // Top position
                        ),
                        radius = 128.5.dp.toPx(), // Half of 257dp
                        color = Color(0xFF2C0084)
                    )

                    // Bottom gradient circle (blue)
                    drawGradientCircle(
                        center = Offset(
                            x = size.width * 0.4f, // Position towards left
                            y = size.height + 200.dp.toPx() // Bottom position
                        ),
                        radius = 128.5.dp.toPx(), // Half of 257dp
                        color = Color(0xFF002293)
                    )
                }
            }
    ) {
        content()
    }
}

/**
 * Extension function to draw a gradient circle with blur effect
 */
private fun DrawScope.drawGradientCircle(
    center: Offset,
    radius: Float,
    color: Color
) {
    // Create a radial gradient that simulates blur
    val gradient = Brush.radialGradient(
        colors = listOf(
            color.copy(alpha = 0.6f),
            color.copy(alpha = 0.4f),
            color.copy(alpha = 0.2f),
            color.copy(alpha = 0.1f),
            color.copy(alpha = 0.05f),
            color.copy(alpha = 0.02f),
            color.copy(alpha = 0f)
        ),
        center = center,
        radius = radius * 3f // Multiply radius to create blur effect
    )
    
    // Draw the circle with gradient
    drawCircle(
        brush = gradient,
        radius = radius * 3f,
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