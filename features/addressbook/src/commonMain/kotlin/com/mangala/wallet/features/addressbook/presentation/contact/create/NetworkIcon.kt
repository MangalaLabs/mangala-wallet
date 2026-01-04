package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.DropDown
import com.mangala.wallet.features.addressbook.presentation.components.BlockchainIconBox
import com.mangala.wallet.ui.theme.mangalaColors

// Danh sách blockchain networks phổ biến để chọn - fallback for compatibility
val supportedNetworks = listOf("ETH", "BTC", "BNB", "SOL", "AVAX", "MATIC", "EOS")

/**
 * Hiển thị icon của network và mở bottom sheet để chọn network khi được nhấp vào
 * Thay thế dropdown menu bằng bottom sheet theo thiết kế Figma
 */
@Composable
fun NetworkIcon(
    selectedBlockchain: BlockchainTypeEntity?,
    modifier: Modifier = Modifier,
    availableBlockchains: List<BlockchainTypeEntity> = emptyList(),
    onBlockchainSelected: (BlockchainTypeEntity) -> Unit = {}
) {
    // Biến state để kiểm soát hiển thị bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }

    // Network icon with dropdown arrow
    Row(
        modifier = Modifier
            .clickable {
                // Hiển thị bottom sheet khi click vào icon
                showBottomSheet = true
            }
            .padding(end = 8.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        BlockchainIconBox(
            symbol = selectedBlockchain?.symbol ?: "ETH",
            size = 32.dp,
            iconSize = 32.dp,
            iconPath = selectedBlockchain?.icon // Use database icon path
        )

        // Figma sync: Arrow down when closed, up when open
        Icon(
            imageVector = ContactIcon.DropDown,
            contentDescription = "Select network",
            tint = MaterialTheme.mangalaColors.textSecondary,
            modifier = Modifier.size(20.dp)
        )

    }

    // Hiển thị bottom sheet nếu được bật
    if (showBottomSheet) {
        NetworkSelectionBottomSheet(
            blockchainEntities = availableBlockchains,
            onBlockchainSelected = { selectedBlockchain ->
                onBlockchainSelected(selectedBlockchain)
                showBottomSheet = false
            },
            onDismiss = {
                showBottomSheet = false
            }
        )
    }
}