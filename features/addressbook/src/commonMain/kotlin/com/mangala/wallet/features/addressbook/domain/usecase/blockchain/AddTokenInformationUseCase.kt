package com.mangala.wallet.features.addressbook.domain.usecase.blockchain

import com.mangala.wallet.features.addressbook.data.model.blockchain.TokenInformationEntity
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository

class AddTokenInformationUseCase(private val repository: BlockchainRepository) {
    suspend operator fun invoke(tokenInformation: TokenInformationEntity): String {
        return repository.insertTokenInformation(tokenInformation)
    }
}