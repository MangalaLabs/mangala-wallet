package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository

class GetLatestBlockUseCase(private val nodeRepository: NodeRepository) {
    suspend operator fun invoke(url: String, id: Int) = nodeRepository.getLatestBlock(url, id)
}