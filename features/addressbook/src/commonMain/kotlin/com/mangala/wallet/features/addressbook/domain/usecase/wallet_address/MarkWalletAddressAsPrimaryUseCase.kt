package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository

class MarkWalletAddressAsPrimaryUseCase(private val repository: BlockchainRepository) {
    suspend operator fun invoke(walletAddressId: String): Boolean {
        return repository.markWalletAddressAsPrimary(walletAddressId)
    }
}