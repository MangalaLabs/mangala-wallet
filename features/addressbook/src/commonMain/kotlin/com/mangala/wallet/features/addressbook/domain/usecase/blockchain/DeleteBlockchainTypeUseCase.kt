package com.mangala.wallet.features.addressbook.domain.usecase.blockchain

import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository

class DeleteBlockchainTypeUseCase(private val repository: BlockchainRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return repository.deleteBlockchainType(id)
    }
}
