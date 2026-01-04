package com.mangala.wallet.domain.datastore.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import com.mangala.wallet.model.blockchain.BlockchainNetworkData

class SaveSelectedNetworkUseCase(private val dataStoreRepository: DataStoreRepository) {
    suspend operator fun invoke(network: BlockchainNetworkData) = dataStoreRepository.saveSelectedNetwork(network)
}