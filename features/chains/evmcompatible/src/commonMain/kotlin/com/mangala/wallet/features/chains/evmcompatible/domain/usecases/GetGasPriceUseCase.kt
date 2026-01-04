package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository

class GetGasPriceUseCase(private val nodeRepository: NodeRepository) {
    suspend operator fun invoke(url: String, id: Int) =
        nodeRepository.getGasPrice(url, id)
}