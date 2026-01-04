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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.features.chains.antelope.presentation.resources.model.CpuResource
import com.mangala.wallet.features.chains.antelope.presentation.resources.model.ResourceRarity
import com.mangala.wallet.features.chains.antelope.presentation.resources.model.StatusEffectType
import com.mangala.wallet.utils.DecimalFormat

@Composable
fun CpuResourceCard(
    resource: CpuResource,
    onRechargeClick: () -> Unit,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { DecimalFormat("#,##0.0") }
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
                    emoji = "⚡",
                    gradientColors = listOf(
                        Color(0xFF3B82F6),
                        Color(0xFF1D4ED8)
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
                            label = "Power",
                            value = "${numberFormat.format(resource.current.toDouble() / 1000)}k ${resource.unit}",
                            color = Color(0xFF3B82F6)
                        )
                        
                        StatItem(
                            label = "Max",
                            value = "${numberFormat.format(resource.max.toDouble() / 1000)}k ${resource.unit}",
                            color = Color(0xFF3B82F6)
                        )
                        
                        StatItem(
                            label = "Regen",
                            value = resource.regenTime,
                            color = Color(0xFF3B82F6)
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
                    Color(0xFF3B82F6),
                    Color(0xFF60A5FA)
                )
            )
            
            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceActionButton(
                    text = "⚡ Recharge",
                    onClick = onRechargeClick,
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