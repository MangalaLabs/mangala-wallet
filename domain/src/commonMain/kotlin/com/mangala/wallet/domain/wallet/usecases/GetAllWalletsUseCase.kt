package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.model.wallet.domain.WalletModel

class GetAllWalletsUseCase(private val walletRepository: WalletRepository) {
    suspend operator fun invoke(): List<WalletModel> {
        return walletRepository.getAllWallets()
    }
}