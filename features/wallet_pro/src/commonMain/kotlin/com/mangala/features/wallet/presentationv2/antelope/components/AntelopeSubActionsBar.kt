package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.WalletThemeV2

@Composable
fun AntelopeSubActionsBar(
    onBuyClick: () -> Unit,
    onSwapClick: () -> Unit,
    onPowerUpClick: () -> Unit,
    onRentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WalletThemeV2.Dimensions.spacingSmall)
    ) {
        SubActionButton(
            text = "Buy",
            emoji = "💳",
            gradientColors = listOf(
                WalletThemeV2.Colors.accentBlue.copy(alpha = 0.2f),
                WalletThemeV2.Colors.accentBlue.copy(alpha = 0.1f)
            ),
            onClick = onBuyClick,
            modifier = Modifier.weight(1f)
        )
        
        SubActionButton(
            text = "Swap",
            emoji = "🔄",
            gradientColors = listOf(
                WalletThemeV2.Colors.purple.copy(alpha = 0.2f),
                WalletThemeV2.Colors.purple.copy(alpha = 0.1f)
            ),
            onClick = onSwapClick,
            modifier = Modifier.weight(1f)
        )
        
        SubActionButton(
            text = "Power Up",
            emoji = "⚡",
            gradientColors = listOf(
                WalletThemeV2.Colors.warning.copy(alpha = 0.2f),
                WalletThemeV2.Colors.warning.copy(alpha = 0.1f)
            ),
            onClick = onPowerUpClick,
            modifier = Modifier.weight(1f)
        )
        
        SubActionButton(
            text = "Rent",
            emoji = "🏪",
            gradientColors = listOf(
                WalletThemeV2.Colors.success.copy(alpha = 0.2f),
                WalletThemeV2.Colors.success.copy(alpha = 0.1f)
            ),
            onClick = onRentClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SubActionButton(
    text: String,
    emoji: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(WalletThemeV2.Dimensions.buttonHeight)
            .clip(RoundedCornerShape(WalletThemeV2.Dimensions.cornerRadiusSmall))
            .background(
                Brush.verticalGradient(colors = gradientColors)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = WalletThemeV2.Typography.fontSizeMedium
            )
            
            Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingXSmall))
            
            Text(
                text = text,
                fontSize = WalletThemeV2.Typography.fontSizeBody,
                fontWeight = FontWeight.Medium,
                color = WalletThemeV2.Colors.primaryText,
                fontFamily = getInterFontFamily()
            )
        }
    }
}