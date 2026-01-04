package com.mangala.wallet.features.settings.network

import com.mangala.wallet.model.blockchain.BlockchainNetworkData

data class NetworkScreenModelUiModel(
    val query: String = "",
    val chainIdSelected: Long = 1,
    val items: List<NetworkScreenModelItemUiModel> = emptyList()
) {
    val filteredItems = items.filter { it.network.name.contains(query, ignoreCase = true) }
}

data class NetworkScreenModelItemUiModel(
    val network: BlockchainNetworkData,
    val isSelected: Boolean = false
)