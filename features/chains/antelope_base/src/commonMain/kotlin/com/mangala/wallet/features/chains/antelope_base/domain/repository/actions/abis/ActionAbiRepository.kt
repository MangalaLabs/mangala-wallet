package com.mangala.wallet.features.chains.antelope_base.domain.repository.actions.abis

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface ActionAbiRepository {

    suspend fun getActionsAbiFlow(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<AntelopeActionAbi>?>>

    suspend fun getActionsAbi(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<List<AntelopeActionAbi>>

    suspend fun getActionAbiByContractAndActionName(
        accountName: String,
        actionName: String,
        forceRefresh: Boolean,
        blockchainType: BlockchainType
    ): Result<List<AntelopeActionAbi>>
}