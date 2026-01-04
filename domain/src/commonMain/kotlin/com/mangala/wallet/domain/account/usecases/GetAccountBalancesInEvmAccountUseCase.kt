package com.mangala.wallet.domain.account.usecases

import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

class GetAccountBalancesInEvmAccountUseCase(
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        accountId: String,
        forceReload: Boolean,
        blockchainType: BlockchainType
    ): Flow<Resource<AccountWithBalance>> = channelFlow {
        val account = getAccountByIdUseCase(accountId)

        send(
            Resource.Loading(
                AccountWithBalance(
                    AccountBlockchainModel(account, "", "", ""),
                    Resource.Loading(emptyList())
                )
            )
        )

        getAccountBalanceUseCase.invokeFlowResource(
            forceReload,
            account.bip44Address,
            blockchainType,
            account.id
        ).map { accountBalance ->
            account to accountBalance
        }.collectLatest {
            val (account, tokenBalances) = it

            if (tokenBalances is Resource.Loading) {
                send(Resource.Loading(AccountWithBalance(AccountBlockchainModel(account, "", "", ""), tokenBalances)))
            } else if (tokenBalances is Resource.Error) {
                send(Resource.Error(Exception(), AccountWithBalance(AccountBlockchainModel(account, "", "", ""), tokenBalances)))
            } else {
                send(Resource.Success(AccountWithBalance(AccountBlockchainModel(account, "", "", ""), tokenBalances)))
            }
        }
    }
}