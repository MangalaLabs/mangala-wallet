package com.mangala.wallet.features.chains.bitcoin.domain.utils

import com.mangala.wallet.model.blockchain.BlockchainType
import fr.acinq.bitcoin.Block
import fr.acinq.bitcoin.BlockHash

fun getChainHash(blockchainType: BlockchainType): BlockHash {
    return when (blockchainType) {
        BlockchainType.Bitcoin -> Block.LivenetGenesisBlock.hash
        BlockchainType.BitcoinTestnet4 -> Block.Testnet4GenesisBlock.hash
        else -> throw IllegalArgumentException("Unsupported blockchain type: $blockchainType")
    }
}