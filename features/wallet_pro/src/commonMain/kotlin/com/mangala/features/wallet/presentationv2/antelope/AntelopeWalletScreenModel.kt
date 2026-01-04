package com.mangala.features.wallet.presentationv2.antelope

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.features.wallet.presentation.toImageSource
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.features.wallet.presentationv2.core.base.BaseWalletViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.mangala.features.wallet.presentationv2.antelope.components.AccountInfo
import com.mangala.features.wallet.presentationv2.antelope.components.FilterOptions
import com.mangala.features.wallet.presentationv2.antelope.components.ViewMode
import com.mangala.wallet.auth.AuthenticationFlowManager
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.domain.portfolio.usecases.GetPortfolioBalanceUseCase
import com.mangala.wallet.domain.portfolio.usecases.GetPortfolioByAccountUseCase
import com.mangala.wallet.domain.portfolio.usecases.SyncPortfolioDataUseCase
import com.mangala.wallet.domain.portfolio.error.PortfolioErrorHandler
import com.mangala.wallet.domain.portfolio.error.PortfolioError
import com.mangala.wallet.domain.portfolio.model.PortfolioDetailResponse
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAllAntelopeAccountsBalancesUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.anchorlink.AnchorLinkSessionManager
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification.RegisterAntelopeNotificationUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification.UnregisterAntelopeNotificationUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.model.provider.coingecko.CoinGeckoTokenPriceModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.utils.AppLifecycleObserver
import com.mangala.wallet.utils.AppLifecycleState
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.ShareFactory
import com.mangala.wallet.utils.ext.bytesToKilobytes
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.toBigDecimalOrNull
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class AntelopeWalletScreenModel(
    private val parseQRCodeResultUseCase: ParseQRCodeResultUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val anchorLinkSessionManager: AnchorLinkSessionManager,
    private val getAllAntelopeAccountsBalancesUseCase: GetAllAntelopeAccountsBalancesUseCase,
    private val registerAntelopeNotificationUseCase: RegisterAntelopeNotificationUseCase,
    private val unregisterAntelopeNotificationUseCase: UnregisterAntelopeNotificationUseCase,
    private val getAntelopeAccountsUseCase: GetAccountsUseCase,
    // Portfolio use cases
    private val getPortfolioBalanceUseCase: GetPortfolioBalanceUseCase,
    private val getPortfolioByAccountUseCase: GetPortfolioByAccountUseCase,
    private val syncPortfolioDataUseCase: SyncPortfolioDataUseCase,
    private val portfolioErrorHandler: PortfolioErrorHandler,
    // Other
    private val clipboardFactory: ClipboardFactory,
    private val shareFactory: ShareFactory,
    private val buildEnvironmentProvider: BuildEnvironmentProvider,
    private val appLifecycleObserver: AppLifecycleObserver,
    private val getBalanceVisibleStatusUseCase: GetBalanceVisibleStatusUseCase,
    private val saveBalanceVisibleStatusUseCase: SaveBalanceVisibleStatusUseCase,
    private val saveSelectedNetworkUseCase: SaveSelectedNetworkUseCase,
    private val authFlowManager: AuthenticationFlowManager,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
) : BaseWalletViewModel() {
    
    override val networkType = NetworkType.ANTELOPE

    private val _uiState = MutableStateFlow(
        AntelopeWalletUiState(
            accountName = "",
            fiatSymbol = "",
            coinGeckoExchangeRate = null,
            isSingleAccountMode = false,
            isLoadingWallets = true,
            portfolioAccounts = emptyList(),
            isPortfolioBalanceHidden = true,
        )
    )
    val uiState: StateFlow<AntelopeWalletUiState> = _uiState.asStateFlow()

    private var scannedAddressFromQrCode: String? = null
    private var loadBalanceJob: Job? = null

    init {
        observeBalanceVisibility()
        observeNetworkChanges()
    }

    private fun loadDataIfChanged(network: BlockchainNetworkData?, forceReload: Boolean) {
        screenModelScope.launch {
            if ((authFlowManager.hasSession().not())) {
                loadBalanceFromNode(network, forceReload)
            } else {
                network?.let { loadBalanceFromPortfolio(it) }
            }
        }

        initializeNotifications()
        initializeAnchorLinkSession()
    }

    private suspend fun loadBalanceFromPortfolio(blockchainNetworkData: BlockchainNetworkData) {
        getPortfolioBalanceUseCase.invokeFlow().collectLatest { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            portfolioSyncStatus = PortfolioSyncStatus.Syncing,
                            isLoadingWallets = true
                        )
                    }
                }

                is Resource.Success -> {
                    resource.data?.let { data ->
                        updateUIStateWithPortfolioData(data, blockchainNetworkData)
                    }
                    _uiState.update {
                        it.copy(
                            portfolioSyncStatus = PortfolioSyncStatus.Success,
                            isPortfolioDataAvailable = true,
                            portfolioError = null,
                            isLoadingWallets = false
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isLoadingWallets = true) }
                    handlePortfolioError(resource.exception, blockchainNetworkData)
                }
            }
        }
    }

    private suspend fun loadBalanceFromNode(
        network: BlockchainNetworkData?,
        forceReload: Boolean
    ) {
        val networkSelected = network ?: getSelectedNetworkUseCase()
        val currentNetwork = _uiState.value.selectedNetwork
        val networkTypeChanged =
            networkSelected.blockchainType.networkType != currentNetwork?.blockchainType?.networkType
        val newBlockchainType = networkSelected.blockchainType
        val newNetworkType = newBlockchainType.networkType
        val networkChanged = networkSelected != currentNetwork

        val currencyCode = getCurrentCurrencyCodeUseCase()
        val currencySymbol = Currency.valueOf(currencyCode).symbol

        val shouldForceReload = forceReload || isInitialLoad || networkChanged

        if (shouldForceReload) {
            isInitialLoad = false
        }

        collectAntelopeBalanceNew(
            networkSelected,
            shouldForceReload,
            currencySymbol
        )
    }

    private fun observeBalanceVisibility() {
        screenModelScope.launch {
            getBalanceVisibleStatusUseCase().collectLatest { isBalanceVisible ->
                println("AntelopeWalletScreenModel observeBalanceVisibility isBalanceVisible: $isBalanceVisible")
                _uiState.update { state ->
                    state.copy(isPortfolioBalanceHidden = !isBalanceVisible)
                }
            }
        }
    }
    
    private fun observeNetworkChanges() {
        screenModelScope.launch {
            getSelectedNetworkUseCase.invokeFlow().collectLatest { selectedNetwork ->
                loadBalanceJob?.cancel()
                _uiState.update { state ->
                    state.copy(selectedNetwork = selectedNetwork)
                }
                loadBalanceJob = screenModelScope.launch {
                    when (networkType) {
                        NetworkType.ANTELOPE -> {
                            // Only reload data if the list of account name changes, ignoring balance changes
                            getAntelopeAccountsUseCase.invokeWithBasicInfo(
                                includeTempAccounts = true,
                                includeIapInitializedAccounts = true
                            ).collectLatest { data ->
                                println("AntelopeWalletScreenModel observeNetworkChanges account list changed $data" )
                                loadDataIfChanged(forceReload = false, network = selectedNetwork)
                            }
                        }

                        NetworkType.EVM -> {
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }
    
    override fun onRefresh() {
        lifecycleScope.launch {
            _isRefreshing.value = true
            // TODO: Implement refresh logic
            kotlinx.coroutines.delay(2000) // Simulate network delay
//            loadWalletData()
            _isRefreshing.value = false
        }
    }
    
    override fun onCopyAddress() {
        val accountName = _uiState.value.accountName
        if (accountName.isNotEmpty()) {
            clipboardFactory.copyText("Mangala copy", accountName)
        }
    }
    
    override fun onShareAddress() {
        val accountName = _uiState.value.accountName
        if (accountName.isNotEmpty()) {
            shareFactory.shareText("Mangala share via", accountName)
        }
    }

    fun onSelectCurrency(currency: AntelopeAccountBalanceUnit) {
        _uiState.update { state ->
            state.copy(selectedCoreBalanceUnit = currency)
        }
    }

    fun getCurrentAccountId(): String {
        val accounts = _uiState.value.portfolioAccounts
        val selectedAccount = accounts.find { it.isActive }

        val accountId = selectedAccount?.accountName.orEmpty()

        return accountId
    }
    
    fun onTogglePortfolioHideBalance() {
        val currentVisibility = _uiState.value.isPortfolioBalanceHidden
        println("AntelopeWalletScreenModel onTogglePortfolioHideBalance $currentVisibility")
        screenModelScope.launch {
            saveBalanceVisibleStatusUseCase(currentVisibility)
        }
    }
    
    fun onPortfolioCurrencyClick() {
        // TODO: Show currency selector
    }
    
    fun onToggleTokenView() {
        _uiState.update { state ->
            val currentState = state.filterOptions.viewMode
            val newState = if (currentState == ViewMode.ALL_ACCOUNTS) {
                ViewMode.SINGLE_ACCOUNT
            } else {
                ViewMode.ALL_ACCOUNTS
            }

            state.copy(filterOptions = state.filterOptions.copy(viewMode = newState))
        }
    }
    
    fun onFilterOptionsChanged(filterOptions: FilterOptions) {
        _uiState.update { state ->
            state.copy(
                filterOptions = filterOptions,
            )
        }
    }
    
    fun onToggleAccountMode() {
        _uiState.update { state ->
            val newMode = !state.isSingleAccountMode

            state.copy(isSingleAccountMode = newMode)
        }
    }
    
    fun onAccountSelected(accountName: String) {
        _uiState.update { state ->
            val updatedAccounts = state.portfolioAccounts.map { account ->
                account.copy(isActive = account.accountName == accountName)
            }
            
            val selectedAccount = updatedAccounts.find { it.isActive }
            
            state.copy(
                portfolioAccounts = updatedAccounts,
                accountName = accountName,
                totalBalance = selectedAccount?.balance ?: BigDecimal.ZERO,
                usdEquivalent = selectedAccount?.usdValue ?: BigDecimal.ZERO,
                pnlAmount = selectedAccount?.pnlAmount ?: BigDecimal.ZERO,
                pnlPercentage = selectedAccount?.pnlPercentage ?: BigDecimal.ZERO,
            )
        }
    }
    
    fun getAvailableNetworks(): List<BlockchainNetworkData> {
        return BlockchainNetworkData.getAllBlockchainNetworkSupported(buildEnvironmentProvider.isDevelopmentEnvironment())
            .filter { it.blockchainType.networkType == NetworkType.ANTELOPE }
    }
    
    fun onNetworkSelected(network: BlockchainNetworkData) {
        screenModelScope.launch {
            saveSelectedNetworkUseCase(network)
        }
    }
    
    fun onScanQrCodeResult(qrCodeData: String): QrCodeData? {
        val qrCodeResult = parseQRCodeResultUseCase(qrCodeData)

        if (qrCodeResult is QrCodeData.Payment) {
            scannedAddressFromQrCode = qrCodeResult.address
        }

        return qrCodeResult
    }
    
    private fun initializeNotifications() {
        screenModelScope.launch {
            val antelopeBlockchainTypeSequence = BlockchainNetworkData
                .getAllBlockchainNetworkSupported(buildEnvironmentProvider.isDevelopmentEnvironment())
                .asSequence()
                .filter { it.blockchainType.networkType == NetworkType.ANTELOPE }
            val listRetryRegisterDeferred = antelopeBlockchainTypeSequence
                .map {
                    async {
                        registerAntelopeNotificationUseCase.retryAllRegisterNotification(it.blockchainType)
                    }
                }
                .toList()
            val listRetryUnregisterDeferred = antelopeBlockchainTypeSequence
                .map {
                    async {
                        unregisterAntelopeNotificationUseCase.retryAllUnregisterNotification(it.blockchainType)
                    }
                }
                .toList()

            listRetryRegisterDeferred.awaitAll()
            listRetryUnregisterDeferred.awaitAll()
        }
    }

    private fun initializeAnchorLinkSession() {
        screenModelScope.launch {
            appLifecycleObserver.appLifecycleStateFlow.collectLatest {
                if (it == AppLifecycleState.FOREGROUND) {
                    anchorLinkSessionManager.initialize()
                }
            }
        }
    }
    
    fun onSearchToggled() {
        _uiState.update { state ->
            val newIsSearchActive = !state.isSearchActive
            state.copy(
                isSearchActive = newIsSearchActive,
                searchQuery = if (!newIsSearchActive) "" else state.searchQuery,
            )
        }
    }
    
    fun onSearchQueryChanged(query: String) {
        _uiState.update { state -> state.copy(searchQuery = query) }
    }
    
    private fun getCurrentUserId(): String {
        // TODO: Implement based on your auth system
        // This might come from a user session, wallet ID, or auth token
        return "user123" // Placeholder
    }

    private fun calculateUsdValue(balance: Double, price: Double): Double {
        return balance * price
    }

    private fun updateUIStateWithAccounts(
        portfolioAccounts: List<AccountInfo>,
        blockchainNetworkData: BlockchainNetworkData,
        isLoading: Boolean = false
    ) {
        // Set first account as active if available
        val updatedPortfolioAccounts = if (portfolioAccounts.isNotEmpty()) {
            portfolioAccounts.mapIndexed { index, account ->
                account.copy(isActive = index == 0)
            }
        } else {
            portfolioAccounts
        }

        val activeAccount = updatedPortfolioAccounts.firstOrNull { it.isActive }

        _uiState.update { state ->
            state.copy(
                accountName = activeAccount?.accountName ?: "",
                isConnected = true,
                notificationCount = 3,
                totalBalance = activeAccount?.balance ?: BigDecimal.ZERO,
                usdEquivalent = activeAccount?.usdValue ?: BigDecimal.ZERO,
                pnlPercentage = activeAccount?.pnlPercentage ?: BigDecimal.ZERO,
                pnlAmount = activeAccount?.pnlAmount ?: BigDecimal.ZERO,
                isBalanceHidden = false,
                portfolioAccounts = updatedPortfolioAccounts,
                isSingleAccountMode = updatedPortfolioAccounts.size <= 1,
                liquidBalance = 10247.89,
                stakedBalance = 3000.0,
                rexBalance = 2000.0,
                cpuUsed = 45,
                cpuMax = 100,
                netUsed = 30,
                netMax = 100,
                ramUsed = 65,
                ramMax = 100,
                ramPrice = 0.0234,
                ramPriceChange = 3.45,
                rexMaturityDate = Clock.System.now(),
                rexEstimatedApr = 4.25,
                selectedNetwork = blockchainNetworkData,
            )
        }
    }

    private fun updateUIStateWithPortfolioData(
        portfolioData: PortfolioDetailResponse,
        networkData: BlockchainNetworkData
    ) {
        val portfolioAccounts = portfolioData.portfolio.accounts.map { account ->
            Napier.d(tag = "AntelopeWalletUiState", message = "balance in VM updateUIStateWithPortfolioData " + account.totals.balanceUsdt)
            AccountInfo(
                accountName = account.label,
                balance = account.totals.balanceUsdt,
                pnlAmount = account.totals.pnl24hUsdt,
                pnlPercentage = account.totals.pnl24hPercent,
                isActive = false
            )
        }

        val updatedPortfolioAccounts = if (portfolioAccounts.isNotEmpty()) {
            portfolioAccounts.mapIndexed { index, account ->
                account.copy(isActive = index == 0)
            }
        } else {
            portfolioAccounts
        }

        val activeAccount = updatedPortfolioAccounts.firstOrNull { it.isActive }

        _uiState.update { state ->
            state.copy(
                accountName = activeAccount?.accountName ?: "",
                isConnected = true,
                totalBalance = portfolioData.portfolio.totals.balanceUsdt,
                usdEquivalent = portfolioData.portfolio.totals.balanceUsdt,
                pnlPercentage = portfolioData.portfolio.totals.pnl24hPercent,
                pnlAmount = portfolioData.portfolio.totals.pnl24hUsdt,
                portfolioAccounts = updatedPortfolioAccounts,
                isSingleAccountMode = updatedPortfolioAccounts.size <= 1,
                selectedNetwork = networkData,
            )
        }
    }

    private fun loadAccountsAsFallback(blockchainNetworkData: BlockchainNetworkData) {
        screenModelScope.launch {
            // Use the same pattern as the main loading with fallback behavior
            getAllAntelopeAccountsBalancesUseCase(
                forceReload = false,
                network = blockchainNetworkData
            ).collectLatest { data ->
                val loadedAccounts = data.data?.accounts ?: emptyList()

                val portfolioAccounts = if (loadedAccounts.isNotEmpty()) {
                    loadedAccounts.map { accountWithBalance ->
                        val balanceDetails = accountWithBalance.balance.data
                        val coreBalance = balanceDetails?.coreBalance

                        Napier.d(tag = "AntelopeWalletUiState", message = "balance in VM loadAccountsAsFallback " + coreBalance?.amount?.toBigDecimal())
                        AccountInfo(
                            accountName = accountWithBalance.account.accountName,
                            balance = coreBalance?.amount?.toBigDecimal(),
                            isActive = false
                        )
                    }
                } else {
                    emptyList()
                }

                updateUIStateWithAccounts(
                    portfolioAccounts,
                    blockchainNetworkData,
                    data.isLoading()
                )
            }
        }
    }

    private suspend fun handlePortfolioError(exception: Throwable, networkData: BlockchainNetworkData) {
        val portfolioError = portfolioErrorHandler.mapToPortfolioError(exception)

        val syncStatus = when (portfolioError) {
            is PortfolioError.NetworkError -> PortfolioSyncStatus.NetworkError
            is PortfolioError.AuthenticationError -> PortfolioSyncStatus.AuthError
            is PortfolioError.PortfolioNotFound -> PortfolioSyncStatus.PortfolioNotFound
            is PortfolioError.TimeoutError -> PortfolioSyncStatus.NetworkError
            is PortfolioError.ServerError -> PortfolioSyncStatus.Error
            else -> PortfolioSyncStatus.Error
        }

        _uiState.update { state ->
            state.copy(
                portfolioSyncStatus = syncStatus,
                portfolioError = portfolioError.getUserMessage(),
                isPortfolioDataAvailable = false
            )
        }

        // Attempt fallback if the error allows it
        if (portfolioError.shouldFallbackToLegacy()) {
            loadBalanceFromNode(networkData, false)
        }
    }

    /**
     * Retry portfolio loading with error handling
     */
    fun onRetryPortfolioSync() {
        val currentNetwork = _uiState.value.selectedNetwork ?: return

        screenModelScope.launch {
            _uiState.update { it.copy(portfolioSyncStatus = PortfolioSyncStatus.Retrying) }

            // Use error handler with retry mechanism
            val result = portfolioErrorHandler.executeWithRetry(
                operation = {
                    // Return a success indicator since loadWalletData handles UI updates
                    loadDataIfChanged(forceReload = true, network = currentNetwork)
                    true // Return success
                },
                shouldRetry = { exception ->
                    // Let the error handler decide if we should retry
                    portfolioErrorHandler.mapToPortfolioError(exception).isRetryable()
                }
            )

            result.onFailure { error ->
                handlePortfolioError(error, currentNetwork)
            }
        }
    }

    fun onManualPortfolioSync() {
        val currentNetwork = _uiState.value.selectedNetwork ?: return

        _uiState.update {
            it.copy(
                portfolioSyncStatus = PortfolioSyncStatus.Syncing,
                portfolioError = null
            )
        }

        loadDataIfChanged(forceReload = true, network = currentNetwork)
    }

    private suspend fun collectAntelopeBalanceNew(
        blockchainNetworkData: BlockchainNetworkData,
        forceReload: Boolean,
        currencySymbol: String
    ) {
        Napier.d(tag = "AntelopeWalletUiState", message = "collectAntelopeBalanceNew")
        getAllAntelopeAccountsBalancesUseCase(
            forceReload,
            blockchainNetworkData
        ).collectLatest { data ->
            Napier.d(tag = "AntelopeWalletUiState", message = "collectAntelopeBalanceNew collectLatest")
            println("WalletMainScreenModel collectAntelopeBalanceNew $data")
            val balanceVisible = getBalanceVisibleStatusUseCase().first()

            if (data.isLoading()) {
                _uiState.update { it.copy(isLoadingWallets = true) }
            }

            _uiState.update { state ->
                val loadedAccounts = data.data?.accounts ?: return@update state.copy(isLoadingWallets = false)

                val antelopeAccounts = loadedAccounts.mapIndexed { index, it ->
                    val nativeTokenPrice = data.data?.nativeTokenPrice
                    val nativeCoinPrice = nativeTokenPrice?.currentPrice?.toBigDecimal().orZero()

                    val tokens = it.balance.data?.let { balanceData ->
                        val nativeToken = listOf(
                            TokenUiState(
                                balanceData.coreBalance?.symbol.orEmpty(),
                                balanceData.coreBalance?.symbol.orEmpty(),
                                balanceData.coreBalance?.precision?.toLong() ?: 4,
                                balanceData.coreBalance?.amount?.toBigDecimal(),
                                nativeTokenPrice?.priceChangePercentage24h?.toBigDecimal().orZero(),
                                ImageSource.Resource(MR.images.eos_new),
                                "",
                                nativeCoinPrice = nativeCoinPrice,
                                priceInNativeCoin = BigDecimal.ONE
                            )
                        )
                        val tokensList = balanceData.tokens?.map { token ->
                            TokenUiState(
                                token.symbol,
                                token.metadata.name,
                                token.decimals.toLong(),
                                token.amount.toBigDecimal(),
                                if (token.symbol == "A") nativeTokenPrice?.priceChangePercentage24h?.toBigDecimalOrNull().orZero() else BigDecimal.ZERO,
                                when {
                                    token.symbol == "A" -> ImageSource.Resource(MR.images.vaulta)
                                    token.symbol == "V" && blockchainNetworkData.blockchainType.isEosNetwork() -> ImageSource.Resource(MR.images.vaultram)
                                    else -> token.metadata.toImageSource()
                                },
                                token.contract,
                                nativeCoinPrice,
                                priceInNativeCoin = if (token.symbol == "A") BigDecimal.ONE else token.exchanges.firstOrNull()?.price?.toBigDecimal() ?: BigDecimal.ZERO
                            )
                        }.orEmpty()
                        nativeToken + tokensList
                    }

                    Napier.d(tag = "AntelopeWalletUiState", message = "balance in VM collectAntelopeBalanceNew " + it.balance.data?.coreBalance?.amount?.toBigDecimal())
                    AccountInfo(
                        it.account.accountName,
                        balance = it.balance.data?.coreBalance?.amount?.toBigDecimal(),
                        tokens = tokens,
                        isActive = index == 0
                    )
                }

                val newUiState =
                    getInitialAntelopeDataUiState(
                        antelopeAccounts = antelopeAccounts,
                        blockchainNetworkData,
                        currencySymbol,
                        data.data?.nativeCoinExchangeRate,
                        isPortfolioBalanceHidden = state.isPortfolioBalanceHidden
                    )

                _isRefreshing.value = data.isLoading()

                return@update newUiState
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
        val result = coreBalanceValue
            ?.plus(stakedCpu ?: BigDecimal.ZERO)
            ?.plus(stakedNet ?: BigDecimal.ZERO)
            ?.plus(stakedInRex ?: BigDecimal.ZERO)
            ?.plus(ramBalanceInNativeCoin ?: BigDecimal.ZERO)
            ?.plus(tokenBalance ?: BigDecimal.ZERO)

        return result
    }

    private suspend fun getInitialAntelopeDataUiState(
        antelopeAccounts: List<AccountInfo>,
        blockchainNetworkData: BlockchainNetworkData,
        currencySymbol: String,
        coinGeckoExchangeRate: CoinGeckoTokenPriceModel?,
        isPortfolioBalanceHidden: Boolean
    ) = AntelopeWalletUiState(
        selectedNetwork = blockchainNetworkData,
        isBalanceHidden = !getBalanceVisibleStatusUseCase().first(),
        portfolioAccounts = antelopeAccounts,
        accountName = antelopeAccounts.firstOrNull()?.accountName.orEmpty(),
        fiatSymbol = currencySymbol,
        coinGeckoExchangeRate = coinGeckoExchangeRate,
        isSingleAccountMode = antelopeAccounts.size <= 1,
        isLoadingWallets = false,
        isPortfolioBalanceHidden = isPortfolioBalanceHidden,
        isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()
    )
}