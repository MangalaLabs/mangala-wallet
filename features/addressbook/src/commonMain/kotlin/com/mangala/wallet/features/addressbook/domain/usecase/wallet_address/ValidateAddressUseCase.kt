package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository

class ValidateAddressUseCase(private val repository: BlockchainRepository) {
    suspend operator fun invoke(address: String, blockchainTypeId: String): Boolean {
        return repository.validateAddress(address, blockchainTypeId)
    }
}