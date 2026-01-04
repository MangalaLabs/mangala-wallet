package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository

class AddWalletAddressUseCase(private val repository: WalletAddressRepository) {
    suspend operator fun invoke(walletAddress: WalletAddressEntity): String {
        return repository.insertWalletAddress(walletAddress)
    }
}
