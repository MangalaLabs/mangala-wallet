package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.account.repository.AccountRepository
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.wallet.domain.WalletModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf

class BaseGetWalletAccountsUseCase(
    private val accountRepository: AccountRepository,
    private val mapAccountToAccountBlockchainUseCase: MapAccountToAccountBlockchainUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        walletFlow: Flow<WalletModel?>,
        filterHiddenAccounts: Boolean,
    ): Flow<List<AccountBlockchainModel>?> {
        return walletFlow.flatMapConcat { wallet ->
            wallet?.let {
                combine(
                    accountRepository.getAllAccountsByWalletIdFlow(wallet.id, filterHiddenAccounts),
                    getSelectedNetworkUseCase.invokeFlow()
                ) { accounts, networkData ->
                    accounts.map { account ->
                        mapAccountToAccountBlockchainUseCase(
                            account, wallet, BlockchainType.fromUid(networkData.blockChainUid)
                        )
                    }
                }
            } ?: flowOf(null)
        }
    }

    suspend operator fun invoke(
        wallet: WalletModel?,
        filterHiddenAccounts: Boolean,
        network: BlockchainNetworkData? = null
    ): List<AccountBlockchainModel>? {
        return wallet?.let {
            val networkData = network ?: getSelectedNetworkUseCase()
            val accounts = accountRepository.getAllAccountsByWalletId(wallet.id, filterHiddenAccounts)

            accounts.map { account ->
                // TODO: for accounts already containing address, we shouldn't derive the address again
                if (account.bip44Address.isNotEmpty()) {
                    return@map AccountBlockchainModel(
                        account = account,
                        account.bip44Address,
                        account.bip49Address,
                        account.bip84Address
                    )
                } else {
                    mapAccountToAccountBlockchainUseCase(
                        account, wallet, BlockchainType.fromUid(networkData.blockChainUid)
                    )
                }
            }
        }
    }
}