package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.model.AntelopeRamOhlcData
import com.mangala.antelope.base.model.RamMarketData
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamChartUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamPriceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.GetRexBalanceInNativeCoinUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.token.GetAntelopeAccountTokenBalanceUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.provider.coingecko.CoinGeckoTokenPriceModel
import com.mangala.wallet.model.token.TokenEntity
import com.mangala.wallet.model.token.TokenPriceEntity
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull

class GetAntelopeAccountBalanceUseCase(
    private val getAccountWithBalanceInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val getRexBalanceInNativeCoinUseCase: GetRexBalanceInNativeCoinUseCase,
    private val getAntelopeAccountTokenBalanceUseCase: GetAntelopeAccountTokenBalanceUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    private val getRamPriceUseCase: GetRamPriceUseCase,
    private val getRamChartUseCase: GetRamChartUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase
) {
    operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType,
        forceReload: Boolean
    ): Flow<Resource<AntelopeBalanceData>> = channelFlow {
        val nativeCoin = getNativeCoinUseCase(blockchainType.uid)
        val currencyCode = getCurrentCurrencyCodeUseCase()

        val ramPriceFlow = getRamPriceUseCase.invokeFlow(blockchainType, forceReload)
        val ramChartFlow = getRamChartUseCase.getOhlcFlow(blockchainType, SamplingInterval.ONE_HOUR)
        val nativeTokenPriceFlow =
            fetchTokenPriceUseCase.getTokenPriceWithSparkline(forceReload, nativeCoin.coinUid, currencyCode)
        val nativeCoinExchangeRateFlow = fetchTokenPriceUseCase.getExchangeRateFlow(
            forceReload,
            nativeCoin.coinUid,
            AntelopeAccountBalanceUnit.entries
                .filter { it != AntelopeAccountBalanceUnit.NativeCoin }
                .map { it.currencySymbol }
        )

        val accountBalance = getAccountBalanceInfo(
            accountName,
            blockchainType,
            forceReload
        )

        combine(
            accountBalance,
            ramPriceFlow,
            ramChartFlow,
            nativeTokenPriceFlow,
            nativeCoinExchangeRateFlow
        ) { accountBalance, ramPrice, ramChart, nativeTokenPrice, nativeCoinExchangeRate ->
            accountBalance.data?.let {
                when {
                    accountBalance is Resource.Loading ||
                            ramPrice is Resource.Loading ||
                            ramChart is Resource.Loading ||
                            nativeTokenPrice is Resource.Loading ||
                            nativeCoinExchangeRate is Resource.Loading -> {
                        Resource.Loading(
                            AntelopeBalanceData(
                                account = it,
                                ramPrice = ramPrice.data,
                                ramChart = ramChart.data,
                                nativeTokenPrice = nativeTokenPrice.data?.firstOrNull(),
                                nativeCoinExchangeRate = nativeCoinExchangeRate.data,
                                nativeCoin = nativeCoin
                            )
                        )
                    }

                    accountBalance is Resource.Error ||
                            ramPrice is Resource.Error ||
                            ramChart is Resource.Error ||
                            nativeTokenPrice is Resource.Error ||
                            nativeCoinExchangeRate is Resource.Error -> {
                        Resource.Error(
                            Exception("Failed to fetch account balances"),
                            AntelopeBalanceData(
                                account = it,
                                ramPrice = ramPrice.data,
                                ramChart = ramChart.data,
                                nativeTokenPrice = nativeTokenPrice.data?.firstOrNull(),
                                nativeCoinExchangeRate = nativeCoinExchangeRate.data,
                                nativeCoin = nativeCoin
                            )
                        )
                    }

                    else -> {
                        Resource.Success(
                            AntelopeBalanceData(
                                account = it,
                                ramPrice = ramPrice.data,
                                ramChart = ramChart.data,
                                nativeTokenPrice = nativeTokenPrice.data?.firstOrNull(),
                                nativeCoinExchangeRate = nativeCoinExchangeRate.data,
                                nativeCoin = nativeCoin
                            )
                        )
                    }
                }
            }
        }.filterNotNull().collect { send(it) }
    }

    internal fun getAccountBalanceInfo(
        accountName: String,
        blockchainType: BlockchainType,
        forceReload: Boolean
    ): Flow<Resource<AccountWithAntelopeBalance>> {
        return channelFlow {
            getAccountWithBalanceInfoUseCase.invokeFlow(
                accountName = accountName,
                blockchainType = blockchainType,
                forceRefresh = forceReload
            ).collect { accountResource ->
                when (accountResource) {
                    is Resource.Loading -> {
                        val balanceDetails = accountResource.data?.let {
                            AntelopeBalanceDetails(
                                coreBalance = it.safeCoreBalance,
                                cpuUsagePercentage = it.cpuLimit?.getUsedPercentage(),
                                netUsagePercentage = it.netLimit?.getUsedPercentage(),
                                ramBalanceBytes = it.ramQuota,
                                stakedCpu = BalanceFormatter.deserializeOrNull(it.selfDelegatedBandwidthCpuWeight.orEmpty())?.amount?.toBigDecimal(),
                                stakedNet = BalanceFormatter.deserializeOrNull(it.selfDelegatedBandwidthNetWeight.orEmpty())?.amount?.toBigDecimal(),
                                stakedInRex = null,
                                tokens = emptyList(),
                                account = accountResource.data
                            )
                        }

                        accountResource.data?.let {
                            send(
                                Resource.Loading(
                                    AccountWithAntelopeBalance(
                                        account = it,
                                        balance = Resource.Loading(balanceDetails)
                                    )
                                )
                            )
                        }
                    }

                    is Resource.Success, is Resource.Error -> {
                        val accountData = accountResource.data

                        if (accountData == null) {
                            send(Resource.Error(Exception("Account data not found"), null))
                            return@collect
                        }

                        // Process REX balance if available
                        val rexBalanceDouble = coroutineScope {
                            val rexBalanceDeferred = async {
                                accountData.rexBalance?.let {
                                    if (it.isBlank()) return@let 0.0

                                    val accountRexBalance = BalanceFormatter.deserialize(it)
                                    getRexBalanceInNativeCoinUseCase(
                                        accountName,
                                        accountRexBalance,
                                        forceReload
                                    )
                                } ?: 0.0
                            }

                            rexBalanceDeferred.await()
                        }

                        // Get token balances
                        val tokenBalanceResult = getAntelopeAccountTokenBalanceUseCase(
                            accountName,
                            blockchainType,
                            forceReload
                        )

                        // Combine the data into AntelopeBalanceDetails
                        val balanceDetails = AntelopeBalanceDetails(
                            coreBalance = accountData.safeCoreBalance,
                            cpuUsagePercentage = accountData.cpuLimit?.getUsedPercentage(),
                            netUsagePercentage = accountData.netLimit?.getUsedPercentage(),
                            ramBalanceBytes = accountData.ramQuota,
                            stakedCpu = BalanceFormatter.deserializeOrNull(accountData.selfDelegatedBandwidthCpuWeight.orEmpty())?.amount?.toBigDecimal(),
                            stakedNet = BalanceFormatter.deserializeOrNull(accountData.selfDelegatedBandwidthNetWeight.orEmpty())?.amount?.toBigDecimal(),
                            stakedInRex = rexBalanceDouble.toBigDecimal(),
                            tokens = tokenBalanceResult.getOrNull() ?: emptyList(),
                            account = accountData
                        )

                        val accountWithBalance = AccountWithAntelopeBalance(
                            account = accountData,
                            balance = Resource.Success(balanceDetails) // TODO: Map Resource type
                        )

                        // Emit with appropriate resource status
                        when (accountResource) {
                            is Resource.Success -> send(Resource.Success(accountWithBalance))
                            is Resource.Error -> send(
                                Resource.Error(
                                    accountResource.exception,
                                    accountWithBalance
                                )
                            )

                            else -> {} // Already handled
                        }
                    }
                }
            }
        }
    }
}

data class AntelopeBalanceData(
    val account: AccountWithAntelopeBalance,
    val ramPrice: RamMarketData?,
    val ramChart: AntelopeRamOhlcData?,
    val nativeTokenPrice: TokenPriceEntity?,
    val nativeCoinExchangeRate: CoinGeckoTokenPriceModel?,
    val nativeCoin: TokenEntity
)