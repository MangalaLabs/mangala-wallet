package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address

import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository

class VerifyWalletAddressUseCase(private val repository: BlockchainRepository) {
    suspend operator fun invoke(walletAddressId: String): Boolean {
        return repository.verifyWalletAddress(walletAddressId)
    }
}