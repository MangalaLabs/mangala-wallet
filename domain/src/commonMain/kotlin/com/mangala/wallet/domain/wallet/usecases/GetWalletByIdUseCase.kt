package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.model.wallet.domain.WalletModel
import kotlinx.coroutines.flow.Flow

class GetWalletByIdUseCase(private val walletRepository: WalletRepository) {
    suspend operator fun invoke(walletId: String): WalletModel? {
        return walletRepository.getWalletById(walletId)
    }

    fun invokeFlow(walletId: String): Flow<WalletModel?> {
        return walletRepository.getWalletByIdFlow(walletId)
    }
}