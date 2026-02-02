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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.features.wallet.presentationv2.evm.EVMAccountInfo
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder

/**
 * Bottom sheet for switching between EVM accounts/wallets
 * Displays wallet addresses in truncated format (0x1234...abcd)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EVMAccountSwitchBottomSheet(
    accounts: List<EVMAccountInfo>,
    activeAccountIndex: Int,
    onAccountSelected: (Int) -> Unit,
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
                .padding(bottom = 32.dp)
        ) {
            // Title
            Text(
                text = "Switch Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WalletThemeV2.Colors.primaryText,
                fontFamily = getInterFontFamily(),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Account List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(accounts) { index, account ->
                    EVMAccountItem(
                        account = account,
                        isSelected = index == activeAccountIndex,
                        onClick = { onAccountSelected(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EVMAccountItem(
    account: EVMAccountInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    WalletThemeV2.Colors.accentBlue.copy(alpha = 0.1f)
                } else {
                    Color.White.copy(alpha = 0.05f)
                }
            )
            .border(
                width = 1.dp,
                color = if (isSelected) {
                    WalletThemeV2.Colors.accentBlue.copy(alpha = 0.3f)
                } else {
                    Color.White.copy(alpha = 0.08f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Account name (e.g., "Account 1")
                Text(
                    text = account.accountName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WalletThemeV2.Colors.primaryText,
                    fontFamily = getInterFontFamily()
                )

                // Truncated address (0x1234...abcd)
                Text(
                    text = account.displayAddress,
                    fontSize = 12.sp,
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily()
                )

                // Balance - uses totalValueFormatted from EVMAccountInfo (already includes currency symbol)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = account.totalValueFormatted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = WalletThemeV2.Colors.primaryText,
                        fontFamily = getInterFontFamily(),
                        modifier = Modifier.mangalaWalletPlaceholder(account.totalValuePlaceholderEnabled)
                    )

                    // PnL percentage - uses formattedPnl from EVMAccountInfo (already formatted)
                    Text(
                        text = account.formattedPnl,
                        fontSize = 11.sp,
                        color = account.pnlColor,
                        fontFamily = getInterFontFamily(),
                        modifier = Modifier.mangalaWalletPlaceholder(account.formattedPnlPlaceholderEnabled)
                    )
                }
            }

            // Checkmark for selected account
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(WalletThemeV2.Colors.accentBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
