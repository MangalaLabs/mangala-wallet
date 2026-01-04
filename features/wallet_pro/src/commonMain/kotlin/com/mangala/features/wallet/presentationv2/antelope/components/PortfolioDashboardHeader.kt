package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Show
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Hide
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.truncateDecimal

@Composable
fun PortfolioDashboardHeader(
    totalPortfolioBalance: Double,
    totalPortfolioUsd: Double,
    totalAccounts: Int,
    activeAccountName: String,
    totalPnlAmount: Double,
    totalPnlPercentage: Double,
    isExpanded: Boolean,
    isBalanceHidden: Boolean,
    onToggleExpand: () -> Unit,
    onToggleHideBalance: () -> Unit,
    onCurrencyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { DecimalFormat("#,##0.00") }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.6f),
                        Color.White.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .animateContentSize()
    ) {
        // Ambient light effect layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 400f,
                        center = Offset(0.3f, 0.2f)
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Portfolio Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Portfolio Label
                Text(
                    text = "TOTAL PORTFOLIO",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = getInterFontFamily()
                )
                
                // Hide/Show Balance Icon - iOS style
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(alpha = 0.08f)
                        )
                        .clickable { onToggleHideBalance() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBalanceHidden) MangalaWalletPack.Hide else MangalaWalletPack.Show,
                        contentDescription = "Toggle balance visibility",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Total Balance with Currency Selector
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (isBalanceHidden) "••••••" else {
                        val compactFormat = DecimalFormat("#,##0.#")
                        when {
                            totalPortfolioBalance >= 1000000 -> "${compactFormat.format(totalPortfolioBalance / 1000000)}M"
                            totalPortfolioBalance >= 1000 -> "${compactFormat.format(totalPortfolioBalance / 1000)}K"
                            else -> numberFormat.format(totalPortfolioBalance)
                        }
                    },
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = getInterFontFamily(),
                    letterSpacing = (-0.5).sp
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .clickable { onCurrencyClick() }
                ) {
                    Text(
                        text = "USDT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.6f),
                        fontFamily = getInterFontFamily()
                    )
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Change currency",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
                
//                Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingXSmall))
//
//                // USD Equivalent
//                Text(
//                    text = if (isBalanceHidden) "≈ $**,***.**" else "≈ $${numberFormat.format(totalPortfolioUsd)}",
//                    fontSize = WalletThemeV2.Typography.fontSizeBody,
//                    color = WalletThemeV2.Colors.secondaryText.copy(alpha = 0.8f),
//                    fontFamily = getInterFontFamily()
//                )
                
                Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))
                
                // 24h PNL
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "24h: ",
                        fontSize = WalletThemeV2.Typography.fontSizeBody,
                        color = WalletThemeV2.Colors.secondaryText,
                        fontFamily = getInterFontFamily()
                    )
                    
                    if (!isBalanceHidden) {
                        Text(
                            text = "${if (totalPnlAmount >= 0) "+" else ""}${numberFormat.format(totalPnlAmount)} USDT (${if (totalPnlPercentage >= 0) "+" else ""}${totalPnlPercentage.truncateDecimal(2)}%)",
                            fontSize = WalletThemeV2.Typography.fontSizeBody,
                            fontWeight = FontWeight.Medium,
                            color = if (totalPnlAmount >= 0) WalletThemeV2.Colors.positiveGain else WalletThemeV2.Colors.negativeLoss,
                            fontFamily = getInterFontFamily()
                        )
                    } else {
                        Text(
                            text = "+*,*** USDT (+*.**%)",
                            fontSize = WalletThemeV2.Typography.fontSizeBody,
                            fontWeight = FontWeight.Medium,
                            color = WalletThemeV2.Colors.secondaryText,
                            fontFamily = getInterFontFamily()
                        )
                    }
                }
                
                // Active Account Display with Expand Icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = WalletThemeV2.Dimensions.spacingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active: ",
                            fontSize = WalletThemeV2.Typography.fontSizeBody,
                            color = WalletThemeV2.Colors.secondaryText,
                            fontFamily = getInterFontFamily()
                        )
                        Text(
                            text = activeAccountName,
                            fontSize = WalletThemeV2.Typography.fontSizeBody,
                            color = WalletThemeV2.Colors.primaryText,
                            fontWeight = FontWeight.Medium,
                            fontFamily = getInterFontFamily()
                        )
                        
                        Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingSmall))
                        
                        Icon(
                            imageVector = MangalaWalletPack.Copy,
                            contentDescription = "Copy account name",
                            tint = WalletThemeV2.Colors.secondaryText,
                            modifier = Modifier
                                .size(WalletThemeV2.Dimensions.iconSizeSmall)
                                .clickable { /* TODO: Copy account name */ }
                        )
                    }
                    
                    // Expand/Collapse Icon
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = WalletThemeV2.Colors.secondaryText,
                        modifier = Modifier
                            .size(WalletThemeV2.Dimensions.iconSizeMedium)
                            .clickable { onToggleExpand() }
                    )
                }
            }
        }
    }
