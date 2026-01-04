package com.mangala.wallet.features.chains.antelope.presentation.resources.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.utils.DecimalFormat

@Composable
fun ResourceHeader(
    playerName: String,
    walletAddress: String,
    eosBalance: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xF20F0F19),
                        Color(0xCC0F0F19)
                    )
                )
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with rotating border
        PlayerAvatar()
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Player Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playerName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontFamily = getInterFontFamily()
            )
            
            Text(
                text = walletAddress,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = getInterFontFamily()
            )
        }
        
        // Currency Display
        CurrencyDisplay(
            balance = eosBalance
        )
    }
}

@Composable
private fun PlayerAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "avatar_rotation"
    )
    
    Box(
        contentAlignment = Alignment.Center
    ) {
        // Rotating gradient border
        Box(
            modifier = Modifier
                .size(60.dp)
                .rotate(rotation)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2),
                            Color(0xFFF093FB),
                            Color(0xFF667EEA)
                        )
                    )
                )
        )
        
        // Avatar content
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚔️",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun CurrencyDisplay(
    balance: Double
) {
    val numberFormat = remember { DecimalFormat("#,##0") }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Gold coin icon
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700),
                            Color(0xFFFFED4E)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "💰",
                fontSize = 14.sp
            )
        }
        
        // Balance
        Text(
            text = numberFormat.format(balance),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700),
            fontFamily = getInterFontFamily()
        )
    }
}