package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository

class GetWalletAddressesForContactUseCase(private val repository: WalletAddressRepository) {
    suspend operator fun invoke(contactId: String, limit: Int = 5, offset: Int = 0): List<WalletAddressWithNetworkModel> {
        return repository.getWalletAddressesForContact(contactId, limit, offset)
    }
}