package com.mangala.wallet.model.blockchain

import kotlinx.serialization.Serializable

@Serializable
data class BlockchainEntity(
    val uid: String,
    val name: String,
    val eip3091url: String?
) {

    val blockchain: Blockchain
        get() = Blockchain(BlockchainType.fromUid(uid), name, eip3091url)

}