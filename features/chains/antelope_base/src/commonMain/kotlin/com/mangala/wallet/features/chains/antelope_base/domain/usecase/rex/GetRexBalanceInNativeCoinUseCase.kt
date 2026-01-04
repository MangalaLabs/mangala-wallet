package com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex

import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexFundInfo
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexQueueInfo
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class GetRexBalanceInNativeCoinUseCase(
    private val calculateRexPriceInEosUseCase: CalculateRexPriceInEosUseCase,
    private val getRexQueueInfoUseCase: GetRexQueueInfoUseCase,
    private val getRexFundInfoUseCase: GetRexFundInfoUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) {
    suspend operator fun invoke(
        accountName: String,
        accountRexBalance: Balance,
        forceRefresh: Boolean
    ): Double {
        return coroutineScope {
            val blockchainType = getSelectedNetworkUseCase().blockchainType

            val rexPriceInEosAsync = async { calculateRexPriceInEosUseCase(blockchainType, forceRefresh) }
            val rexFundAsync = async {
                getRexFundInfoUseCase(
                    accountName = accountName,
                    blockchainType = blockchainType,
                    forceRefresh = forceRefresh
                )
            }
            val rexQueueAsync = async {
                getRexQueueInfoUseCase(
                    accountName = accountName,
                    blockchainType = blockchainType,
                    forceRefresh = forceRefresh
                )
            }

            val rexPriceInEos = rexPriceInEosAsync.await()
            val rexFund = rexFundAsync.await().getOrNull()
            val rexQueue = rexQueueAsync.await().getOrNull()

            return@coroutineScope calculateRexBalance(rexPriceInEos, rexFund, rexQueue, accountName, accountRexBalance)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun invokeFlow(
        accountName: String,
        accountRexBalance: Balance?,
        forceRefresh: Boolean
    ): Flow<Resource<Double>> {
        return getSelectedNetworkUseCase.invokeFlow().flatMapLatest { network ->
            if (accountRexBalance == null) return@flatMapLatest flowOf(Resource.Success(0.0))

            val blockchainType = network.blockchainType

            combine(
                calculateRexPriceInEosUseCase.invokeFlow(blockchainType, forceRefresh),
                getRexFundInfoUseCase.invokeFlow(accountName, blockchainType, forceRefresh),
                getRexQueueInfoUseCase.invokeFlow(accountName, blockchainType, forceRefresh)
            ) { rexPriceResource, rexFundResource, rexQueueResource ->
                if (rexPriceResource.isLoading() || rexFundResource.isLoading() || rexQueueResource.isLoading()) {
                    val previousData = calculateRexBalance(
                        rexPriceResource.data,
                        rexFundResource.data,
                        rexQueueResource.data,
                        accountName,
                        accountRexBalance
                    )
                    return@combine Resource.Loading(previousData)
                }

                val errors = listOfNotNull(
                    (rexPriceResource as? Resource.Error)?.exception,
                    (rexFundResource as? Resource.Error)?.exception,
                    (rexQueueResource as? Resource.Error)?.exception
                )

                if (errors.isNotEmpty()) {
                    val combinedException = errors.firstOrNull() ?: Exception("Unknown error")

                    return@combine Resource.Error(combinedException)
                }

                val rexPriceInEos = rexPriceResource.data
                val rexFund = rexFundResource.data
                val rexQueue = rexQueueResource.data

                val totalAmount = calculateRexBalance(
                    rexPriceInEos,
                    rexFund,
                    rexQueue,
                    accountName,
                    accountRexBalance
                )

                return@combine Resource.Success(totalAmount)
            }
        }
    }

    private fun calculateRexBalance(
        rexPriceInEos: Balance?,
        rexFund: AntelopeRexFundInfo?,
        rexQueue: AntelopeRexQueueInfo?,
        accountName: String,
        accountRexBalance: Balance
    ): Double {
        val rexQueueRefunds = rexQueue?.rows
            ?.filter { it.owner == accountName }
            ?.mapNotNull { BalanceFormatter.deserializeOrNull(it.proceeds)?.amount }
            ?.sum() ?: 0.0

        // Queue refunds field on bloks.io
        val rexQueueRefundsTotal = Balance(rexQueueRefunds, accountRexBalance.symbol)

        // Deposited field on bloks.io
        val rexDeposited = if (rexFund?.balance.isNullOrEmpty().not()) {
            BalanceFormatter.deserialize(rexFund?.balance!!).amount
        } else {
            0.0
        }

        // (Liquid loans + savings loans + processing loans) on bloks.io
        val rexPriceAmount = rexPriceInEos?.amount ?: 0.0
        val rexLiquidLoansInEos = accountRexBalance.amount * rexPriceAmount

        println("GetRexBalanceInNativeCoinUseCase accountName $accountName rexBalance ${accountRexBalance.amount} rexPriceInEos $rexPriceAmount rexLiquidLoansInEos $rexLiquidLoansInEos rexQueueRefundsTotal $rexQueueRefundsTotal rexDeposited $rexDeposited")

        return rexLiquidLoansInEos + rexQueueRefundsTotal.amount + rexDeposited
    }
}