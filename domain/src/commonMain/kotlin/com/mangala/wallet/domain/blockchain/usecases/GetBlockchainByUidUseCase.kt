package com.mangala.wallet.domain.blockchain.usecases

import com.mangala.wallet.domain.blockchain.repository.BlockchainRepository
import com.mangala.wallet.model.blockchain.BlockchainEntity

class GetBlockchainByUidUseCase(private val blockchainRepository: BlockchainRepository) {

    suspend operator fun invoke(uid: String): List<BlockchainEntity>{
        return blockchainRepository.getBlockchainById(uid)
    }
}
