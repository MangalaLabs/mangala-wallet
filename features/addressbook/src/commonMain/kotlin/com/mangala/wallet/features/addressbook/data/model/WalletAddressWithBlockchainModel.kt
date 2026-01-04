package com.mangala.wallet.features.addressbook.data.model

import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity

/**
 * Model kết hợp WalletAddress với thông tin BlockchainType tương ứng
 */
data class WalletAddressWithBlockchainModel(
    val walletAddress: WalletAddressEntity,
    val blockchainType: BlockchainTypeEntity
)