package com.mangala.wallet.features.addressbook.domain.mapper

import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.utils.imageResourceToPath
import com.mangala.wallet.features.addressbook.utils.BlockchainColorMapper
import com.mangala.wallet.model.blockchain.BlockchainNetworkData

/**
 * Mapper to convert BlockchainNetworkData (from hardcoded data) to BlockchainTypeEntity (used in UI)
 */
class BlockchainNetworkMapper {
    
    fun mapToBlockchainTypeEntity(network: BlockchainNetworkData): BlockchainTypeEntity {
        return BlockchainTypeEntity(
            id = network.blockChainUid,
            symbol = network.blockchainType.uid,
            name = network.name,
            icon = imageResourceToPath(network.localImage),
            networkType = if (network.isTestNet) "testnet" else "mainnet",
            color = BlockchainColorMapper.getColorForBlockchain(network.blockchainType)
        )
    }
    
    fun mapToBlockchainTypeEntities(networks: List<BlockchainNetworkData>): List<BlockchainTypeEntity> {
        return networks.map { mapToBlockchainTypeEntity(it) }
    }
}