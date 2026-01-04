package com.mangala.wallet.domain.account.usecases

import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetAllAccountBalancesInEvmWalletUseCase(
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        forceReload: Boolean,
        network: BlockchainNetworkData
    ): Flow<Resource<List<AccountWithBalance>>> = channelFlow {
        getSelectedWalletAccountsUseCase.invokeFlow(forceReload).collectLatest { accounts ->
            val accountsWithEmptyBalance =
                accounts?.map { AccountWithBalance(it, Resource.Loading(emptyList())) }

            if (accounts.isNullOrEmpty()) {
                send(Resource.Success(accountsWithEmptyBalance ?: emptyList()))
                return@collectLatest
            }

            send(Resource.Loading(accounts.map {
                AccountWithBalance(
                    it,
                    Resource.Loading(emptyList())
                )
            }))

            val accountsWithBalancesFlow = accounts.map { account ->
                getAccountBalanceUseCase.invokeFlowResource(
                    forceReload,
                    account.bip44Address,
                    network.blockchainType,
                    account.account.id
                ).map { accountBalance ->
                    account to accountBalance
                }
            }

            if (accountsWithBalancesFlow.isEmpty()) {
                send(Resource.Success(emptyList()))
            } else {
                val newFlow = combine(accountsWithBalancesFlow) { accountsWithBalances ->
                    accountsWithBalances.toList().map {
                        val account = it.first
                        val tokenBalances = it.second

                        AccountWithBalance(
                            account,
                            tokenBalances
                        )
                    }
                }
                newFlow.collect {
                    if (it.all { accountWithBalance -> accountWithBalance.tokenBalances is Resource.Loading }) {
                        send(Resource.Loading(it))
                    } else if (it.all { accountWithBalance -> accountWithBalance.tokenBalances is Resource.Error }) {
                        send(Resource.Error(Exception(), it))
                    } else {
                        send(Resource.Success(it))
                    }
                }
            }
        }
    }
}

data class AccountWithBalance(
    val account: AccountBlockchainModel,
    val tokenBalances: Resource<List<TokenBalanceModel>>
)