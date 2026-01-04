package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository

class GetDefaultWalletAddressUseCase(private val repository: WalletAddressRepository) {
    suspend operator fun invoke(contactId: String, blockchainNetworkId: String): WalletAddressEntity? {
        return repository.getDefaultWalletAddress(contactId, blockchainNetworkId)
    }
}