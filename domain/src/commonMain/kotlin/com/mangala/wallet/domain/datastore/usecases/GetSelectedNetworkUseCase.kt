package com.mangala.wallet.domain.datastore.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import kotlinx.coroutines.flow.Flow

class GetSelectedNetworkUseCase(private val dataStoreRepository: DataStoreRepository) {
    fun invokeFlow(): Flow<BlockchainNetworkData> = dataStoreRepository.getSelectedNetworkFlow()

    suspend operator fun invoke(): BlockchainNetworkData = dataStoreRepository.getSelectedNetwork()
}