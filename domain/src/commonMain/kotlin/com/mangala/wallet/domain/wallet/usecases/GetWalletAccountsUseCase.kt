package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import kotlinx.coroutines.flow.Flow

class GetWalletAccountsUseCase(
    private val walletRepository: WalletRepository,
    private val baseGetWalletAccountsUseCase: BaseGetWalletAccountsUseCase
) {

    fun invokeFlow(
        filterHiddenAccounts: Boolean = true,
        walletId: String
    ): Flow<List<AccountBlockchainModel>?> {
        return baseGetWalletAccountsUseCase(
            walletRepository.getWalletByIdFlow(walletId), filterHiddenAccounts
        )
    }

    suspend operator fun invoke(
        filterHiddenAccounts: Boolean = true,
        walletId: String
    ): List<AccountBlockchainModel>? {
        return baseGetWalletAccountsUseCase(
            walletRepository.getWalletById(walletId),
            filterHiddenAccounts
        )
    }
}