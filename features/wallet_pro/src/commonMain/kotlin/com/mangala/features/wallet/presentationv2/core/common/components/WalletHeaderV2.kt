package com.mangala.features.wallet.presentationv2.core.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Plus
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Notification
import com.mangala.wallet.ui.imageloader.RemoteImage
import androidx.compose.ui.layout.ContentScale
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Category
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Dropdown
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.imageloader.LocalImage

@Composable
fun WalletHeaderV2(
    selectedNetwork: BlockchainNetworkData?,
    isConnected: Boolean? = null,
    notificationCount: Int = 0,
    isDevelopmentEnvironment: Boolean = false,
    onNotificationClick: () -> Unit,
    onAddAccountClick: () -> Unit,
    onToggleAccountMode: () -> Unit = {},
    onNetworkDropdownClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(WalletThemeV2.Dimensions.headerHeight + 16.dp) // Extra height to prevent cutoff
            .padding(
                horizontal = WalletThemeV2.Dimensions.paddingMedium,
                vertical = WalletThemeV2.Dimensions.spacingSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onNetworkDropdownClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedNetwork != null) {
                LocalImage(
                    imageResource = selectedNetwork.localImage,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingMedium))
            
            Text(
                text = selectedNetwork?.name.orEmpty(),
                fontSize = WalletThemeV2.Typography.fontSizeMedium,
                fontWeight = FontWeight.SemiBold,
                color = WalletThemeV2.Colors.primaryText,
                fontFamily = getInterFontFamily(),
                modifier = Modifier
                    .weight(1f, fill = false)
                    .basicMarquee(iterations = Int.MAX_VALUE),
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingSmall))
            
            Icon(
                imageVector = MangalaWalletPack.Dropdown,
                contentDescription = "Network Dropdown",
                tint = WalletThemeV2.Colors.primaryText,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingSmall))
            
            isConnected?.let {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isConnected) WalletThemeV2.Colors.success
                            else WalletThemeV2.Colors.error
                        )
                )
            }
        }
        
        // Icon buttons row
        Row(
            horizontalArrangement = Arrangement.spacedBy(WalletThemeV2.Dimensions.spacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            // Temporary toggle account mode button
//            Box(
//                modifier = Modifier
//                    .size(36.dp)
//                    .clip(CircleShape)
//                    .background(WalletThemeV2.Colors.secondaryBackground.copy(alpha = 0.5f))
//                    .clickable { onToggleAccountMode() },
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = MangalaWalletPack.Group,
//                    contentDescription = "Toggle Account Mode",
//                    tint = WalletThemeV2.Colors.primaryText,
//                    modifier = Modifier.size(WalletThemeV2.Dimensions.iconSizeMedium)
//                )
//            }
            
            // Add account button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(WalletThemeV2.Colors.secondaryBackground.copy(alpha = 0.5f))
                    .clickable { onAddAccountClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MangalaWalletPack.Plus,
                    contentDescription = "Add Account",
                    tint = WalletThemeV2.Colors.primaryText,
                    modifier = Modifier.size(WalletThemeV2.Dimensions.iconSizeMedium)
                )
            }
            
            if (isDevelopmentEnvironment) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WalletThemeV2.Colors.secondaryBackground.copy(alpha = 0.5f))
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.Notification,
                        contentDescription = "Notifications",
                        tint = WalletThemeV2.Colors.primaryText,
                        modifier = Modifier.size(WalletThemeV2.Dimensions.iconSizeMedium)
                    )
                }
            }
        }
    }
}