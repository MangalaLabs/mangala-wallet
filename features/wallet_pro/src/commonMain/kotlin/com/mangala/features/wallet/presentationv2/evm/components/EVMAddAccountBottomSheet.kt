package com.mangala.features.wallet.presentationv2.evm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowCircleDown
import com.mangala.wallet.ui.WalletThemeV2

/**
 * Bottom sheet for adding a new EVM account
 * Options: Create new account or Import existing wallet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EVMAddAccountBottomSheet(
    onCreateNewAccount: () -> Unit,
    onImportWallet: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WalletThemeV2.Colors.cardBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(WalletThemeV2.Colors.tertiaryText.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title
            Text(
                text = "Add Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WalletThemeV2.Colors.primaryText,
                fontFamily = getInterFontFamily(),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Create new account option
            AddAccountOption(
                icon = Icons.Default.Add,
                iconBackgroundColor = WalletThemeV2.Colors.accentBlue.copy(alpha = 0.15f),
                iconTint = WalletThemeV2.Colors.accentBlue,
                title = "Create New Account",
                description = "Generate a new HD wallet account",
                onClick = {
                    onDismiss()
                    onCreateNewAccount()
                }
            )

            // Import wallet option
            AddAccountOption(
                icon = MangalaWalletPack.ArrowCircleDown,
                iconBackgroundColor = WalletThemeV2.Colors.accentPurple.copy(alpha = 0.15f),
                iconTint = WalletThemeV2.Colors.accentPurple,
                title = "Import Wallet",
                description = "Import using seed phrase or private key",
                onClick = {
                    onDismiss()
                    onImportWallet()
                }
            )
        }
    }
}

@Composable
private fun AddAccountOption(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WalletThemeV2.Colors.primaryText,
                    fontFamily = getInterFontFamily()
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily()
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = WalletThemeV2.Colors.tertiaryText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
