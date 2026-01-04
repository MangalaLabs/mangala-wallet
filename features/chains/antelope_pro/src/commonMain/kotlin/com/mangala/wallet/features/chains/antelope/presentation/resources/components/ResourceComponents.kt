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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.features.chains.antelope.presentation.resources.model.ResourceRarity
import com.mangala.wallet.features.chains.antelope.presentation.resources.model.StatusEffect
import com.mangala.wallet.features.chains.antelope.presentation.resources.model.StatusEffectType

@Composable
fun ResourceCardBase(
    rarity: ResourceRarity,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val (backgroundColor, borderColor) = when (rarity) {
        ResourceRarity.EPIC -> {
            listOf(
                Color(0xFF8B5CF6).copy(alpha = 0.1f),
                Color(0xFF8B5CF6).copy(alpha = 0.05f)
            ) to Color(0xFF8B5CF6).copy(alpha = 0.3f)
        }
        ResourceRarity.LEGENDARY -> {
            listOf(
                Color(0xFFFFD700).copy(alpha = 0.1f),
                Color(0xFFFFD700).copy(alpha = 0.05f)
            ) to Color(0xFFFFD700).copy(alpha = 0.3f)
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = backgroundColor
                )
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .then(
                if (rarity == ResourceRarity.LEGENDARY) {
                    Modifier.legendaryGlow()
                } else {
                    Modifier
                }
            )
            .padding(20.dp)
    ) {
        content()
    }
}

@Composable
fun ResourceIcon(
    emoji: String,
    gradientColors: List<Color>
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = gradientColors.map { it.copy(alpha = 0.2f) }
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 32.sp
        )
    }
}

@Composable
fun ResourceProgressBar(
    current: Number,
    max: Number,
    percentage: Int,
    gradientColors: List<Color>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // Fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(percentage / 100f)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(colors = gradientColors)
                )
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = gradientColors.first()
                )
        )
        
        // Text
        Text(
            text = "$percentage% • $current / $max",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center),
            fontFamily = getInterFontFamily()
        )
    }
}

@Composable
fun ResourceActionButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isPrimary) {
                    Color(0xFF8B5CF6).copy(alpha = 0.2f)
                } else {
                    Color.White.copy(alpha = 0.05f)
                }
            )
            .border(
                width = 1.dp,
                color = if (isPrimary) {
                    Color(0xFF8B5CF6).copy(alpha = 0.3f)
                } else {
                    Color.White.copy(alpha = 0.1f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPrimary) Color(0xFFA78BFA) else Color.White,
            textAlign = TextAlign.Center,
            fontFamily = getInterFontFamily()
        )
    }
}

@Composable
fun StatusEffects(
    effects: List<StatusEffect>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        effects.forEach { effect ->
            StatusEffectChip(effect = effect)
        }
    }
}

@Composable
private fun StatusEffectChip(
    effect: StatusEffect
) {
    val (borderColor, textColor) = when (effect.type) {
        StatusEffectType.BUFF -> Color(0xFF10B981).copy(alpha = 0.3f) to Color(0xFF10B981)
        StatusEffectType.DEBUFF -> Color(0xFFEF4444).copy(alpha = 0.3f) to Color(0xFFEF4444)
    }
    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "✨",
            fontSize = 10.sp
        )
        
        Text(
            text = effect.name,
            fontSize = 10.sp,
            color = textColor,
            fontFamily = getInterFontFamily()
        )
    }
}

// Extension function for legendary glow animation
private fun Modifier.legendaryGlow(): Modifier {
    return this.shadow(
        elevation = 20.dp,
        shape = RoundedCornerShape(20.dp),
        spotColor = Color(0xFFFFD700).copy(alpha = 0.2f)
    )
}