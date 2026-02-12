package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.runtime.Composable
import com.mangala.features.wallet.presentationv2.core.common.components.NetworkSelectionBottomSheet
import com.mangala.wallet.model.blockchain.BlockchainNetworkData

/**
 * Delegates to shared [NetworkSelectionBottomSheet].
 * Kept for backward compatibility with existing Antelope call sites.
 */
@Composable
fun AntelopeNetworkSelectionBottomSheet(
    availableNetworks: List<BlockchainNetworkData>,
    selectedNetwork: BlockchainNetworkData?,
    onNetworkSelected: (BlockchainNetworkData) -> Unit,
    onDismiss: () -> Unit
) {
    NetworkSelectionBottomSheet(
        availableNetworks = availableNetworks,
        selectedNetwork = selectedNetwork,
        onNetworkSelected = onNetworkSelected,
        onDismiss = onDismiss
    )
}
