package com.mangala.wallet.features.chains.antelope.presentation.resources.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.features.chains.antelope.presentation.resources.model.NetResource
import com.mangala.wallet.utils.DecimalFormat

@Composable
fun NetResourceCard(
    resource: NetResource,
    onRefreshClick: () -> Unit,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { DecimalFormat("#,##0") }
    ResourceCardBase(
        rarity = resource.rarity,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Section
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon
                ResourceIcon(
                    emoji = "🌐",
                    gradientColors = listOf(
                        Color(0xFF10B981),
                        Color(0xFF059669)
                    )
                )
                
                // Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = resource.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontFamily = getInterFontFamily()
                    )
                    
                    Text(
                        text = resource.type,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        fontFamily = getInterFontFamily()
                    )
                    
                    // Stats
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatItem(
                            label = "Usage",
                            value = "${resource.current} ${resource.unit}",
                            color = Color(0xFF10B981)
                        )
                        
                        StatItem(
                            label = "Limit",
                            value = "${numberFormat.format(resource.max.toDouble() / 1000)} MB",
                            color = Color(0xFF10B981)
                        )
                        
                        StatItem(
                            label = "Regen",
                            value = resource.regenTime,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
            
            // Progress Bar
            ResourceProgressBar(
                current = resource.current,
                max = resource.max,
                percentage = resource.percentage,
                gradientColors = listOf(
                    Color(0xFF10B981),
                    Color(0xFF34D399)
                )
            )
            
            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceActionButton(
                    text = "🌐 Refresh",
                    onClick = onRefreshClick,
                    modifier = Modifier.weight(1f)
                )
                
                ResourceActionButton(
                    text = "⬆️ Upgrade",
                    isPrimary = true,
                    onClick = onUpgradeClick,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Status Effects
            StatusEffects(effects = resource.statusEffects)
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.5f),
            letterSpacing = 0.5.sp,
            fontFamily = getInterFontFamily()
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            fontFamily = getInterFontFamily()
        )
    }
}