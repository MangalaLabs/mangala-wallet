package com.mangala.wallet.features.chains.antelope.presentation.resources.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.features.chains.antelope.presentation.resources.model.*
import com.mangala.wallet.utils.DecimalFormat

@Composable
fun ResourcesPanel(
    powerLevel: Int,
    cpuResource: CpuResource,
    netResource: NetResource,
    ramResource: RamResource,
    onCpuRechargeClick: () -> Unit,
    onCpuUpgradeClick: () -> Unit,
    onNetRefreshClick: () -> Unit,
    onNetUpgradeClick: () -> Unit,
    onRamBuyClick: () -> Unit,
    onRamSellClick: () -> Unit,
    onRamMarketClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Panel Header
        PanelHeader(powerLevel = powerLevel)
        
        // Resource Cards
        CpuResourceCard(
            resource = cpuResource,
            onRechargeClick = onCpuRechargeClick,
            onUpgradeClick = onCpuUpgradeClick
        )
        
        NetResourceCard(
            resource = netResource,
            onRefreshClick = onNetRefreshClick,
            onUpgradeClick = onNetUpgradeClick
        )
        
        RamResourceCard(
            resource = ramResource,
            onBuyClick = onRamBuyClick,
            onSellClick = onRamSellClick,
            onMarketClick = onRamMarketClick
        )
    }
}

@Composable
private fun PanelHeader(
    powerLevel: Int
) {
    val numberFormat = remember { DecimalFormat("#,##0") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title with sword emoji
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "⚔️",
                fontSize = 20.sp
            )
            
            Text(
                text = "Battle Resources",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = getInterFontFamily()
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Power Level Badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF8B5CF6).copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = Color(0xFF8B5CF6).copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Power",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = getInterFontFamily()
            )
            
            Text(
                text = numberFormat.format(powerLevel.toDouble()),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5CF6),
                fontFamily = getInterFontFamily()
            )
        }
    }
}