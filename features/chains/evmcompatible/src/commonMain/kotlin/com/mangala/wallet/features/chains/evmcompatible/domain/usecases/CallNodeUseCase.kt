package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter

class CallNodeUseCase(private val nodeRepository: NodeRepository) {
    suspend operator fun invoke(url: String, id: Int, contractAddress: Address, data: ByteArray, defaultBlockParameter: DefaultBlockParameter) =
        nodeRepository.call(url, id, contractAddress, data, defaultBlockParameter)

}