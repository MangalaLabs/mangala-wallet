package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Send
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Scan
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TransactionHistory
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Cpu
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.aspectRatio
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Card
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HomeReceive
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HomeSend
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ReceiveButton
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SendButton
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SendNewContact
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TxReceive
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.utils.isQrCodeScanningSupported
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun AntelopeQuickActionsRow(
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onMyQRClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionItem(
            icon = MangalaWalletPack.SendButton,
            label = "Send",
            onClick = onSendClick,
            priority = 2 // Highest priority
        )
        
        QuickActionItem(
            icon = MangalaWalletPack.ReceiveButton,
            label = "Receive",
            onClick = onReceiveClick,
            priority = 2 // Second priority
        )
        
        QuickActionItem(
            icon = MangalaWalletPack.Card,
            label = "History",
            onClick = onHistoryClick,
            priority = 2 // Third priority
        )

        if (isQrCodeScanningSupported()) {
            QuickActionItem(
                icon = MangalaWalletPack.Scan,
                label = "Scan",
                onClick = onMyQRClick,
                priority = 2 // Lowest priority
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    priority: Int, // 1 = highest, 4 = lowest priority
    modifier: Modifier = Modifier
) {
    // Onboarding style uses consistent styling for all buttons
    val contentAlpha = 0.9f
    val fontWeight = FontWeight.Medium
    
    // Onboarding style button
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Same background as portfolio
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(WalletThemeV2.Colors.cardBackground)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            // Icon centered in square
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = WalletThemeV2.Colors.primaryText.copy(alpha = contentAlpha),
                modifier = Modifier.size(28.dp)
            )
        }
        
        // Text label outside button
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = fontWeight,
            color = WalletThemeV2.Colors.secondaryText,
            fontFamily = getInterFontFamily(),
            letterSpacing = 0.sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}