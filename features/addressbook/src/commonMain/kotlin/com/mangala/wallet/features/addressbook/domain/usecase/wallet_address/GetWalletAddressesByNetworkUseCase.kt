package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository

class GetWalletAddressesByNetworkUseCase(private val repository: WalletAddressRepository) {
    suspend operator fun invoke(networkId: String, limit: Int = 5, offset: Int = 0): List<WalletAddressWithNetworkModel> {
        return repository.getWalletAddressesByNetwork(networkId, limit, offset)
    }
}
