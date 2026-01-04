package com.mangala.wallet.features.addressbook.domain.usecase.blockchain

import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository

class GetAllBlockchainTypesUseCase(private val repository: BlockchainRepository) {
    suspend operator fun invoke(): List<BlockchainTypeEntity> {
        return repository.getAllBlockchainTypes()
    }
}