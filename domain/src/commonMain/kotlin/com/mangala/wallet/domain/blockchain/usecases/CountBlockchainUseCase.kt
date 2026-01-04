package com.mangala.wallet.domain.blockchain.usecases

import com.mangala.wallet.domain.blockchain.repository.BlockchainRepository

class CountBlockchainUseCase(private val blockchainRepository: BlockchainRepository) {
    suspend operator fun invoke(): Long{
        return blockchainRepository.countBlockchain()
    }
}