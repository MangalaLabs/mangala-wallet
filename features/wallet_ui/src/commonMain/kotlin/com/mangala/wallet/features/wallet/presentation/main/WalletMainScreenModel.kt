package com.mangala.wallet.features.wallet.presentation.main

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.features.wallet.presentation.BaseWalletMainScreenModel
import com.mangala.features.wallet.presentation.EvmAccountItemUiModel
import com.mangala.wallet.domain.account.usecases.GetAccountBalanceUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.utils.ClipboardFactory
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class WalletMainScreenModel(
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase,
    private val saveBalanceVisibleStatusUseCase: SaveBalanceVisibleStatusUseCase,
    private val getBalanceVisibleStatusUseCase: GetBalanceVisibleStatusUseCase,
    private val parseQRCodeResultUseCase: ParseQRCodeResultUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val clipboardFactory: ClipboardFactory
) : BaseWalletMainScreenModel() {

    private val _uiState =
        MutableStateFlow<WalletMainScreenUiState>(WalletMainScreenUiState.Loading)
    val uiState: StateFlow<WalletMainScreenUiState> get() = _uiState

    private var balanceVisible = true
    private var loadBalanceJob: Job? = null
    private var scannedAddressFromQrCode: String? = null

    override fun doOnComposableStarted() {
        lifecycleScope.launch {
            loadDataIfChanged()
        }
        lifecycleScope.launch {
            collectBalanceVisibleStatus()
        }
    }

    fun onPullToRefresh() {
        screenModelScope.launch {
            _isRefreshing.value = true
            _uiState.value = WalletMainScreenUiState.Loading
            loadDataIfChanged()
            _isRefreshing.value = false
        }
    }

    private suspend fun loadDataIfChanged() {
        val accountsChanged = checkAndLoadSelectedWalletAccounts()
        val networkChanged = checkAndLoadNetworkSelected()

        if (accountsChanged || networkChanged) {
            loadBalancesForAllAccounts(true)
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
            (it as? WalletMainScreenUiState.Data)?.copy(selectedAccountIndex = index) ?: it
        }
    }

    override fun getCurrentAccountId(): String {
        val accountId = (_uiState.value as? WalletMainScreenUiState.Data)?.accounts?.getOrNull(
            (_uiState.value as? WalletMainScreenUiState.Data)?.selectedAccountIndex ?: 0
        )?.account?.account?.id.orEmpty()

        return accountId
    }

    override fun onScanQrCodeResult(qrCodeData: String): QrCodeData? {
        val qrCodeResult = parseQRCodeResultUseCase(qrCodeData)

        if (qrCodeResult is QrCodeData.Payment) {
            scannedAddressFromQrCode = qrCodeResult.address
        }

        return qrCodeResult
    }

    private fun loadBalancesForAllAccounts(
        forceReload: Boolean = false
    ) {
        loadBalanceJob?.cancel()
        loadBalanceJob = screenModelScope.launch {
            val accountDataList =
                (_uiState.value as? WalletMainScreenUiState.Data)?.accounts ?: return@launch

            // Using async to make network requests concurrently
            val newAccountDataListDeferred = accountDataList.map { accountData ->
                async {
                    val result = getAccountBalanceUseCase.invoke(
                        forceReload = forceReload,
                        address = accountData.account.bip44Address,
                        accountId = accountData.account.account.id,
                        sparkline = true,
                    ).filter { it.isCoin || it.balance.toDouble() > 0 }
                        .take(MAX_BALANCE_ITEMS)

                    result
                }
            }

            val newAccountDataList = newAccountDataListDeferred.awaitAll()
            val balancesModel = accountDataList.mapIndexed { index, accountData ->

                accountData.copy(balances = newAccountDataList[index])
            }
            _uiState.update {
                if (it !is WalletMainScreenUiState.Data) return@update it
                it.copy(accounts = balancesModel)
            }
        }
    }

    fun onClickCopy() {
        val it = _uiState.value
        if (it !is WalletMainScreenUiState.Data) return

        val selectedAccount = it.accounts.getOrNull(it.selectedAccountIndex) ?: return
        val address = Address(selectedAccount.account.bip44Address).eip55 // TODO: Determine type of address copied

        // TODO check hard code
        clipboardFactory.copyText("Mangala copy", address)
    }

    private suspend fun collectBalanceVisibleStatus() {
        getBalanceVisibleStatusUseCase().collect { showBalance ->
            balanceVisible = showBalance
            _uiState.update {
                (it as? WalletMainScreenUiState.Data)?.copy(
                    isBalanceVisible = showBalance
                ) ?: it
            }
        }
    }

    private suspend fun checkAndLoadSelectedWalletAccounts(): Boolean {
        val result = getSelectedWalletAccountsUseCase()
        val currencyCode = getCurrentCurrencyCodeUseCase.invoke()
        val currencySymbol = Currency.valueOf(currencyCode).symbol

        result?.let { accountBlockchainModels ->
            val updatedAccounts = accountBlockchainModels.map {
                EvmAccountItemUiModel(
                    account = it,
                    balances = null,
                    isBalanceVisible = balanceVisible,
                    currencySymbol = currencySymbol
                )
            }
            val currentAccounts = (_uiState.value as? WalletMainScreenUiState.Data)?.accounts
            val accountsUpdated =
                updatedAccounts.map { it.account } != currentAccounts?.map { it.account }

            if (accountsUpdated) {
                _uiState.update {
                    when (it) {
                        is WalletMainScreenUiState.Data -> it.copy(accounts = updatedAccounts)
                        WalletMainScreenUiState.Loading, is WalletMainScreenUiState.NoWallet -> {
                            WalletMainScreenUiState.Data(
                                fiatCurrencySymbol = currencySymbol,
                                accounts = updatedAccounts,
                                selectedAccountIndex = 0,
                                isBalanceVisible = balanceVisible,
                                networkSelected = null
                            )
                        }
                    }
                }
                return true
            }

            return false
        } ?: kotlin.run {
            _uiState.value = WalletMainScreenUiState.NoWallet(null)

            return false
        }
    }

    private suspend fun checkAndLoadNetworkSelected(): Boolean {
        val networkSelected = getSelectedNetworkUseCase.invoke()
        val currentNetwork = (_uiState.value as? WalletMainScreenUiState.Data)?.networkSelected
        _uiState.update {
            when (it) {
                is WalletMainScreenUiState.Data -> {
                    it.copy(networkSelected = networkSelected)
                }

                is WalletMainScreenUiState.NoWallet -> {
                    it.copy(networkSelected = networkSelected)
                }

                else -> {
                    it
                }
            }
        }

        return networkSelected != currentNetwork
    }

    companion object {
        const val MAX_BALANCE_ITEMS = 5
    }
}