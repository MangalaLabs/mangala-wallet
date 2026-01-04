package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.wallet.repository.WalletRepository

class SaveWalletNameUseCase(private val walletRepository: WalletRepository) {

    suspend operator fun invoke(walletName: String, walletId: String) {
        walletRepository.saveWalletName(walletName, walletId)
    }
}