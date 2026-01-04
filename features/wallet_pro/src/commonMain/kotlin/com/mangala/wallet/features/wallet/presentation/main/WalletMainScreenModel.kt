package com.mangala.wallet.features.wallet.presentation.main

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.features.wallet.presentation.AntelopeAccountItemUiModel
import com.mangala.features.wallet.presentation.AntelopeAssetsUiModel.RamBalanceUiModel
import com.mangala.features.wallet.presentation.AntelopeAssetsUiModel.TokenBalanceUiModel
import com.mangala.features.wallet.presentation.BaseWalletMainScreenDataUiState
import com.mangala.features.wallet.presentation.BaseWalletMainScreenModel
import com.mangala.features.wallet.presentation.BitcoinAccountItemUiModel
import com.mangala.features.wallet.presentation.EvmAccountItemUiModel
import com.mangala.features.wallet.presentation.toImageSource
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AntelopeSymbolUtils
import com.mangala.wallet.domain.account.usecases.GetAllAccountBalancesInEvmWalletUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAllAntelopeAccountsBalancesUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.anchorlink.AnchorLinkSessionManager
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification.RegisterAntelopeNotificationUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification.UnregisterAntelopeNotificationUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetSelectedWalletBitcoinAccountsUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.utils.AppLifecycleObserver
import com.mangala.wallet.utils.AppLifecycleState
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.ISystemInfoManager
import com.mangala.wallet.utils.MANGALA_SUPPORT_EMAIL
import com.mangala.wallet.utils.MailToFactory
import com.mangala.wallet.utils.ext.bytesToKilobytes
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.toBigDecimalOrNull
import com.mmk.kmpnotifier.notification.NotifierManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class WalletMainScreenModel(
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    // EVM
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    private val getAllAccountBalancesInEvmWalletUseCase: GetAllAccountBalancesInEvmWalletUseCase,
    // Antelope
    private val anchorLinkSessionManager: AnchorLinkSessionManager,
    private val getAllAntelopeAccountsBalancesUseCase: GetAllAntelopeAccountsBalancesUseCase,
    registerAntelopeNotificationUseCase: RegisterAntelopeNotificationUseCase,
    unregisterAntelopeNotificationUseCase: UnregisterAntelopeNotificationUseCase,
    private val getAntelopeAccountsUseCase: GetAccountsUseCase,
    // Bitcoin
    private val getSelectedWalletBitcoinAccountsUseCase: GetSelectedWalletBitcoinAccountsUseCase,
    // Other
    private val saveBalanceVisibleStatusUseCase: SaveBalanceVisibleStatusUseCase,
    private val getBalanceVisibleStatusUseCase: GetBalanceVisibleStatusUseCase,
    private val parseQRCodeResultUseCase: ParseQRCodeResultUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val clipboardFactory: ClipboardFactory,
    private val mailToFactory: MailToFactory,
    private val appLifecycleObserver: AppLifecycleObserver,
    buildEnvironmentProvider: BuildEnvironmentProvider,
    private val systemInfoManager: ISystemInfoManager
) : BaseWalletMainScreenModel() {

    private val _uiState =
        MutableStateFlow<WalletMainScreenUiState>(WalletMainScreenUiState.Loading)
    val uiState: StateFlow<WalletMainScreenUiState> get() = _uiState

    private var balanceVisible = true
    private var loadBalanceJob: Job? = null
    private var scannedAddressFromQrCode: String? = null

    val anchorLinkIncomingSignRequests = anchorLinkSessionManager.signRequest

    init {
        screenModelScope.launch {
            val token = try {
                NotifierManager.getPushNotifier().getToken()
            } catch (e: Exception) {
                null
            }
            println("WalletMainScreenModel token $token")
        }
        screenModelScope.launch {
            appLifecycleObserver.appLifecycleStateFlow.collectLatest {
                if (it == AppLifecycleState.FOREGROUND) {
                    anchorLinkSessionManager.initialize()
                }
            }
        }
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
        screenModelScope.launch {
            getSelectedNetworkUseCase.invokeFlow().collectLatest { network ->
                val networkType = network.blockchainType.networkType

                loadBalanceJob?.cancel()
                loadBalanceJob = screenModelScope.launch {
                    when (networkType) {
                        NetworkType.ANTELOPE -> {
                            getAntelopeAccountsUseCase.invokeFlow(includeTempAccounts = true, includeIapInitializedAccounts = true).collectLatest { _ ->
                                loadDataIfChanged(forceReload = false, this, network)
                            }
                        }
                        NetworkType.EVM -> {
                            getSelectedWalletAccountsUseCase.invokeFlow().collectLatest {
                                loadDataIfChanged(forceReload = false, this, network)
                            }
                        }
                        else -> {
                            loadDataIfChanged(forceReload = false, this, network)
                        }
                    }
                }
            }
        }
    }

    override fun doOnComposableStarted() {
        lifecycleScope.launch {
            collectBalanceVisibleStatus()
        }
    }

    fun onPullToRefresh() {
        loadBalanceJob?.cancel()
        loadBalanceJob = screenModelScope.launch {
            _isRefreshing.value = true
            loadDataIfChanged(forceReload = true, this)
        }
    }

    fun getSelectedAccountName(): String? {
        val antelopeData =
            (uiState.value as? WalletMainScreenUiState.AntelopeData) ?: return null

        return antelopeData.selectedAccount?.account?.accountName
    }

    fun getSelectedAccount(): AntelopeAccount? {
        val antelopeData =
            (uiState.value as? WalletMainScreenUiState.AntelopeData) ?: return null

        return antelopeData.selectedAccount?.account
    }

    private suspend fun loadDataIfChanged(forceReload: Boolean, coroutineScope: CoroutineScope, network: BlockchainNetworkData? = null) {
        println("WalletMainScreenModel loadDataIfChanged forceReload $forceReload")
        val networkSelected = network ?: getSelectedNetworkUseCase()
        val currentNetwork =
            (_uiState.value as? BaseWalletMainScreenDataUiState<*>)?.networkSelected
        val networkTypeChanged =
            networkSelected.blockchainType.networkType != currentNetwork?.blockchainType?.networkType
        val newBlockchainType = networkSelected.blockchainType
        val newNetworkType = newBlockchainType.networkType
        val networkChanged = if (!networkTypeChanged) {
            _uiState.update {
                when (it) {
                    is WalletMainScreenUiState.EvmData -> {
                        it.copy(networkSelected = networkSelected)
                    }

                    is WalletMainScreenUiState.AntelopeData -> {
                        it.copy(networkSelected = networkSelected)
                    }

                    is WalletMainScreenUiState.NoWallet -> {
                        it.copy(networkSelected = networkSelected)
                    }

                    is WalletMainScreenUiState.BitcoinData -> {
                        it.copy(networkSelected = networkSelected)
                    }

                    else -> {
                        throw UnsupportedOperationException("Network change not handled for $networkSelected")
                    }
                }
            }
            networkSelected != currentNetwork
        } else {
            true
        }

        val currencyCode = getCurrentCurrencyCodeUseCase.invoke()
        val currencySymbol = Currency.valueOf(currencyCode).symbol

        when (newNetworkType) {
            NetworkType.EVM -> {
                val accountsChanged =
                    checkAndLoadEvmSelectedWalletAccounts(networkSelected, currencySymbol)

                if (forceReload || isInitialLoad || accountsChanged || networkChanged) {
                    isInitialLoad = false
                    collectEvmBalanceForAllAccounts(forceReload)
                }
            }

            NetworkType.ANTELOPE -> {
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

            NetworkType.BITCOIN -> {
                val shouldForceReload = forceReload || isInitialLoad || networkChanged

                if (shouldForceReload) {
                    isInitialLoad = false
                }

                collectBitcoinBalance(
                    networkSelected,
                    shouldForceReload,
                    currencySymbol
                )
            }

            else -> throw UnsupportedOperationException("Network type $newNetworkType not supported")
        }
    }

    fun toggleBalanceVisible(showBalance: Boolean) {
        screenModelScope.launch {
            saveBalanceVisibleStatusUseCase(showBalance)
        }
    }

    fun onAccountChange(index: Int) {
        Napier.d(tag = "LinkDebug") { "onAccountChange $index" }
        _uiState.update {
            when (it) {
                is WalletMainScreenUiState.EvmData -> it.copy(selectedAccountIndex = index)
                is WalletMainScreenUiState.AntelopeData -> it.copy(selectedAccountIndex = index)
                is WalletMainScreenUiState.BitcoinData -> it.copy(selectedAccountIndex = index)
                else -> throw UnsupportedOperationException("Account change for $it is not handled")
            }
        }
    }

    override fun getCurrentAccountId(): String {
        val accountId =
            when (_uiState.value) {
                is WalletMainScreenUiState.EvmData -> {
                    (_uiState.value as WalletMainScreenUiState.EvmData).accounts.getOrNull(
                        (_uiState.value as WalletMainScreenUiState.EvmData).selectedAccountIndex
                    )?.account?.account?.id.orEmpty()
                }

                is WalletMainScreenUiState.AntelopeData -> {
                    (_uiState.value as WalletMainScreenUiState.AntelopeData).accounts.getOrNull(
                        (_uiState.value as WalletMainScreenUiState.AntelopeData).selectedAccountIndex
                    )?.account?.accountName.orEmpty()
                }

                else -> ""
            }
        return accountId
    }

    override fun onScanQrCodeResult(qrCodeData: String): QrCodeData? {
        val qrCodeResult = parseQRCodeResultUseCase(qrCodeData)

        if (qrCodeResult is QrCodeData.Payment) {
            scannedAddressFromQrCode = qrCodeResult.address
        }

        return qrCodeResult
    }

    fun onClickCopy() {
        val it = _uiState.value

        when (it) {
            is WalletMainScreenUiState.EvmData -> {
                val selectedAccount = it.accounts.getOrNull(it.selectedAccountIndex) ?: return
                val address =
                    Address(selectedAccount.account.bip44Address).eip55 // TODO: Determine type of address copied

                // Todo hard code
                clipboardFactory.copyText("Mangala copy", address)
            }

            is WalletMainScreenUiState.AntelopeData -> {
                val selectedAccount = it.accounts.getOrNull(it.selectedAccountIndex) ?: return
                val address = selectedAccount.account.accountName

                clipboardFactory.copyText("Mangala copy", address)
            }

            is WalletMainScreenUiState.BitcoinData -> {
                val selectedAccount = it.accounts.getOrNull(it.selectedAccountIndex) ?: return
                val address = selectedAccount.account.bip84Address // TODO: Determine type of address copied

                clipboardFactory.copyText("Mangala copy", address)
            }

            else -> TODO()
        }
    }

    fun onClickContactSupport() {
        mailToFactory.mailTo(
            MANGALA_SUPPORT_EMAIL,
            "Create account support", // TODO: Localization?
            ""
        )
    }

    private suspend fun collectAntelopeBalanceNew(
        blockchainNetworkData: BlockchainNetworkData,
        forceReload: Boolean,
        currencySymbol: String
    ) {
        getAllAntelopeAccountsBalancesUseCase(forceReload, blockchainNetworkData).collectLatest { data ->

            println("WalletMainScreenModel collectAntelopeBalanceNew $data")

            _uiState.update {
                val loadedAccounts = data.data?.accounts ?: return@update it

                val accounts = loadedAccounts.map {
                    val blockchainType = blockchainNetworkData.blockchainType

                    val nativeTokenPrice = data.data?.nativeTokenPrice
                    val nativeCoinPriceChange24h =
                        nativeTokenPrice?.priceChangePercentage24h.orEmpty()
                    val nativeCoinPnl = if (nativeCoinPriceChange24h.isBlank()) {
                        null
                    } else {
                        BigDecimal.parseString(nativeCoinPriceChange24h).scale(2)
                    }
                    val nativeCoinPrice = nativeTokenPrice?.currentPrice?.toBigDecimalOrNull()
                    val tokenBalances = it.balance.data?.tokens?.map { token ->
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
                    }
                    val coreBalance = it.balance.data?.coreBalance
                    val totalValueInNativeCoin = getAntelopeTotalAccountValue(
                        it.balance.data?.account?.ramQuota,
                        data.data?.ramPrice?.price,
                        coreBalance?.amount?.toBigDecimal(),
                        it.balance.data?.stakedCpu,
                        it.balance.data?.stakedNet,
                        it.balance.data?.stakedInRex,
                        tokenBalances?.map { it.priceInNativeCoin?.times(it.balance.orZero()).orZero() }
                            ?.reduceOrNull { acc, value -> acc.plus(value) }
                    )

                    val tokenBalanceItems = tokenBalances ?: List(5) {
                        TokenBalanceUiModel(
                            key = "$KEY_PLACEHOLDER${blockchainType.uid}AntelopeToken$it",
                            sparkline = null,
                            name = "",
                            symbol = "",
                            balance = null,
                            null,
                            null,
                            null, null, ""
                        )
                    }

                    val assets = listOf(
                        // Add item for RAM
                        RamBalanceUiModel(
                            key = "${blockchainType.uid}AntelopeRam",
                            sparkline = data.data?.ramChart?.getSparklineData() ?: emptyList(),
                            balance = it.balance.data?.account?.ramQuota?.toBigDecimal(),
                            ramUsed = it.balance.data?.account?.ramUsage?.toBigDecimal() ?: BigDecimal.ZERO,
                            priceChangePercentage24h = data.data?.ramChart
                                ?.getPriceChangePercentage24h()?.toBigDecimal(),
                            ramPriceInKilobytes = data.data?.ramPrice?.price,
                            nativeCoinSymbol = AntelopeSymbolUtils.formatSymbol(coreBalance?.symbol.orEmpty(), blockchainType),
                            accountName = it.account.accountName,
                            blockchainType = blockchainType
                        ),
                        // Add item for native coin
                        TokenBalanceUiModel(
                            key = "${blockchainType.uid}${data.data?.nativeCoin?.coinUid}AntelopeNativeCoin",
                            name = coreBalance?.symbol.orEmpty(),
                            symbol = coreBalance?.symbol.orEmpty(),
                            balance = coreBalance?.amount?.toBigDecimal(),
                            priceInNativeCoin = BigDecimal.ONE,
                            nativeCoinPrice = nativeCoinPrice,
                            iconResource = ImageSource.Resource(MR.images.eos_new),
                            fiatSymbol = currencySymbol,
                            sparkline = nativeTokenPrice?.sparklineIn7d?.price,
                            priceChangePercentage24h = nativeTokenPrice?.priceChangePercentage24h?.toBigDecimalOrNull()
                        )
                    ) + tokenBalanceItems


                    AntelopeAccountItemUiModel(
                        account = it.account,
                        isBalanceVisible = balanceVisible,
                        currencySymbol = currencySymbol,
                        coreBalanceValue = coreBalance?.amount?.toBigDecimal(),
                        coreBalanceSymbol = blockchainType.getNativeTokenSymbol(),
                        cpuUsagePercentage = it.balance.data?.cpuUsagePercentage,
                        netUsagePercentage = it.balance.data?.netUsagePercentage,
                        ramBalanceBytes = it.balance.data?.account?.ramQuota,
                        ramPriceKilobytes = data.data?.ramPrice?.price,
                        stakedCpu = it.balance.data?.stakedCpu,
                        stakedNet = it.balance.data?.stakedNet,
                        stakedInRex = it.balance.data?.stakedInRex,
                        nativeCoinPnl = nativeCoinPnl,
                        nativeCoinPrice = nativeCoinPrice,
                        totalValueInNativeCoin = totalValueInNativeCoin,
                        assets = assets,
                        coinGeckoExchangeRate = data.data?.nativeCoinExchangeRate,
                        isLoading = data.isLoading()
                    )
                }
                val newUiState = getInitialAntelopeDataUiState(antelopeAccounts = accounts, blockchainNetworkData, currencySymbol)

                _isRefreshing.value = data.isLoading()

                return@update newUiState
            }
        }
    }

    fun onChangeAntelopeBalanceUnit(unit: AntelopeAccountBalanceUnit) {
        _uiState.update {
            when (it) {
                is WalletMainScreenUiState.AntelopeData -> {
                    it.copy(accounts = it.accounts.map { account ->
                        account.copy(selectedCoreBalanceUnit = unit)
                    })
                }

                else -> it
            }
        }
    }

    private suspend fun collectBalanceVisibleStatus() {
        getBalanceVisibleStatusUseCase().collectLatest { showBalance ->
            balanceVisible = showBalance
            _uiState.update { uiState ->
                updateBalanceVisibility(uiState, showBalance)
            }
        }
    }

    private fun updateBalanceVisibility(
        uiState: WalletMainScreenUiState,
        showBalance: Boolean
    ): WalletMainScreenUiState {
        return when (uiState) {
            is WalletMainScreenUiState.EvmData -> {
                val accountsUpdated = uiState.accounts.map { account ->
                    if (account.isBalanceVisible != showBalance) {
                        account.copy(isBalanceVisible = showBalance)
                    } else {
                        account
                    }
                }

                if (uiState.isBalanceVisible != showBalance || accountsUpdated != uiState.accounts) {
                    uiState.copy(
                        isBalanceVisible = showBalance,
                        accounts = accountsUpdated
                    )
                } else {
                    uiState
                }
            }

            is WalletMainScreenUiState.AntelopeData -> {
                val accountsUpdated = uiState.accounts.map { account ->
                    if (account.isBalanceVisible != showBalance) {
                        account.copy(isBalanceVisible = showBalance)
                    } else {
                        account
                    }
                }

                if (uiState.isBalanceVisible != showBalance || accountsUpdated != uiState.accounts) {
                    uiState.copy(
                        isBalanceVisible = showBalance,
                        accounts = accountsUpdated
                    )
                } else {
                    uiState
                }
            }

            else -> uiState
        }
    }

    private suspend fun checkAndLoadEvmSelectedWalletAccounts(
        networkSelected: BlockchainNetworkData,
        currencySymbol: String
    ): Boolean {
        val result = getSelectedWalletAccountsUseCase()

        result?.let { accountBlockchainModels ->
            val updatedAccounts = accountBlockchainModels.map {
                EvmAccountItemUiModel(
                    account = it,
                    balances = null,
                    isBalanceVisible = balanceVisible,
                    currencySymbol = currencySymbol
                )
            }
            val currentAccounts = (_uiState.value as? WalletMainScreenUiState.EvmData)?.accounts
            val accountsUpdated =
                updatedAccounts.map { it.account } != currentAccounts?.map { it.account }

            if (accountsUpdated) {
                _uiState.update {
                    when (it) {
                        is WalletMainScreenUiState.EvmData -> it.copy(accounts = updatedAccounts)
                        WalletMainScreenUiState.Loading, is WalletMainScreenUiState.NoWallet, is WalletMainScreenUiState.AntelopeData, is WalletMainScreenUiState.BitcoinData -> {
                            WalletMainScreenUiState.EvmData(
                                fiatCurrencySymbol = currencySymbol,
                                accounts = updatedAccounts,
                                selectedAccountIndex = 0,
                                isBalanceVisible = balanceVisible,
                                networkSelected = networkSelected
                            )
                        }
                    }
                }
                return true
            }

            return false
        } ?: kotlin.run {
            _uiState.value = WalletMainScreenUiState.NoWallet(networkSelected)

            return false
        }
    }

    private fun getInitialAntelopeDataUiState(
        antelopeAccounts: List<AntelopeAccountItemUiModel>,
        blockchainNetworkData: BlockchainNetworkData,
        currencySymbol: String
    ) = WalletMainScreenUiState.AntelopeData(
        accounts = antelopeAccounts,
        networkSelected = blockchainNetworkData,
        fiatCurrencySymbol = currencySymbol,
        isBalanceVisible = balanceVisible,
        selectedAccountIndex = 0
    )

    private suspend fun collectEvmBalanceForAllAccounts(forceReload: Boolean) {
        val networkSelected = getSelectedNetworkUseCase()

        getAllAccountBalancesInEvmWalletUseCase(forceReload, networkSelected).collect { data ->
            val accountEvmDataList =
                (_uiState.value as? WalletMainScreenUiState.EvmData)?.accounts ?: return@collect

            _uiState.update {
                if (it !is WalletMainScreenUiState.EvmData) return@update it

                _isRefreshing.value = data is Resource.Loading

                val newAccountsList = accountEvmDataList.mapIndexed { index, accountData ->
                    val balances =
                        data.data
                            ?.getOrNull(index)?.tokenBalances?.data
                            ?.filter { it.isCoin || it.balance.toDouble() > 0 }
                            ?.take(MAX_BALANCE_ITEMS)
                    accountData.copy(balances = balances)
                }

                it.copy(accounts = newAccountsList)
            }
        }
    }

    private suspend fun collectBitcoinBalance(
        blockchainNetworkData: BlockchainNetworkData,
        forceReload: Boolean,
        currencySymbol: String
    ) {
        getSelectedWalletBitcoinAccountsUseCase(
            forceReload = forceReload,
            filterHiddenAccounts = true,
            blockchainNetworkData
        ).collect { data ->
            _uiState.update {
                _isRefreshing.value = data is Resource.Loading

                val newAccountsList = data.data?.map {
                    BitcoinAccountItemUiModel(
                        account = it.account,
                        balanceModel = it.balanceInSatoshis.data,
                        isBalanceVisible = balanceVisible,
                        currencySymbol = currencySymbol
                    )
                } ?: emptyList()

                if (newAccountsList.isEmpty()) {
                    return@update WalletMainScreenUiState.NoWallet(blockchainNetworkData)
                }

                (it as? WalletMainScreenUiState.BitcoinData)?.copy(accounts = newAccountsList)
                    ?: WalletMainScreenUiState.BitcoinData(
                        fiatCurrencySymbol = currencySymbol,
                        accounts = newAccountsList,
                        networkSelected = blockchainNetworkData,
                        selectedAccountIndex = 0,
                        isBalanceVisible = balanceVisible
                    )
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

    companion object {
        const val MAX_BALANCE_ITEMS = 5
        const val KEY_PLACEHOLDER = "placeholder"
    }
}