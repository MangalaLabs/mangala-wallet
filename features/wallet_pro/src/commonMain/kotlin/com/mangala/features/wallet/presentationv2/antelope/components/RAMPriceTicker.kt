package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Ram
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowUp
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.WalletThemeV2
import kotlinx.coroutines.delay
import com.mangala.wallet.utils.DecimalFormat

@Composable
fun RAMPriceTicker(
    ramPrice: Double,
    priceChange: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { DecimalFormat("0.0000") }
    val percentFormat = remember { DecimalFormat("+#.##%;-#.##%") }
    
    var visible by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    LaunchedEffect(Unit) {
        delay(1000)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(WalletThemeV2.Animation.durationLong)) + 
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
        exit = fadeOut() + slideOutHorizontally { it }
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(WalletThemeV2.Dimensions.cornerRadiusButton))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            WalletThemeV2.Colors.cardBackground.copy(alpha = 0.95f),
                            WalletThemeV2.Colors.cardBackground.copy(alpha = 0.85f)
                        )
                    )
                )
                .clickable { onClick() }
                .padding(
                    horizontal = WalletThemeV2.Dimensions.paddingMedium,
                    vertical = WalletThemeV2.Dimensions.paddingSmall
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(WalletThemeV2.Dimensions.spacingSmall)
            ) {
                Icon(
                    imageVector = MangalaWalletPack.Ram,
                    contentDescription = "RAM",
                    tint = WalletThemeV2.Colors.accentBlue,
                    modifier = Modifier
                        .size(WalletThemeV2.Dimensions.iconSizeSmall)
                        .alpha(pulseAlpha)
                )
                
                Text(
                    text = "RAM",
                    fontSize = WalletThemeV2.Typography.fontSizeSmall,
                    fontWeight = FontWeight.Medium,
                    color = WalletThemeV2.Colors.primaryText,
                    fontFamily = getInterFontFamily()
                )
                
                Text(
                    text = "${numberFormat.format(ramPrice)} EOS/KB",
                    fontSize = WalletThemeV2.Typography.fontSizeSmall,
                    fontWeight = FontWeight.Bold,
                    color = WalletThemeV2.Colors.primaryText,
                    fontFamily = getInterFontFamily()
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (priceChange >= 0) MangalaWalletPack.ArrowUp else MangalaWalletPack.ArrowDown,
                        contentDescription = "Change",
                        tint = if (priceChange >= 0) WalletThemeV2.Colors.positiveChange else WalletThemeV2.Colors.negativeChange,
                        modifier = Modifier.size(WalletThemeV2.Dimensions.iconSizeSmall)
                    )
                    
                    Text(
                        text = percentFormat.format(priceChange / 100),
                        fontSize = WalletThemeV2.Typography.fontSizeSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (priceChange >= 0) WalletThemeV2.Colors.positiveChange else WalletThemeV2.Colors.negativeChange,
                        fontFamily = getInterFontFamily()
                    )
                }
            }
        }
    }
}