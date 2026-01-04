package com.mangala.wallet.features.chains.antelope.presentation.resources.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.features.chains.antelope.presentation.resources.model.RamResource
import com.mangala.wallet.utils.DecimalFormat

@Composable
fun RamResourceCard(
    resource: RamResource,
    onBuyClick: () -> Unit,
    onSellClick: () -> Unit,
    onMarketClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    emoji = "💎",
                    gradientColors = listOf(
                        Color(0xFFFBBF24),
                        Color(0xFFF59E0B)
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
                            label = "Owned",
                            value = "${resource.current} ${resource.unit}",
                            color = Color(0xFFFBBF24)
                        )
                        
                        StatItem(
                            label = "Price",
                            value = "₹${resource.price}/KB",
                            color = Color(0xFFFBBF24)
                        )
                        
                        StatItem(
                            label = "24h",
                            value = if (resource.priceChange > 0) "↑${resource.priceChange}%" else "↓${resource.priceChange}%",
                            color = if (resource.priceChange > 0) Color(0xFF10B981) else Color(0xFFEF4444)
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
                    Color(0xFFFBBF24),
                    Color(0xFFFCD34D)
                )
            )
            
            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceActionButton(
                    text = "💰 Buy",
                    onClick = onBuyClick,
                    modifier = Modifier.weight(1f)
                )
                
                ResourceActionButton(
                    text = "💸 Sell",
                    onClick = onSellClick,
                    modifier = Modifier.weight(1f)
                )
                
                ResourceActionButton(
                    text = "📊 Market",
                    isPrimary = true,
                    onClick = onMarketClick,
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