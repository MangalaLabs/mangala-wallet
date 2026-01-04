package com.mangala.wallet.features.chains.antelope.presentation.resources.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily

@Composable
fun ResourceSkillBar(
    onBoostClick: () -> Unit,
    onTradeClick: () -> Unit,
    onAutoOptimizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkillButton(
                emoji = "⚡",
                label = "Boost",
                backgroundColors = listOf(
                    Color(0xFF8B5CF6).copy(alpha = 0.2f),
                    Color(0xFF8B5CF6).copy(alpha = 0.1f)
                ),
                onClick = onBoostClick
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            SkillButton(
                emoji = "💎",
                label = "Trade",
                backgroundColors = listOf(
                    Color(0xFFFBBF24).copy(alpha = 0.2f),
                    Color(0xFFFBBF24).copy(alpha = 0.1f)
                ),
                onClick = onTradeClick
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            SkillButton(
                emoji = "🎯",
                label = "Auto",
                backgroundColors = listOf(
                    Color(0xFF10B981).copy(alpha = 0.2f),
                    Color(0xFF10B981).copy(alpha = 0.1f)
                ),
                onClick = onAutoOptimizeClick
            )
        }
    }
}

@Composable
private fun SkillButton(
    emoji: String,
    label: String,
    backgroundColors: List<Color>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .skillButtonEffect()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(colors = backgroundColors)
            )
            .border(
                width = 2.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp
            )
            
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                fontFamily = getInterFontFamily()
            )
        }
    }
}

// Extension function for skill button hover effect
@Composable
private fun Modifier.skillButtonEffect(): Modifier {
    return this
}