package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import kotlinx.coroutines.flow.Flow

class GetSelectedWalletAccountsUseCase(
    private val walletRepository: WalletRepository,
    private val baseGetWalletAccountsUseCase: BaseGetWalletAccountsUseCase
) {

    fun invokeFlow(
        filterHiddenAccounts: Boolean = true
    ): Flow<List<AccountBlockchainModel>?> {
        return baseGetWalletAccountsUseCase(
            walletRepository.getSelectedWalletFlow(), filterHiddenAccounts
        )
    }

    suspend operator fun invoke(
        filterHiddenAccounts: Boolean = true,
        networkData: BlockchainNetworkData? = null
    ): List<AccountBlockchainModel>? {
        return baseGetWalletAccountsUseCase(
            walletRepository.getSelectedWallet(), filterHiddenAccounts, networkData
        )
    }
}