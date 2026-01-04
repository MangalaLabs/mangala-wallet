package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository


class CountWalletAddressesForContactUseCase(private val repository: WalletAddressRepository) {
    suspend operator fun invoke(contactId: String): Int {
        return repository.countWalletAddressesForContact(contactId)
    }
}