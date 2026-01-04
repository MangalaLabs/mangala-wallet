package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Check
import com.mangala.wallet.features.addressbook.presentation.privacy.PrivacyAwareAddressText
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.utils.isNotNullOrBlank

/**
 * Unified address item component that can be used in both address list and bottom sheet
 *
 * @param blockchainSymbol Symbol of the blockchain (e.g., "ETH", "BTC")
 * @param walletAlias Alias of the wallet (e.g., "Hot wallet")
 * @param contactName Name of the contact/owner
 * @param walletAddress Formatted wallet address for display
 * @param fullWalletAddress Full wallet address for copying
 * @param showCard Whether to wrap in a card (true for address list, false for bottom sheet)
 * @param showCheckbox Whether to show selection checkbox
 * @param isSelected Whether the checkbox is selected
 * @param onCheckboxClick Callback when checkbox is clicked
 * @param onCopyClick Callback when copy button is clicked
 * @param onQrCodeClick Callback when QR code button is clicked
 * @param privacyModeEnabled Whether privacy mode is enabled
 * @param isSensitive Whether the address is sensitive
 */
@Composable
fun UnifiedAddressItem(
    blockchainSymbol: String,
    walletAlias: String,
    contactName: String,
    walletAddress: String,
    fullWalletAddress: String,
    modifier: Modifier = Modifier,
    showCard: Boolean = true,
    showCheckbox: Boolean = false,
    isSelected: Boolean = false,
    onCheckboxClick: (() -> Unit)? = null,
    onCopyClick: (() -> Unit)? = null,
    onQrCodeClick: () -> Unit = {},
    privacyModeEnabled: Boolean = false,
    isSensitive: Boolean = false,
) {
    val content = @Composable {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (showCard) {
                        Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    } else {
                        Modifier.padding(vertical = Spacing.TINY)
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.TINY)
        ) {
            // Blockchain icon
            BlockchainIconBox(
                symbol = blockchainSymbol,
                size = 32.dp,
                iconSize = 20.dp,
            )
            // Wallet info column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // First row: Alias and contact name (if enabled)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (walletAlias.isNotNullOrBlank()) {
                        Text(
                            text = walletAlias,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.mangalaColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Text(
                        text = contactName,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = MaterialTheme.mangalaColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }

                // Second row: Address and action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Privacy-aware address text
                    PrivacyAwareAddressText(
                        address = walletAddress,
                        privacyModeEnabled = privacyModeEnabled,
                        isSensitive = isSensitive,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Action buttons row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.XTINY),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Copy button
                        DocumentCopyButton(
                            textToCopy = fullWalletAddress,
                            label = "Wallet Address",
                            onCopyComplete = onCopyClick,
                            iconTint = MaterialTheme.mangalaColors.iconSecondary,
                        )

                        // QR code button
                        ContactQrButton(
                            onShowQrCodeClick = onQrCodeClick
                        )
                    }
                }
            }

            // Checkbox (if needed)
            if (showCheckbox) {
                Spacer(modifier = Modifier.width(16.dp)) // Add clear separation
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.5.dp,
                            color = if (isSelected) MaterialTheme.mangalaColors.iconPrimary else MaterialTheme.mangalaColors.border,
                            shape = CircleShape
                        )
                        .background(
                            color = if (isSelected) MaterialTheme.mangalaColors.iconPrimary else androidx.compose.ui.graphics.Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(
                            enabled = onCheckboxClick != null,
                            onClick = { onCheckboxClick?.invoke() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = MangalaWalletPack.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.mangalaColors.bg,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }

    // Wrap in card if needed
    if (showCard) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            )
        ) {
            content()
        }
    } else {
        Box(modifier = modifier.fillMaxWidth()) {
            content()
        }
    }
}