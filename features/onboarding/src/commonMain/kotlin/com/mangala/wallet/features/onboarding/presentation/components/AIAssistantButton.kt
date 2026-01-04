package com.mangala.wallet.features.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily

@Composable
fun AIAssistantButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aiGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF227BFF),
            0.32692307f to Color(0xFF1C8DF9),
            0.66346156f to Color(0xFFB988EE),
            1.0f to Color(0xFFEE4D5D)
        )
    )
    
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .background(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFF227BFF).copy(alpha = 0.15f),
                        0.5f to Color(0xFFB988EE).copy(alpha = 0.12f),
                        1.0f to Color(0xFFEE4D5D).copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(1000.dp)
            ),
        color = Color.Transparent,
        shape = RoundedCornerShape(1000.dp),
        contentColor = Color.White
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🤖",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 23.8.sp,
                    fontFamily = getInterFontFamily(),
                    modifier = Modifier.offset(y = (-2).dp) // Lift emoji slightly higher
                )
                
                Text(
                    text = "Try with AI Assistant",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.17).sp,
                    lineHeight = 23.8.sp,
                    fontFamily = getInterFontFamily(),
                    style = TextStyle(
                        brush = aiGradient
                    )
                )
            }
        }
    }
}