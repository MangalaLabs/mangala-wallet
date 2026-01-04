package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository

class SetWalletAddressAsDefaultUseCase(private val repository: WalletAddressRepository) {
    suspend operator fun invoke(id: String, contactId: String, blockchainNetworkId: String): Boolean {
        return repository.setWalletAddressAsDefault(id, contactId, blockchainNetworkId)
    }
}