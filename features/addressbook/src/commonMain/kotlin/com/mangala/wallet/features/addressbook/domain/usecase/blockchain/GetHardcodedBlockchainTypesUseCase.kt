package com.mangala.wallet.features.addressbook.domain.usecase.blockchain

import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.domain.mapper.BlockchainNetworkMapper
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.utils.BuildEnvironmentProvider

/**
 * Use case to get blockchain types from hardcoded BlockchainNetworkData
 * This replaces the database-based getAllBlockchainTypesUseCase for consistency with Send flow
 */
class GetHardcodedBlockchainTypesUseCase(
    private val buildEnvironmentProvider: BuildEnvironmentProvider,
    private val mapper: BlockchainNetworkMapper
) {
    operator fun invoke(): List<BlockchainTypeEntity> {
        val networks = BlockchainNetworkData.getAllBlockchainNetworkSupported(
            buildEnvironmentProvider.isDevelopmentEnvironment()
        )
        return mapper.mapToBlockchainTypeEntities(networks).sortedBy { it.name }
    }
}