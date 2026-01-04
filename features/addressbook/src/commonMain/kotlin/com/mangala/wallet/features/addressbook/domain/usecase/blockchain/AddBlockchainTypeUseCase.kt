package com.mangala.wallet.features.addressbook.domain.usecase.blockchain

import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository

class AddBlockchainTypeUseCase(private val repository: BlockchainRepository) {
    suspend operator fun invoke(blockchainType: BlockchainTypeEntity): String {
        return repository.insertBlockchainType(blockchainType)
    }
}