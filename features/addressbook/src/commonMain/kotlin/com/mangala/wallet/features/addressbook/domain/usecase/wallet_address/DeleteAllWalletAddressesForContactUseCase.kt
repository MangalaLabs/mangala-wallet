package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository

class DeleteAllWalletAddressesForContactUseCase(private val repository: WalletAddressRepository) {
    suspend operator fun invoke(contactId: String): Boolean {
        return repository.deleteWalletAddressesByContactId(contactId)
    }
}