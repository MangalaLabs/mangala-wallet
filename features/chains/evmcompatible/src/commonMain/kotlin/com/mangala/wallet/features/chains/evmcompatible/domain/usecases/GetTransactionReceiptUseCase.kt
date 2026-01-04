package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository

class GetTransactionReceiptUseCase(private val nodeRepository: NodeRepository) {
    suspend operator fun invoke(url: String, id: Int, txHash: String) =
        nodeRepository.getTransactionReceipt(url, id, txHash)
}