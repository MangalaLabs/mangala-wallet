package com.mangala.wallet.domain.blockchain.usecases

import com.mangala.wallet.domain.blockchain.repository.BlockchainRepository
import com.mangala.wallet.model.blockchain.BlockchainEntity

class CreateBlockchainUseCase(private val blockchainRepository: BlockchainRepository) {

    suspend operator fun invoke(blockchain: List<BlockchainEntity>) {
        blockchainRepository.insertBlockchain(blockchain)
    }
}