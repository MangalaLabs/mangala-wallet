package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository

class DeleteWalletAddressUseCase(private val repository: WalletAddressRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return repository.deleteWalletAddress(id)
    }
}