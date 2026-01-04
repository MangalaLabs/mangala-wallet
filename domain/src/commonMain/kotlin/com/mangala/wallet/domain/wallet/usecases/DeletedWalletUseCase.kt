package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.wallet.repository.WalletRepository

class DeletedWalletUseCase(private val walletRepository: WalletRepository) {
    suspend operator fun invoke(walletId: String): Unit = walletRepository.deletedWallet(walletId)
}