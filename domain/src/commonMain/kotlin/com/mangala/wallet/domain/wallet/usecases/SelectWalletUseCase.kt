package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.wallet.repository.WalletRepository

class SelectWalletUseCase(private val walletRepository: WalletRepository) {

    suspend operator fun invoke(walletId: String) {
        walletRepository.setSelectedWallet(walletId)
    }
}