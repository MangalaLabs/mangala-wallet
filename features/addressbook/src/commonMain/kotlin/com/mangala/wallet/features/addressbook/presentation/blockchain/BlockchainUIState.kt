package com.mangala.wallet.features.addressbook.presentation.blockchain

import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.TokenInformationEntity

data class BlockchainUIState(
    val isLoading: Boolean = false,
    val blockchainTypes: List<BlockchainTypeEntity> = emptyList(),
    val selectedBlockchainType: BlockchainTypeEntity? = null,
    val tokensForSelectedBlockchain: List<TokenInformationEntity> = emptyList(),
    val walletAddress: WalletAddressInput = WalletAddressInput(),
    val isAddressValid: Boolean? = null,
    val validationError: String? = null,
    val error: String? = null
)

data class WalletAddressInput(
    val address: String = "",
    val blockchainTypeId: String = "",
    val alias: String = "",
    val isPrimary: Boolean = false
)