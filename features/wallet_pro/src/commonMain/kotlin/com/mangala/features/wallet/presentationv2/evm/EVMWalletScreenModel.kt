package com.mangala.features.wallet.presentationv2.evm

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.features.wallet.presentationv2.evm.model.EVMFilterOptions
import com.mangala.features.wallet.presentationv2.evm.model.EVMViewMode
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.features.wallet.presentationv2.core.base.BaseWalletViewModel
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.domain.portfolio.model.AccountPortfolio
import com.mangala.wallet.domain.portfolio.usecases.GetAllWalletsPortfolioUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.ShareFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EVMWalletScreenModel(
    private val getAllWalletsPortfolioUseCase: GetAllWalletsPortfolioUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getBalanceVisibleStatusUseCase: GetBalanceVisibleStatusUseCase,
    private val saveBalanceVisibleStatusUseCase: SaveBalanceVisibleStatusUseCase,
    private val saveSelectedNetworkUseCase: SaveSelectedNetworkUseCase,
    private val parseQRCodeResultUseCase: ParseQRCodeResultUseCase,
    private val clipboardFactory: ClipboardFactory,
    private val shareFactory: ShareFactory,
    private val buildEnvironmentProvider: BuildEnvironmentProvider,
) : BaseWalletViewModel() {

    override val networkType = NetworkType.EVM

    private val _uiState = MutableStateFlow(
        EVMWalletUiState(
            accounts = emptyList(),
            fiatSymbol = "",
            isPortfolioBalanceHidden = true,
            isLoadingWallets = true
        )
    )
    val uiState: StateFlow<EVMWalletUiState> = _uiState.asStateFlow()

    private var loadPortfolioJob: Job? = null

    init {
        observeBalanceVisibility()
        observeNetworkAndLoadPortfolio()
    }

    private fun observeBalanceVisibility() {
        screenModelScope.launch {
            getBalanceVisibleStatusUseCase().collectLatest { isBalanceVisible ->
                _uiState.update { state ->
                    state.copy(
                        isPortfolioBalanceHidden = !isBalanceVisible,
                        accounts = state.accounts.map { it.copy(isBalanceVisible = isBalanceVisible) }
                    )
                }
            }
        }
    }

    private fun observeNetworkAndLoadPortfolio() {
        screenModelScope.launch {
            getSelectedNetworkUseCase.invokeFlow().collectLatest { selectedNetwork ->
                if (selectedNetwork.blockchainType.networkType != NetworkType.EVM) return@collectLatest

                loadPortfolioJob?.cancel()
                _uiState.update { state ->
                    state.copy(selectedNetwork = selectedNetwork)
                }

                loadPortfolioJob = screenModelScope.launch {
                    loadPortfolio(forceReload = isInitialLoad, selectedNetwork)
                    isInitialLoad = false
                }
            }
        }
    }

    private suspend fun loadPortfolio(forceReload: Boolean, network: BlockchainNetworkData) {
        val currencyCode = getCurrentCurrencyCodeUseCase()
        val currencySymbol = Currency.valueOf(currencyCode).symbol
        val balanceVisible = getBalanceVisibleStatusUseCase().first()

        getAllWalletsPortfolioUseCase(forceReload, network).collect { resource ->
            _isRefreshing.value = resource is Resource.Loading

            val portfolioData = resource.data ?: return@collect

            val currentSelectedIndex = _uiState.value.selectedAccountIndex
            val validatedSelectedIndex = if (portfolioData.accounts.isEmpty()) {
                0
            } else {
                currentSelectedIndex.coerceIn(0, portfolioData.accounts.lastIndex)
            }

            val accountInfos = portfolioData.accounts.mapIndexed { index, accountPortfolio ->
                mapToAccountInfo(
                    accountPortfolio = accountPortfolio,
                    isActive = index == validatedSelectedIndex,
                    isBalanceVisible = balanceVisible,
                    currencySymbol = currencySymbol
                )
            }

            _uiState.update { state ->
                state.copy(
                    accounts = accountInfos,
                    selectedAccountIndex = validatedSelectedIndex,
                    fiatSymbol = currencySymbol,
                    isLoadingWallets = resource is Resource.Loading,
                    isPortfolioBalanceHidden = !balanceVisible,
                    isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment(),
                    portfolioTotalUsd = portfolioData.totalValueUsd,
                    portfolioPnl = portfolioData.totalPnl,
                    portfolioPnlPercentage = portfolioData.totalPnlPercentage
                )
            }
        }
    }

    private fun mapToAccountInfo(
        accountPortfolio: AccountPortfolio,
        isActive: Boolean,
        isBalanceVisible: Boolean,
        currencySymbol: String
    ): EVMAccountInfo {
        return EVMAccountInfo(
            accountId = accountPortfolio.accountId,
            accountName = accountPortfolio.accountName,
            address = Address(accountPortfolio.address).eip55,
            walletId = accountPortfolio.walletId,
            balances = accountPortfolio.balances,
            isActive = isActive,
            isBalanceVisible = isBalanceVisible,
            currencySymbol = currencySymbol,
            totalValueUsd = accountPortfolio.totalValueUsd,
            pnl = accountPortfolio.pnl,
            yesterdayTotalValue = accountPortfolio.yesterdayTotalValue
        )
    }

    override fun onRefresh() {
        loadPortfolioJob?.cancel()
        loadPortfolioJob = screenModelScope.launch {
            _isRefreshing.value = true
            val network = _uiState.value.selectedNetwork ?: getSelectedNetworkUseCase()
            loadPortfolio(forceReload = true, network)
        }
    }

    override fun onCopyAddress() {
        val activeAccount = _uiState.value.activeAccount ?: return
        val address = activeAccount.address
        if (address.isNotEmpty()) {
            clipboardFactory.copyText("Mangala copy", address)
        }
    }

    override fun onShareAddress() {
        val activeAccount = _uiState.value.activeAccount ?: return
        val address = activeAccount.address
        if (address.isNotEmpty()) {
            shareFactory.shareText("Mangala share via", address)
        }
    }

    fun onTogglePortfolioHideBalance() {
        val currentVisibility = _uiState.value.isPortfolioBalanceHidden
        screenModelScope.launch {
            saveBalanceVisibleStatusUseCase(currentVisibility)
        }
    }

    fun onAccountSelected(accountIndex: Int) {
        _uiState.update { state ->
            val updatedAccounts = state.accounts.mapIndexed { index, account ->
                account.copy(isActive = index == accountIndex)
            }
            state.copy(
                accounts = updatedAccounts,
                selectedAccountIndex = accountIndex
            )
        }
    }

    fun getCurrentAccountId(): String {
        return _uiState.value.activeAccount?.accountId.orEmpty()
    }

    fun getCurrentAddress(): String {
        return _uiState.value.activeAccount?.address.orEmpty()
    }

    fun onScanQrCodeResult(qrCodeData: String): QrCodeData? {
        return parseQRCodeResultUseCase(qrCodeData)
    }

    fun getAvailableNetworks(): List<BlockchainNetworkData> {
        return BlockchainNetworkData.getAllBlockchainNetworkSupported(buildEnvironmentProvider.isDevelopmentEnvironment())
            .filter { it.blockchainType.networkType == NetworkType.EVM }
    }

    fun onNetworkSelected(network: BlockchainNetworkData) {
        screenModelScope.launch {
            saveSelectedNetworkUseCase(network)
        }
    }

    fun onSearchToggled() {
        _uiState.update { state ->
            val newSearchActive = !state.isSearchActive
            state.copy(
                isSearchActive = newSearchActive,
                searchQuery = if (newSearchActive) state.searchQuery else ""
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(searchQuery = query)
        }
    }

    fun onFilterOptionsChanged(filterOptions: EVMFilterOptions) {
        _uiState.update { state ->
            state.copy(filterOptions = filterOptions)
        }
    }
}
