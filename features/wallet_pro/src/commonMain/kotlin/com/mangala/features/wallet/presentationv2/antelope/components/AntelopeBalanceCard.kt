package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Show
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Hide
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.truncateDecimal

@Composable
fun AntelopeBalanceCard(
    totalBalance: Double,
    usdEquivalent: Double,
    pnlPercentage: Double,
    pnlAmount: Double,
    isBalanceHidden: Boolean,
    sparklineData: List<Float>,
    onToggleHideBalance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { DecimalFormat("#,##0.00") }
    val percentFormat = remember { DecimalFormat("+#.##%;-#.##%") }
    
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
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WalletThemeV2.Dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(WalletThemeV2.Dimensions.spacingSmall)
        ) {
            // Header with diamond emoji and privacy toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💎",
                        fontSize = WalletThemeV2.Typography.fontSizeLarge
                    )
                    
                    Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingSmall))
                    
                    Text(
                        text = "Total Balance",
                        fontSize = WalletThemeV2.Typography.fontSizeBody,
                        color = WalletThemeV2.Colors.secondaryText,
                        fontFamily = getInterFontFamily()
                    )
                }
                
                Icon(
                    imageVector = if (isBalanceHidden) MangalaWalletPack.Hide else MangalaWalletPack.Show,
                    contentDescription = "Toggle balance visibility",
                    tint = WalletThemeV2.Colors.secondaryText,
                    modifier = Modifier
                        .size(WalletThemeV2.Dimensions.iconSizeMedium)
                        .clickable { onToggleHideBalance() }
                )
            }
            
            // Primary Balance with Currency Selector
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (isBalanceHidden) "****.**" else numberFormat.format(totalBalance),
                    fontSize = WalletThemeV2.Typography.fontSizeBalance,
                    fontWeight = FontWeight.Bold,
                    color = WalletThemeV2.Colors.primaryText,
                    fontFamily = getInterFontFamily()
                )
                
                Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingXSmall))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "USDT",
                        fontSize = WalletThemeV2.Typography.fontSizeMedium,
                        color = WalletThemeV2.Colors.secondaryText,
                        fontFamily = getInterFontFamily()
                    )
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Change currency",
                        tint = WalletThemeV2.Colors.secondaryText,
                        modifier = Modifier
                            .size(WalletThemeV2.Dimensions.iconSizeSmall)
                            .clickable { /* TODO: Show currency selector */ }
                    )
                }
            }
            
            // USD Equivalent
            Text(
                text = if (isBalanceHidden) "≈ **.***.**" else "≈ $${numberFormat.format(usdEquivalent)}",
                fontSize = WalletThemeV2.Typography.fontSizeLarge,
                color = WalletThemeV2.Colors.secondaryText,
                fontFamily = getInterFontFamily()
            )
            
            // 24h PNL
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "24h: ",
                    fontSize = WalletThemeV2.Typography.fontSizeMedium,
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily()
                )
                
                if (!isBalanceHidden) {
                    Text(
                        text = "${if (pnlAmount >= 0) "+" else ""}${numberFormat.format(pnlAmount)} USDT (${if (pnlPercentage >= 0) "+" else ""}${pnlPercentage.truncateDecimal(2)}%)",
                        fontSize = WalletThemeV2.Typography.fontSizeMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (pnlAmount >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontFamily = getInterFontFamily()
                    )
                } else {
                    Text(
                        text = "+*,*** USDT (+*.**%)",
                        fontSize = WalletThemeV2.Typography.fontSizeMedium,
                        fontWeight = FontWeight.Medium,
                        color = WalletThemeV2.Colors.secondaryText,
                        fontFamily = getInterFontFamily()
                    )
                }
            }
            
        }
    }
}

@Composable
private fun BalanceBreakdownItem(
    label: String,
    amount: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = WalletThemeV2.Typography.fontSizeSmall,
            color = WalletThemeV2.Colors.tertiaryText,
            fontFamily = getInterFontFamily()
        )
        
        Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingXSmall))
        
        Text(
            text = amount,
            fontSize = WalletThemeV2.Typography.fontSizeMedium,
            fontWeight = FontWeight.Medium,
            color = color,
            fontFamily = getInterFontFamily()
        )
    }
}