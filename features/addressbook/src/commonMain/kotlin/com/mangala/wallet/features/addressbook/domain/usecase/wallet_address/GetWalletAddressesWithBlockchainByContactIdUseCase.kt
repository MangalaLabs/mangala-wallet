package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithBlockchainModel
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository

class GetWalletAddressesWithBlockchainByContactIdUseCase(private val repository: BlockchainRepository) {
    suspend operator fun invoke(contactId: String): List<WalletAddressWithBlockchainModel> {
        return repository.getWalletAddressesWithBlockchainByContactId(contactId)
    }
}