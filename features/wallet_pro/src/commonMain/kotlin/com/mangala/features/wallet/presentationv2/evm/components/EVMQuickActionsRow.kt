package com.mangala.features.wallet.presentationv2.evm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Card
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ReceiveButton
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Scan
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SendButton
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.utils.isQrCodeScanningSupported
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun EVMQuickActionsRow(
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        EVMQuickActionItem(
            icon = MangalaWalletPack.SendButton,
            label = stringResource(MR.strings.all_send),
            onClick = onSendClick
        )

        EVMQuickActionItem(
            icon = MangalaWalletPack.ReceiveButton,
            label = stringResource(MR.strings.all_receive),
            onClick = onReceiveClick
        )

        EVMQuickActionItem(
            icon = MangalaWalletPack.Card,
            label = stringResource(MR.strings.label_history),
            onClick = onHistoryClick
        )

        if (isQrCodeScanningSupported()) {
            EVMQuickActionItem(
                icon = MangalaWalletPack.Scan,
                label = stringResource(MR.strings.label_scan),
                onClick = onScanClick
            )
        }
    }
}

@Composable
private fun EVMQuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentAlpha = 0.9f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = WalletThemeV2.Colors.primaryText.copy(alpha = contentAlpha),
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = WalletThemeV2.Colors.secondaryText,
            fontFamily = getInterFontFamily(),
            letterSpacing = 0.sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
