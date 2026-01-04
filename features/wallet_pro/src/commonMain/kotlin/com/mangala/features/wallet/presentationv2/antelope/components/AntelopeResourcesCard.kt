package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Cpu
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Net
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Ram
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.WalletThemeV2
import kotlinx.coroutines.delay

@Composable
fun AntelopeResourcesCard(
    cpuUsed: Int,
    cpuMax: Int,
    netUsed: Int,
    netMax: Int,
    ramUsed: Int,
    ramMax: Int,
    ramPrice: Double,
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationTriggered by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        animationTriggered = true
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(WalletThemeV2.Dimensions.cornerRadiusLarge))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        WalletThemeV2.Colors.cardBackground,
                        WalletThemeV2.Colors.cardBackground.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WalletThemeV2.Dimensions.paddingLarge)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡",
                        fontSize = WalletThemeV2.Typography.fontSizeLarge
                    )
                    
                    Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingSmall))
                    
                    Text(
                        text = "Resources",
                        fontSize = WalletThemeV2.Typography.fontSizeLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = WalletThemeV2.Colors.primaryText,
                        fontFamily = getInterFontFamily()
                    )
                }
                
                Row(
                    modifier = Modifier.clickable { onManageClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Manage",
                        fontSize = WalletThemeV2.Typography.fontSizeBody,
                        color = WalletThemeV2.Colors.accentBlue,
                        fontWeight = FontWeight.Medium,
                        fontFamily = getInterFontFamily()
                    )
                    
                    Icon(
                        imageVector = MangalaWalletPack.ArrowRight,
                        contentDescription = "Manage",
                        tint = WalletThemeV2.Colors.accentBlue,
                        modifier = Modifier.size(WalletThemeV2.Dimensions.iconSizeSmall)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingLarge))
            
            // CPU Resource
            ResourceItem(
                icon = MangalaWalletPack.Cpu,
                label = "CPU",
                used = cpuUsed,
                max = cpuMax,
                color = WalletThemeV2.Colors.accentBlue,
                animationTriggered = animationTriggered
            )
            
            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))
            
            // NET Resource
            ResourceItem(
                icon = MangalaWalletPack.Net,
                label = "NET",
                used = netUsed,
                max = netMax,
                color = WalletThemeV2.Colors.purple,
                animationTriggered = animationTriggered
            )
            
            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))
            
            // RAM Resource
            ResourceItem(
                icon = MangalaWalletPack.Ram,
                label = "RAM",
                used = ramUsed,
                max = ramMax,
                color = WalletThemeV2.Colors.success,
                animationTriggered = animationTriggered,
                subtitle = "@ $ramPrice EOS/KB"
            )
        }
    }
}

@Composable
private fun ResourceItem(
    icon: ImageVector,
    label: String,
    used: Int,
    max: Int,
    color: Color,
    animationTriggered: Boolean,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    val percentage = if (max > 0) used.toFloat() / max else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationTriggered) percentage else 0f,
        animationSpec = tween(
            durationMillis = WalletThemeV2.Animation.durationExtraLong,
            delayMillis = 200
        )
    )
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(WalletThemeV2.Dimensions.iconSizeMedium)
                )
                
                Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingSmall))
                
                Column {
                    Text(
                        text = label,
                        fontSize = WalletThemeV2.Typography.fontSizeMedium,
                        fontWeight = FontWeight.Medium,
                        color = WalletThemeV2.Colors.primaryText,
                        fontFamily = getInterFontFamily()
                    )
                    
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = WalletThemeV2.Typography.fontSizeSmall,
                            color = WalletThemeV2.Colors.tertiaryText,
                            fontFamily = getInterFontFamily()
                        )
                    }
                }
            }
            
            Text(
                text = "$used / $max",
                fontSize = WalletThemeV2.Typography.fontSizeBody,
                color = WalletThemeV2.Colors.secondaryText,
                fontFamily = getInterFontFamily()
            )
        }
        
        Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingSmall))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(WalletThemeV2.Colors.secondaryBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        }
    }
}