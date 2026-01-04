package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository

class SendRawTransactionUseCase(private val nodeRepository: NodeRepository) {
    suspend operator fun invoke(url: String, id: Int, signedTransaction: ByteArray) =
        nodeRepository.sendRawTransaction(url, id, signedTransaction)
}