package com.mangala.wallet.features.addressbook.domain.usecase.contact.wallet_address

import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository

class InsertWalletAddressesBatchUseCase (private val walletAddressRepository: WalletAddressRepository) {
    suspend operator fun invoke(physicalAddresses: List<WalletAddressEntity>): Map<WalletAddressEntity, String> {
        return walletAddressRepository.insertWalletAddressesBatch(physicalAddresses)
    }
}