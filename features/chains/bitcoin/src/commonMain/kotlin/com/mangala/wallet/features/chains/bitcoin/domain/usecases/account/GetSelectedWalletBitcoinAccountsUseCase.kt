package com.mangala.wallet.features.chains.bitcoin.domain.usecases.account

import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccount
import com.mangala.wallet.features.chains.bitcoin.domain.repository.account.BitcoinAccountRepository
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccountWithBalance
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class GetSelectedWalletBitcoinAccountsUseCase(
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    private val bitcoinAccountRepository: BitcoinAccountRepository,
    private val getAccountTokenBalanceUseCase: GetAccountTokenBalanceUseCase
) {

    suspend fun getWithoutBalance(
        blockchainNetworkData: BlockchainNetworkData
    ): List<BitcoinAccount> {
        val accounts = getSelectedWalletAccountsUseCase.invoke(
            filterHiddenAccounts = true,
            networkData = blockchainNetworkData
        )
        val accountIdAndAccounts = accounts?.associate { account ->
            account.account.id to account
        }
        val accountIds = accountIdAndAccounts?.keys?.toList().orEmpty()

        return bitcoinAccountRepository.getAccounts(blockchainNetworkData.blockchainType, accountIds).first().map {
            it.copy(
                name = accountIdAndAccounts?.get(it.accountId)?.account?.name,
                sortingOrder = it.sortingOrder
            )
        }.sortedBy { it.sortingOrder ?: 0 }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        forceReload: Boolean = false,
        filterHiddenAccounts: Boolean = true,
        blockchainNetworkData: BlockchainNetworkData,
    ): Flow<Resource<List<BitcoinAccountWithBalance>>> = channelFlow {
        getSelectedWalletAccountsUseCase.invokeFlow(
            filterHiddenAccounts = filterHiddenAccounts,
        ).flatMapMerge {
            val accountIdMap = it?.associate { account ->
                account.account.id to account
            }

            bitcoinAccountRepository.getAccounts(blockchainNetworkData.blockchainType, accountIdMap?.keys?.toList().orEmpty()).map {
                it.map {
                    it.copy(name = accountIdMap?.get(it.accountId)?.account?.name)
                }.sortedBy { it.sortingOrder ?: 0 }
            }
        }.collectLatest { accountsUnsorted ->
            val accounts = accountsUnsorted.sortedBy { it.sortingOrder ?: 0 }
            val accountsWithEmptyBalance =
                accounts.map { BitcoinAccountWithBalance(it, Resource.Loading(null)) }

            if (accounts.isEmpty()) {
                send(Resource.Success(accountsWithEmptyBalance))
                return@collectLatest
            }

            send(
                Resource.Loading(
                accounts.map {
                    BitcoinAccountWithBalance(
                        it,
                        Resource.Loading(null)
                    )
                }
            ))

            val accountsWithBalancesFlow = accounts.map { account ->
                getAccountTokenBalanceUseCase(forceReload, blockchainNetworkData, account)
            }

            if (accountsWithBalancesFlow.isEmpty()) {
                send(Resource.Success(emptyList()))
            } else {
                val newFlow = combine(accountsWithBalancesFlow) { accountsWithBalances ->
                    accountsWithBalances.toList()
                }
                newFlow.collect {
                    if (it.all { accountWithBalance -> accountWithBalance.balanceInSatoshis is Resource.Loading }) {
                        send(Resource.Loading(it))
                    } else if (it.all { accountWithBalance -> accountWithBalance.balanceInSatoshis is Resource.Error }) {
                        send(Resource.Error(Exception(), it))
                    } else {
                        send(Resource.Success(it))
                    }
                }
            }
        }
    }
}