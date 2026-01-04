package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository

class SaveWalletAddressUseCase(private val repository: BlockchainRepository) {
    suspend operator fun invoke(walletAddress: WalletAddressEntity): String {
        return repository.insertWalletAddress(walletAddress)
    }
}