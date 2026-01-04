package com.mangala.wallet.features.portfolio.presentation

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.features.wallet.presentation.AntelopeAccountItemUiModel
import com.mangala.features.wallet.presentation.AntelopeAssetsUiModel
import com.mangala.features.wallet.presentation.AntelopeAssetsUiModel.RamBalanceUiModel
import com.mangala.features.wallet.presentation.AntelopeAssetsUiModel.TokenBalanceUiModel
import com.mangala.features.wallet.presentation.toImageSource
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AntelopeSymbolUtils
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.domain.account.usecases.GetAccountBalancesInEvmAccountUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveBalanceVisibleStatusUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAntelopeAccountBalanceUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetAccountBalancesInBitcoinAccountUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.model.token.TokenPriceEntity
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ext.bytesToKilobytes
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.toBigDecimalOrNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PortfolioScreenModel(
    private val accountId: String,
    networkType: String,
    val address: String,
    val initialAccountName: String,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getBalanceVisibleStatusUseCase: GetBalanceVisibleStatusUseCase,
    // EVM
    private val getAccountBalancesInEvmAccountUseCase: GetAccountBalancesInEvmAccountUseCase,
    private val saveBalanceVisibleStatusUseCase: SaveBalanceVisibleStatusUseCase,
    // Antelope
    private val getAntelopeAccountBalanceUseCase: GetAntelopeAccountBalanceUseCase,
    // Bitcoin
    private val getAccountBalancesInBitcoinAccountUseCase: GetAccountBalancesInBitcoinAccountUseCase,
    // Other
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
) : BaseScreenModel() {

    private val _uiState =
        MutableStateFlow<PortfolioScreenUiState>(
            PortfolioScreenUiState.Data(
                portfolioScreenUiModel = when (val networkType = NetworkType.valueOf(networkType)) {
                    NetworkType.EVM -> {
                        PortfolioScreenUiModel.Evm(accountName = initialAccountName, isLoading = true, networkSelected = null)
                    }
                    NetworkType.ANTELOPE -> PortfolioScreenUiModel.Antelope(accountName = initialAccountName, isLoading = true, networkSelected = null)
                    NetworkType.BITCOIN -> PortfolioScreenUiModel.Bitcoin(accountName = initialAccountName, isLoading = true, networkSelected = null)
                    else -> throw UnsupportedOperationException("$networkType is not supported yet")
                })
            )


    val uiState: StateFlow<PortfolioScreenUiState> get() = _uiState

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var balanceVisible = true
    private var loadBalanceJob: Job? = null

    override fun doOnComposableStarted() {
        lifecycleScope.launch {
            val networkSelected = getSelectedNetworkUseCase()
            initializeState(networkSelected)
            loadDataIfChanged(forceRefresh = false)
        }
        lifecycleScope.launch {
            collectBalanceVisibleStatus()
        }
    }

    fun pullToRefresh() {
        screenModelScope.launch {
            _isLoading.value = true
            loadDataIfChanged(forceRefresh = true)
            _isLoading.value = false
        }
    }

    private suspend fun loadDataIfChanged(forceRefresh: Boolean) {
        val networkSelected = getSelectedNetworkUseCase()
        val newBlockchainType = networkSelected.blockchainType
        screenModelScope.launch {
            _uiState.update { current ->
                if (current is PortfolioScreenUiState.Data) {
                    val updateUiModel = when (val uiModel = current.portfolioScreenUiModel) {
                        is PortfolioScreenUiModel.Evm -> uiModel.copy(isLoading = true)
                        is PortfolioScreenUiModel.Antelope -> uiModel.copy(isLoading = true)
                        is PortfolioScreenUiModel.Bitcoin -> uiModel.copy(isLoading = true)
                    }
                    current.copy(portfolioScreenUiModel = updateUiModel)
                } else {
                    current
                }
            }
        }
        getAccountInfo(forceRefresh = forceRefresh, networkSelected = networkSelected)
    }

    private suspend fun initializeState(networkSelected: BlockchainNetworkData) {
        val currencyCode = getCurrentCurrencyCodeUseCase()
        val currencySymbol = Currency.valueOf(currencyCode).symbol
        _uiState.update { currentState ->
            when (networkSelected.blockchainType.networkType) {
                NetworkType.EVM -> {
                    val newUiModel = PortfolioScreenUiModel.Evm(
                        accountName = initialAccountName,
                        tokenBalances = emptyList(),
                        isBalanceVisible = balanceVisible,
                        hideZeroBalances = (currentState as? PortfolioScreenUiState.Data)?.portfolioScreenUiModel.let {
                            if (it is PortfolioScreenUiModel.Evm) it.hideZeroBalances else false
                        },
                        currencySymbol = currencySymbol,
                        isLoading = true,
                        networkSelected = networkSelected
                    )
                    PortfolioScreenUiState.Data(newUiModel)
                }

                NetworkType.ANTELOPE -> {
                    val fiatCurrencySymbol = Currency.valueOf(currencyCode).symbol
                    val newUiModel = PortfolioScreenUiModel.Antelope(
                        accountName = accountId,
                        hideZeroBalances = false,
                        tokenQuery = "",
                        fiatCurrencySymbol = fiatCurrencySymbol,
                        account = null,
                        networkSelected = networkSelected,
                        selectedAccountIndex = 0,
                        isBalanceVisible = balanceVisible,
                        isLoading = true
                    )

                    PortfolioScreenUiState.Data(newUiModel)
                }
                NetworkType.BITCOIN -> {
                    val newUiModel = PortfolioScreenUiModel.Bitcoin(
                        accountName = initialAccountName,
                        tokenBalances = emptyList(),
                        isBalanceVisible = balanceVisible,
                        hideZeroBalances = (currentState as? PortfolioScreenUiState.Data)?.portfolioScreenUiModel.let {
                            if (it is PortfolioScreenUiModel.Bitcoin) it.hideZeroBalances else false
                        },
                        currencySymbol = currencySymbol,
                        isLoading = true,
                        networkSelected = networkSelected
                    )
                    PortfolioScreenUiState.Data(newUiModel)
                }
                else -> {
                    currentState // Default case to handle unexpected network types
                }
            }
        }
    }

    private fun getAccountInfo(forceRefresh: Boolean, networkSelected: BlockchainNetworkData) {
        val blockchainType = networkSelected.blockchainType

        loadBalanceJob?.cancel()
        loadBalanceJob = lifecycleScope.launch {
            val currencyCode = getCurrentCurrencyCodeUseCase()
            val currency = Currency.valueOf(currencyCode)
            val currentState = _uiState.value
            if (currentState is PortfolioScreenUiState.Data) {
                when (val uiModel = currentState.portfolioScreenUiModel) {
                    is PortfolioScreenUiModel.Evm -> {
                        getAccountBalancesInEvmAccountUseCase(
                            accountId,
                            forceRefresh,
                            blockchainType
                        ).collectLatest { balanceData ->
                            balanceData.data?.let {
                                val updatedUiModel = uiModel.copy(
                                    accountName = it.account.account.name,
                                    tokenBalances = it.tokenBalances.data.orEmpty(),
                                    isBalanceVisible = balanceVisible,
                                    hideZeroBalances = uiModel.hideZeroBalances,
                                    currencySymbol = currency.symbol,
                                    isLoading = balanceData.isLoading()
                                )
                                _uiState.update {
                                    if (it is PortfolioScreenUiState.Data && it.portfolioScreenUiModel is PortfolioScreenUiModel.Evm) {
                                        it.copy(portfolioScreenUiModel = updatedUiModel)
                                    } else {
                                        it
                                    }
                                }
                            }
                        }
                    }
                    
                    is PortfolioScreenUiModel.Bitcoin -> {
                        getAccountBalancesInBitcoinAccountUseCase(
                            accountId,
                            forceRefresh,
                            blockchainNetworkData = networkSelected
                        ).collectLatest { balanceData ->
                            balanceData.data?.let {
                                val updatedUiModel = uiModel.copy(
                                    accountName = it.account.name.orEmpty(),
                                    tokenBalances = it.balanceInSatoshis.data?.let { listOf(it) }.orEmpty(),
                                    isBalanceVisible = balanceVisible,
                                    hideZeroBalances = uiModel.hideZeroBalances,
                                    currencySymbol = currency.symbol,
                                    isLoading = balanceData.isLoading()
                                )
                                _uiState.update {
                                    if (it is PortfolioScreenUiState.Data && it.portfolioScreenUiModel is PortfolioScreenUiModel.Bitcoin) {
                                        it.copy(portfolioScreenUiModel = updatedUiModel)
                                    } else {
                                        it
                                    }
                                }
                            }
                        }
                    }

                    is PortfolioScreenUiModel.Antelope -> {
                        getAntelopeAccountBalanceUseCase(
                            accountId,
                            blockchainType,
                            forceRefresh
                        ).collectLatest { balanceDataResource ->
                            val balanceData = balanceDataResource.data ?: return@collectLatest

                            val currentUiModel = currentState.portfolioScreenUiModel as? PortfolioScreenUiModel.Antelope
                            val currencySymbol = currentUiModel?.fiatCurrencySymbol.orEmpty()

                            val cpuWeight =
                                BalanceFormatter.deserializeOrNull(balanceData.account.account.selfDelegatedBandwidthCpuWeight.orEmpty())
                            val netWeight =
                                BalanceFormatter.deserializeOrNull(balanceData.account.account.selfDelegatedBandwidthNetWeight.orEmpty())

                            _uiState.update {
                                val nativeTokenPrice = balanceData.nativeTokenPrice
                                val nativeCoinPriceChange24h =
                                    nativeTokenPrice?.priceChangePercentage24h.orEmpty()
                                val nativeCoinPnl = if (nativeCoinPriceChange24h.isBlank()) {
                                    null
                                } else {
                                    BigDecimal.parseString(nativeCoinPriceChange24h).scale(2)
                                }
                                val nativeCoinPrice = nativeTokenPrice?.currentPrice?.toBigDecimalOrNull()
                                val tokenBalances = balanceData.account.balance.data?.tokens?.map { token ->
                                    TokenBalanceUiModel(
                                        key = token.key,
                                        name = token.metadata.name,
                                        symbol = token.symbol,
                                        balance = token.amount.toBigDecimal(),
                                        priceInNativeCoin =  if (token.symbol == "A") BigDecimal.ONE else token.exchanges.firstOrNull()?.price?.toBigDecimal()
                                            ?: BigDecimal.ZERO,
                                        nativeCoinPrice = nativeCoinPrice ?: BigDecimal.ZERO,
                                        iconResource = when {
                                            token.symbol == "A" -> ImageSource.Resource(MR.images.vaulta)
                                            token.symbol == "V" && blockchainType.isEosNetwork() -> ImageSource.Resource(MR.images.vaultram)
                                            else -> token.metadata.toImageSource()
                                        },
                                        fiatSymbol = currencySymbol,
                                        sparkline = if (token.symbol == "A") nativeTokenPrice?.sparklineIn7d?.price else null,
                                        priceChangePercentage24h = if (token.symbol == "A") nativeTokenPrice?.priceChangePercentage24h?.toBigDecimalOrNull() else null
                                    )
                                } ?: emptyList()
                                val totalValueInNativeCoin = getAntelopeTotalAccountValue(
                                    balanceData.account.account.ramQuota,
                                    balanceData.ramPrice?.price,
                                    balanceData.account.balance.data?.coreBalance?.amount?.toBigDecimal(),
                                    balanceData.account.balance.data?.stakedCpu,
                                    balanceData.account.balance.data?.stakedNet,
                                    balanceData.account.balance.data?.stakedInRex,
                                    tokenBalances.map { it.priceInNativeCoin?.times(it.balance.orZero()).orZero() }
                                        .reduceOrNull { acc, value -> acc.plus(value) }
                                )
                                val coreBalance = balanceData.account.account.safeCoreBalance
                                val assets = listOf(
                                    // Add item for RAM
                                    RamBalanceUiModel(
                                        key = "${blockchainType.uid}AntelopeRam",
                                        sparkline = balanceData.ramChart?.getSparklineData() ?: emptyList(),
                                        balance = balanceData.account.account.ramQuota?.toBigDecimal(),
                                        ramUsed = balanceData.account.account.ramUsage?.toBigDecimal() ?: BigDecimal.ZERO,
                                        priceChangePercentage24h = balanceData.ramChart?.getPriceChangePercentage24h()?.toBigDecimal(),
                                        ramPriceInKilobytes = balanceData.ramPrice?.price,
                                        nativeCoinSymbol = AntelopeSymbolUtils.formatSymbol(blockchainType.getNativeTokenSymbol(), blockchainType),
                                        accountName = balanceData.account.account.accountName,
                                        blockchainType = blockchainType
                                    ),
                                    // Add item for native coin
                                    TokenBalanceUiModel(
                                        key = "${blockchainType.uid}${balanceData.nativeCoin.coinUid}AntelopeNativeCoin",
                                        name = coreBalance.symbol,
                                        symbol = coreBalance.symbol,
                                        balance = coreBalance.amount.toBigDecimal(),
                                        priceInNativeCoin = BigDecimal.ONE,
                                        nativeCoinPrice = nativeCoinPrice ?: BigDecimal.ZERO,
                                        iconResource = ImageSource.Resource(MR.images.eos_new),
                                        fiatSymbol = currencySymbol,
                                        sparkline = nativeTokenPrice?.sparklineIn7d?.price,
                                        priceChangePercentage24h = nativeTokenPrice?.priceChangePercentage24h?.toBigDecimalOrNull(),
                                    )
                                ) + tokenBalances

                                val updatedAccount = currentUiModel?.account?.copy(
                                    coreBalanceValue = coreBalance.amount.toBigDecimal(),
                                    coreBalanceSymbol = blockchainType.getNativeTokenSymbol(),
                                    cpuUsagePercentage = balanceData.account.account.cpuLimit?.getUsedPercentage(),
                                    netUsagePercentage = balanceData.account.account?.netLimit?.getUsedPercentage(),
                                    ramBalanceBytes = balanceData.account.account.ramQuota,
                                    ramPriceKilobytes = balanceData.ramPrice?.price,
                                    stakedCpu = cpuWeight?.amount?.toBigDecimal() ?: BigDecimal.ZERO,
                                    stakedNet = netWeight?.amount?.toBigDecimal() ?: BigDecimal.ZERO,
                                    stakedInRex = balanceData.account.balance.data?.stakedInRex,
                                    nativeCoinPnl = nativeTokenPrice?.priceChangePercentage24h?.toBigDecimalOrNull(),
                                    nativeCoinPrice = nativeTokenPrice?.currentPrice?.toBigDecimalOrNull(),
                                    totalValueInNativeCoin = totalValueInNativeCoin,
                                    assets = assets
                                ) ?: AntelopeAccountItemUiModel(
                                    account = balanceData.account.account,
                                    coreBalanceValue = coreBalance.amount.toBigDecimal(),
                                    coreBalanceSymbol = blockchainType.getNativeTokenSymbol(),
                                    cpuUsagePercentage = balanceData.account.account.cpuLimit?.getUsedPercentage(),
                                    netUsagePercentage = balanceData.account.account.netLimit?.getUsedPercentage(),
                                    ramBalanceBytes = balanceData.account.account.ramQuota,
                                    ramPriceKilobytes = balanceData.ramPrice?.price,
                                    stakedCpu = cpuWeight?.amount?.toBigDecimal() ?: BigDecimal.ZERO,
                                    stakedNet = netWeight?.amount?.toBigDecimal() ?: BigDecimal.ZERO,
                                    stakedInRex = balanceData.account.balance.data?.stakedInRex,
                                    nativeCoinPnl = nativeTokenPrice?.priceChangePercentage24h?.toBigDecimalOrNull(),
                                    nativeCoinPrice = nativeTokenPrice?.currentPrice?.toBigDecimalOrNull(),
                                    totalValueInNativeCoin = totalValueInNativeCoin,
                                    assets = assets,
                                    currencySymbol = currentUiModel?.fiatCurrencySymbol.orEmpty(),
                                    isBalanceVisible = currentUiModel?.isBalanceVisible ?: true,
                                    coinGeckoExchangeRate = null,
                                    isLoading = balanceDataResource.isLoading()
                                )

                                if (it is PortfolioScreenUiState.Data && it.portfolioScreenUiModel is PortfolioScreenUiModel.Antelope) {
                                    it.copy(
                                        portfolioScreenUiModel = it.portfolioScreenUiModel.copy(
                                            account = updatedAccount,
                                            isLoading = false
                                        )
                                    )
                                } else {
                                    it
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    fun toggleBalanceVisible(showBalance: Boolean) {
        screenModelScope.launch {
            saveBalanceVisibleStatusUseCase(showBalance)
        }
    }

    fun onChangeTokenQuery(query: String) {
        _uiState.update { currentState ->
            if (currentState is PortfolioScreenUiState.Data) {
                val updateUiModel = when (val uiModel = currentState.portfolioScreenUiModel) {
                    is PortfolioScreenUiModel.Evm -> uiModel.copy(tokenQuery = query)
                    is PortfolioScreenUiModel.Antelope -> uiModel.copy(tokenQuery = query)
                    is PortfolioScreenUiModel.Bitcoin -> uiModel.copy(tokenQuery = query)
                }
                currentState.copy(portfolioScreenUiModel = updateUiModel)
            } else {
                currentState
            }
        }

    }

    fun toggleHideZeroBalances(hideZeroBalances: Boolean) {
        _uiState.update { currentState ->
            if (currentState is PortfolioScreenUiState.Data) {
                val updateUiModel = when (val uiModel = currentState.portfolioScreenUiModel) {
                    is PortfolioScreenUiModel.Evm -> uiModel.copy(hideZeroBalances = hideZeroBalances)
                    is PortfolioScreenUiModel.Antelope -> uiModel.copy(hideZeroBalances = hideZeroBalances)
                    is PortfolioScreenUiModel.Bitcoin -> uiModel.copy(hideZeroBalances = hideZeroBalances)
                }
                currentState.copy(portfolioScreenUiModel = updateUiModel)
            } else {
                currentState
            }
        }
    }

    private suspend fun collectBalanceVisibleStatus() {
        getBalanceVisibleStatusUseCase().collectLatest { showBalance ->
            balanceVisible = showBalance
            _uiState.update { currentState ->
                if (currentState is PortfolioScreenUiState.Data) {
                    val updatedUiModel = when (val uiModel = currentState.portfolioScreenUiModel) {
                        is PortfolioScreenUiModel.Evm -> {
                            if (uiModel.isBalanceVisible != showBalance) {
                                uiModel.copy(
                                    isBalanceVisible = showBalance
                                )
                            } else {
                                uiModel
                            }
                        }

                        is PortfolioScreenUiModel.Antelope -> {
                            val accountsUpdated =
                                if (uiModel.account?.isBalanceVisible != showBalance) {
                                    uiModel.account?.copy(isBalanceVisible = showBalance)
                                } else {
                                    uiModel.account
                                }

                            if (uiModel.isBalanceVisible != showBalance || accountsUpdated != uiModel.account) {
                                uiModel.copy(
                                    isBalanceVisible = showBalance,
                                    account = accountsUpdated
                                )
                            } else {
                                uiModel
                            }
                        }
                        
                        is PortfolioScreenUiModel.Bitcoin -> {
                            if (uiModel.isBalanceVisible != showBalance) {
                                uiModel.copy(
                                    isBalanceVisible = showBalance
                                )
                            } else {
                                uiModel
                            }
                        }
                    }
                    currentState.copy(portfolioScreenUiModel = updatedUiModel)
                } else {
                    currentState
                }
            }
        }
    }

    private fun getAntelopeTotalAccountValue(
        ramBalanceBytes: Long?,
        ramPriceKilobytes: BigDecimal?,
        coreBalanceValue: BigDecimal?,
        stakedCpu: BigDecimal?,
        stakedNet: BigDecimal?,
        stakedInRex: BigDecimal?,
        tokenBalance: BigDecimal?
    ): BigDecimal? {
        val ramBalanceInNativeCoin = if (ramBalanceBytes == null || ramPriceKilobytes == null) {
            null
        } else {
            ramBalanceBytes.bytesToKilobytes().toBigDecimal().times(ramPriceKilobytes)
        }
        println("totalValueFormatted RAM price ${ramPriceKilobytes?.toStringExpanded()} RAM balance ${ramBalanceBytes?.bytesToKilobytes()} RAM balance in native coin $ramBalanceInNativeCoin stakedInRex ${stakedInRex?.toStringExpanded()} stakedNet ${stakedNet?.toStringExpanded()} stakedCpu ${stakedCpu?.toStringExpanded()} total token balance ${tokenBalance?.toStringExpanded()}")
        val result = coreBalanceValue
            ?.plus(stakedCpu ?: BigDecimal.ZERO)
            ?.plus(stakedNet ?: BigDecimal.ZERO)
            ?.plus(stakedInRex ?: BigDecimal.ZERO)
            ?.plus(ramBalanceInNativeCoin ?: BigDecimal.ZERO)
            ?.plus(tokenBalance ?: BigDecimal.ZERO)

        println("totalValueFormatted $result")

        return result
    }
}