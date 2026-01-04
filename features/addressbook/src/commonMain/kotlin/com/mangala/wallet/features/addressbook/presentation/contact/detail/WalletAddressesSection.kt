package com.mangala.wallet.features.addressbook.presentation.contact.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithBlockchainModel
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.Qrcode
import com.mangala.wallet.features.addressbook.presentation.components.BlockchainIconBox
import com.mangala.wallet.features.addressbook.presentation.components.DocumentCopyButton
import com.mangala.wallet.features.addressbook.presentation.privacy.PrivacyAwareAddressText
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun WalletAddressesSection(
    walletAddresses: List<WalletAddressWithBlockchainModel>,
    onShowQrCodeClick: (WalletAddressWithBlockchainModel) -> Unit,
    onAddAddressClick: () -> Unit = {},
    isViewOnly: Boolean = false,
    onCopyComplete: () -> Unit = {},
    privacyModeEnabled: Boolean = false,
    privacyDisplayMode: DisplayMode
) {
    if (walletAddresses.isEmpty()) return

    // Group wallet addresses by blockchain type
    val groupedAddresses = walletAddresses.groupBy { it.blockchainType.name }

    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Address",
            style = MangalaTypography.Size14SemiBold(),
            color = MaterialTheme.mangalaColors.textPrimary
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.mangalaColors.bgInnerCard,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Display each blockchain group
                groupedAddresses.entries.forEachIndexed { index, entry ->
                    val (blockchainName, addresses) = entry

                    // Blockchain name as header
                    Text(
                        text = blockchainName,
                        style = MangalaTypography.Size14Regular(),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Addresses in this blockchain group
                    addresses.forEach { addressWithBlockchain ->
                        BlockchainAddressItem(
                            addressWithBlockchain = addressWithBlockchain,
                            onShowQrCodeClick = onShowQrCodeClick,
                            onCopyComplete = onCopyComplete,
                            privacyModeEnabled = privacyModeEnabled,
                            privacyDisplayMode = privacyDisplayMode
                        )
                    }
                    // Only show divider if it's not the last item
                    if (index < groupedAddresses.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            color = MaterialTheme.mangalaColors.border,
                            thickness = 0.5.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (!isViewOnly) {
                    OutlinedButton(
                        onClick = onAddAddressClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.mangalaColors.textLink
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.mangalaColors.textLink,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Add Another Address",
                            color = MaterialTheme.mangalaColors.textLink,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BlockchainAddressItem(
    addressWithBlockchain: WalletAddressWithBlockchainModel,
    onShowQrCodeClick: (WalletAddressWithBlockchainModel) -> Unit,
    onCopyComplete: () -> Unit = {},
    privacyModeEnabled: Boolean = false,
    privacyDisplayMode: DisplayMode
) {
    val walletAddress = addressWithBlockchain.walletAddress
    val blockchain = addressWithBlockchain.blockchainType

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
//            .padding(vertical = 4.dp)
    ) {
        BlockchainIconBox(
            symbol = blockchain.symbol,
            iconPath = blockchain.icon,
            size = 16.dp,
            iconSize = 12.dp
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Address in shortened form with "Default" badge if it's primary
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Use PrivacyAwareAddressText for consistent privacy behavior
            PrivacyAwareAddressText(
                address = walletAddress.address,
                privacyModeEnabled = privacyModeEnabled,
                isSensitive = walletAddress.isSensitive,
                style = MangalaTypography.Size14Medium().copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.mangalaColors.textPrimary,
                maxLines = 1,
                privacyDisplayMode = privacyDisplayMode
            )
        }


        // Add "Default" label with oval shape for primary addresses
        if (walletAddress.isPrimary) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.mangalaColors.border,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .background(MaterialTheme.mangalaColors.bgInnerCard)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Default",
                    style = MangalaTypography.Size12Medium(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                )
            }
        }

        // Action buttons - chỉ hiển thị 2 nút như trong thiết kế Figma
        DocumentCopyButton(
            textToCopy = walletAddress.address,
            label = "Wallet Address",
            onCopyComplete = onCopyComplete,
            iconSize = 16.dp,
            iconTint = MaterialTheme.mangalaColors.iconSecondary,
            buttonSize = 28.dp,
        )

        IconButton(
            onClick = { onShowQrCodeClick(addressWithBlockchain) },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = ContactIcon.Qrcode,
                contentDescription = "Show QR Code",
                tint = MaterialTheme.mangalaColors.iconSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}