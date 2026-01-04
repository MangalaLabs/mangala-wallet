package com.mangala.wallet.features.addressbook.domain.usecase.blockchain

import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository

class GetBlockchainTypeByIdUseCase(private val repository: BlockchainRepository) {
    suspend operator fun invoke(id: String): BlockchainTypeEntity? {
        return repository.getBlockchainTypeById(id)
    }
}