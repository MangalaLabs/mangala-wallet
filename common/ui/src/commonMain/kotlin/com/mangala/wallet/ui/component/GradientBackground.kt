package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors

@Composable
fun GradientBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    MaxSizeBox(modifier.background(Colors.appleBg).windowInsetsPadding(WindowInsets.safeDrawing).drawBehind {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val topColor = Colors.softPeach
        val leftColor = Color(0xFFFBDACC)
        val rightColor = Color(0xFFF8E8FF)
        val bottomColor = Color(0xFFE9E9FF)

        val radius = 500.dp.toPx()

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(topColor, Color.Transparent),
                center = Offset(canvasWidth / 2, 0f),
                radius = radius,
                tileMode = TileMode.Clamp
            ),
            radius = radius,
            center = Offset(canvasWidth / 2, 0f)
        )
        // Left
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(leftColor, Color.Transparent),
                center = Offset(0f, canvasHeight / 2),
                radius = radius,
                tileMode = TileMode.Clamp
            ),
            radius = radius,
            center = Offset(0f, canvasHeight / 2)
        )
        // Right
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(rightColor, Color.Transparent),
                center = Offset(canvasWidth, canvasHeight / 2),
                radius = radius,
                tileMode = TileMode.Clamp
            ),
            radius = radius,
            center = Offset(canvasWidth, canvasHeight / 2)
        )
        // Bottom
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(bottomColor, Color.Transparent),
                center = Offset(canvasWidth / 2, canvasHeight),
                radius = radius,
                tileMode = TileMode.Clamp
            ),
            radius = radius,
            center = Offset(canvasWidth / 2, canvasHeight)
        )
    }) {
        content()
    }
}