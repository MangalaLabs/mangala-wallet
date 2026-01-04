package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRight
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.WalletThemeV2
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.mangala.wallet.utils.DecimalFormat

@Composable
fun AntelopeREXStakingCard(
    rexBalance: Double,
    maturityDate: Instant?,
    estimatedApr: Double,
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { DecimalFormat("#,##0.00") }
    val percentFormat = remember { DecimalFormat("#.##%") }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(WalletThemeV2.Dimensions.cornerRadiusLarge))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        WalletThemeV2.Colors.purple.copy(alpha = 0.2f),
                        WalletThemeV2.Colors.purple.copy(alpha = 0.1f)
                    )
                )
            )
            .clickable { onManageClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WalletThemeV2.Dimensions.paddingLarge)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏦",
                        fontSize = WalletThemeV2.Typography.fontSizeLarge
                    )
                    
                    Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingSmall))
                    
                    Column {
                        Text(
                            text = "REX Staking",
                            fontSize = WalletThemeV2.Typography.fontSizeMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = WalletThemeV2.Colors.primaryText,
                            fontFamily = getInterFontFamily()
                        )
                        
                        Text(
                            text = "${numberFormat.format(rexBalance)} REX",
                            fontSize = WalletThemeV2.Typography.fontSizeLarge,
                            fontWeight = FontWeight.Bold,
                            color = WalletThemeV2.Colors.purple,
                            fontFamily = getInterFontFamily()
                        )
                    }
                }
                
                Icon(
                    imageVector = MangalaWalletPack.ArrowRight,
                    contentDescription = "Manage",
                    tint = WalletThemeV2.Colors.purple,
                    modifier = Modifier.size(WalletThemeV2.Dimensions.iconSizeMedium)
                )
            }
            
            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // APR
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.ArrowUp,
                        contentDescription = "APR",
                        tint = WalletThemeV2.Colors.success,
                        modifier = Modifier.size(WalletThemeV2.Dimensions.iconSizeSmall)
                    )
                    
                    Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingXSmall))
                    
                    Text(
                        text = "Est. APR",
                        fontSize = WalletThemeV2.Typography.fontSizeBody,
                        color = WalletThemeV2.Colors.secondaryText,
                        fontFamily = getInterFontFamily()
                    )
                    
                    Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingSmall))
                    
                    Text(
                        text = percentFormat.format(estimatedApr / 100),
                        fontSize = WalletThemeV2.Typography.fontSizeBody,
                        fontWeight = FontWeight.Medium,
                        color = WalletThemeV2.Colors.success,
                        fontFamily = getInterFontFamily()
                    )
                }
                
                // Maturity
                if (maturityDate != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.ArrowRight,  // TODO: Replace with clock icon
                            contentDescription = "Maturity",
                            tint = WalletThemeV2.Colors.secondaryText,
                            modifier = Modifier.size(WalletThemeV2.Dimensions.iconSizeSmall)
                        )
                        
                        Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingXSmall))
                        
                        val localDateTime = maturityDate.toLocalDateTime(TimeZone.currentSystemDefault())
                        Text(
                            text = "Matures ${localDateTime.month.name.lowercase().capitalize()} ${localDateTime.dayOfMonth}",
                            fontSize = WalletThemeV2.Typography.fontSizeBody,
                            color = WalletThemeV2.Colors.secondaryText,
                            fontFamily = getInterFontFamily()
                        )
                    }
                }
            }
        }
    }
}