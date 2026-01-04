package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.model.wallet.domain.WalletModel
import kotlinx.coroutines.flow.Flow

class GetSelectedWalletUseCase(private val walletRepository: WalletRepository) {

    operator fun invoke(): WalletModel? = walletRepository.getSelectedWallet()

    fun invokeFlow(): Flow<WalletModel?> = walletRepository.getSelectedWalletFlow()

}