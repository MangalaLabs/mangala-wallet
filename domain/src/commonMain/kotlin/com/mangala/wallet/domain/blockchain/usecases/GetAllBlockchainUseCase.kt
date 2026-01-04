package com.mangala.wallet.domain.blockchain.usecases

import com.mangala.wallet.domain.blockchain.repository.BlockchainRepository
import com.mangala.wallet.model.blockchain.BlockchainEntity

class GetAllBlockchainUseCase(private val blockchainRepository: BlockchainRepository) {

    suspend operator fun invoke(): List<BlockchainEntity>{
        return blockchainRepository.getAllBlockchain()
    }
}