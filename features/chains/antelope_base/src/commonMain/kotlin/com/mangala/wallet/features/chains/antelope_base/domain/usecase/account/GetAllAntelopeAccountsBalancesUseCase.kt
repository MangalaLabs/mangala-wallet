package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.model.AntelopeRamOhlcData
import com.mangala.antelope.base.model.RamMarketData
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamChartUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamPriceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.GetRexBalanceInNativeCoinUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.token.GetAntelopeAccountTokenBalanceUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.provider.coingecko.CoinGeckoTokenPriceModel
import com.mangala.wallet.model.token.TokenEntity
import com.mangala.wallet.model.token.TokenPriceEntity
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GetAllAntelopeAccountsBalancesUseCase(
    private val getAntelopeAccountsUseCase: GetAccountsUseCase,
    private val getAccountWithBalanceInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val getRamPriceUseCase: GetRamPriceUseCase,
    private val getRamChartUseCase: GetRamChartUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    private val getRexBalanceInNativeCoinUseCase: GetRexBalanceInNativeCoinUseCase,
    private val getAntelopeAccountTokenBalanceUseCase: GetAntelopeAccountTokenBalanceUseCase,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase
) {

    operator fun invoke(
        forceReload: Boolean,
        network: BlockchainNetworkData
    ): Flow<Resource<AntelopeBalanceDataList>> = channelFlow {
        val blockchainType = network.blockchainType

        val nativeCoinAsync = async { getNativeCoinUseCase(blockchainType.uid) }
        val accountsAsync = async { getAntelopeAccountsUseCase(includeTempAccounts = true) }
        val currencyCode = async { getCurrentCurrencyCodeUseCase() }

        val nativeCoin = nativeCoinAsync.await()
        val accounts = accountsAsync.await()

        if (accounts.isEmpty()) {
            send(Resource.Success(AntelopeBalanceDataList(emptyList(), null, null, null, null, nativeCoin)))
            return@channelFlow
        }

        send(
            Resource.Loading(
                AntelopeBalanceDataList(
                    accounts = accounts.map {
                        AccountWithAntelopeBalance(
                            account = it,
                            Resource.Loading(null)
                        )
                    },
                    ramPrice = null,
                    ramChart = null,
                    nativeTokenPrice = null,
                    nativeCoinExchangeRate = null,
                    nativeCoin = nativeCoin
                )
            )
        )

        val ramPriceFlow = getRamPriceUseCase.invokeFlow(blockchainType, forceReload)
        val ramChartFlow = getRamChartUseCase.getOhlcFlow(blockchainType, SamplingInterval.ONE_HOUR)
        val nativeTokenPriceFlow = fetchTokenPriceUseCase.getTokenPriceWithSparkline(forceReload, nativeCoin.coinUid, currencyCode.await())
        val nativeCoinExchangeRateFlow = fetchTokenPriceUseCase.getExchangeRateFlow(
            forceReload,
            nativeCoin.coinUid.replace("eos", "vaulta"),
            AntelopeAccountBalanceUnit.entries
                .filter { it != AntelopeAccountBalanceUnit.NativeCoin }
                .map { it.currencySymbol }
        )

        val combinedFlow = accounts.map {
            getAccountBalanceInfo(
                it.accountName,
                blockchainType,
                forceReload
            )
        }

        combine(
            combine(combinedFlow) { it.toList() },
            ramPriceFlow,
            ramChartFlow,
            nativeTokenPriceFlow,
            nativeCoinExchangeRateFlow
        ) { accountBalances, ramPrice, ramChart, nativeTokenPrice, nativeCoinExchangeRate ->
            val accountsWithBalances = accountBalances.mapIndexed { index, resource ->
                AccountWithAntelopeBalance(account = accounts[index], balance = resource)
            }

            when {
                accountBalances.any { it is Resource.Loading } ||
                        ramPrice is Resource.Loading ||
                        ramChart is Resource.Loading ||
                        nativeTokenPrice is Resource.Loading ||
                        nativeCoinExchangeRate is Resource.Loading -> {
                    Resource.Loading(
                        AntelopeBalanceDataList(
                            accounts = accountsWithBalances,
                            ramPrice = ramPrice.data,
                            ramChart = ramChart.data,
                            nativeTokenPrice = nativeTokenPrice.data?.firstOrNull(),
                            nativeCoinExchangeRate = nativeCoinExchangeRate.data,
                            nativeCoin = nativeCoin
                        )
                    )
                }

                accountBalances.any { it is Resource.Error }  ||
                        ramPrice is Resource.Error ||
                        ramChart is Resource.Error ||
                        nativeTokenPrice is Resource.Error ||
                        nativeCoinExchangeRate is Resource.Error -> {
                    Resource.Error(
                        Exception("Failed to fetch account balances"),
                        AntelopeBalanceDataList(
                            accounts = accountsWithBalances,
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
                        AntelopeBalanceDataList(
                            accounts = accountsWithBalances,
                            ramPrice = ramPrice.data,
                            ramChart = ramChart.data,
                            nativeTokenPrice = nativeTokenPrice.data?.firstOrNull(),
                            nativeCoinExchangeRate = nativeCoinExchangeRate.data,
                            nativeCoin = nativeCoin
                        )
                    )
                }
            }
        }.collect { send(it) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getAccountBalanceInfo(
        accountName: String,
        blockchainType: BlockchainType,
        forceReload: Boolean
    ): Flow<Resource<AntelopeBalanceDetails?>> {
        return channelFlow {
            send(Resource.Loading(null))

            val accountFlow = getAccountWithBalanceInfoUseCase.invokeFlow(
                accountName = accountName,
                blockchainType = blockchainType,
                forceRefresh = forceReload
            )

            val tokenBalanceFlow = getAntelopeAccountTokenBalanceUseCase.invokeFlow(
                accountName = accountName,
                blockchainType = blockchainType,
                forceRefresh = forceReload
            )

            val rexBalanceFlow = accountFlow.map { it.data?.rexBalance }.flatMapLatest { rexBalance ->
                if (rexBalance.isNullOrBlank().not()) {
                    val accountRexBalance = BalanceFormatter.deserialize(rexBalance!!)
                    getRexBalanceInNativeCoinUseCase.invokeFlow(
                        accountName,
                        accountRexBalance,
                        forceReload
                    )
                } else {
                    flowOf(Resource.Success(0.0))
                }
            }

            // Combine all three flows
            combine(accountFlow, tokenBalanceFlow, rexBalanceFlow) { accountResource, tokenBalanceResource, rexBalanceResource ->
                val accountData = accountResource.data

                if (accountData == null && accountResource !is Resource.Loading) {
                    return@combine Resource.Error(
                        Exception("Account data not found"),
                        null
                    )
                }

                val balanceDetails = AntelopeBalanceDetails(
                    account = accountData,
                    coreBalance = accountData?.safeCoreBalance,
                    cpuUsagePercentage = accountData?.cpuLimit?.getUsedPercentage(),
                    netUsagePercentage = accountData?.netLimit?.getUsedPercentage(),
                    ramBalanceBytes = accountData?.ramQuota,
                    stakedCpu = BalanceFormatter.deserializeOrNull(accountData?.selfDelegatedBandwidthCpuWeight.orEmpty())?.amount?.toBigDecimal(),
                    stakedNet = BalanceFormatter.deserializeOrNull(accountData?.selfDelegatedBandwidthNetWeight.orEmpty())?.amount?.toBigDecimal(),
                    stakedInRex = rexBalanceResource.data?.toBigDecimal(),
                    tokens = tokenBalanceResource.data
                )

                when {
                    accountResource is Resource.Loading ||
                            tokenBalanceResource is Resource.Loading ||
                            rexBalanceResource is Resource.Loading ->
                        Resource.Loading(balanceDetails)
                    accountResource is Resource.Error ->
                        Resource.Error(accountResource.exception, balanceDetails)
                    tokenBalanceResource is Resource.Error ->
                        Resource.Error(tokenBalanceResource.exception, balanceDetails)
                    rexBalanceResource is Resource.Error ->
                        Resource.Error(rexBalanceResource.exception, balanceDetails)
                    else ->
                        Resource.Success(balanceDetails)
                }
            }.collect { send(it) }
        }
    }
}

data class AntelopeBalanceDataList(
    val accounts: List<AccountWithAntelopeBalance>,
    val ramPrice: RamMarketData?,
    val ramChart: AntelopeRamOhlcData?,
    val nativeTokenPrice: TokenPriceEntity?,
    val nativeCoinExchangeRate: CoinGeckoTokenPriceModel?,
    val nativeCoin: TokenEntity
)

data class AccountWithAntelopeBalance(
    val account: AntelopeAccount,
    val balance: Resource<AntelopeBalanceDetails?>
)

data class AntelopeBalanceDetails(
    val account: AntelopeAccount?,
    val coreBalance: Balance?,
    val cpuUsagePercentage: Double?,
    val netUsagePercentage: Double?,
    val ramBalanceBytes: Long?,
    val stakedCpu: BigDecimal?,
    val stakedNet: BigDecimal?,
    val stakedInRex: BigDecimal?,
    val tokens: List<AntelopeTokenBalance>?
)

enum class AntelopeAccountBalanceUnit(val symbol: String, val currencySymbol: String, val decimal: Long) {
    NativeCoin("A", "", 4),
    USDT("USDT", "usdt", 6),
    BTC("BTC", "btc", 8),
    ETH("ETH", "eth", 18),
    BNB("BNB", "bnb", 18),
}