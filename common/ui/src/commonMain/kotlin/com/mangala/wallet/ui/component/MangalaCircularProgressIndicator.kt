package com.mangala.wallet.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.ui.TextDescription2

@Composable
fun MangalaCircularProgressIndicatorFullScreen(
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    strokeWidth: Dp = 4.dp,
    color: Color = Colors.coral,
){
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Gray.copy(0.5f)),
        contentAlignment = Alignment.Center
    ) {
        MangalaCircularProgressIndicator(
            modifier = modifier.size(size),
            color = color,
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun MangalaCircularProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    strokeWidth: Dp = 4.dp,
    color: Color = Colors.coral,
){
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = strokeWidth
    )
}

@Composable
fun MangalaCircularPercentageIndicator(
    percentage: Float, // Value between 0f and 1f,
    percentageString: String,
    strokeWidth: Dp,
    modifier: Modifier,
    colorBackground: Color,
    colorForeground: Color,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(Dimensions.BoxCircleSize)
    ) {
        Canvas(modifier = modifier.fillMaxSize()) {
            // Background circle
            drawArc(
                color = colorBackground,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
            )

            // Foreground circle
            drawArc(
                color = colorForeground,
                startAngle = -90f,
                sweepAngle = 360f * percentage,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
            )
        }

        // Percentage text
        TextDescription2(
            text = "$percentageString %",
            color = colorForeground,
            fontSize = FontType.TINY,
            fontWeight = FontWeight.Medium
        )
    }
}