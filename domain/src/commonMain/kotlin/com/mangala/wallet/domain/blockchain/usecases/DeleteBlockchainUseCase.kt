package com.mangala.wallet.domain.blockchain.usecases

import com.mangala.wallet.domain.blockchain.repository.BlockchainRepository

class DeleteBlockchainUseCase(private val blockchainRepository: BlockchainRepository) {
    suspend operator fun invoke(){
        blockchainRepository.clearDatabase()
    }
}