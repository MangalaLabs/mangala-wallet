package com.mangala.wallet.features.home.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.AppLifecycleObserver
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.mangala.wallet.utils.ISystemInfoManager
import com.mmk.kmpnotifier.notification.NotifierManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

const val NOTICE_TITLE_EVM_ACCOUNT_SUCCESS_KEY = "Create Account EOS Success"
const val NOTICE_TITLE_EVM_ACCOUNT_FAILED_KEY = "Create Account EOS Failed"

class HomeScreenModel(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val appLifecycleObserver: AppLifecycleObserver,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val getWalletAccountsUseCase: GetWalletAccountsUseCase,
    private val getAntelopeAccountsUseCase: GetAccountsUseCase,
    private val systemInfoManager: ISystemInfoManager
) : ScreenModel {

    private val _selectedNetwork = MutableStateFlow<BlockchainNetworkData?>(null)
    val selectedNetwork = _selectedNetwork.asStateFlow()

    private val _isNoEvmAccountFound = MutableStateFlow(true)
    val isNoEvmAccountFound = _isNoEvmAccountFound.asStateFlow()

    private val _isDevelopmentEnvironment = MutableStateFlow(buildEnvironmentProvider.isDevelopmentEnvironment())
    val isDevelopmentEnvironment = _isDevelopmentEnvironment.asStateFlow()

    private val _eosAccountCreatedUIState = MutableStateFlow<EosAccountCreatedNotificationUIState>(EosAccountCreatedNotificationUIState.Default)
    val eosAccountCreatedUIState = _eosAccountCreatedUIState.asStateFlow()

    private val _isNoAntelopeAccountFound = MutableStateFlow(true)
    val isNoAntelopeAccountFound = _isNoAntelopeAccountFound.asStateFlow()

    init {
        screenModelScope.launch {
            getSelectedNetworkUseCase.invokeFlow().stateIn(screenModelScope).collect {
                _selectedNetwork.value = it
            }
        }

        screenModelScope.launch {
            getSelectedWalletUseCase.invokeFlow().stateIn(screenModelScope).debounce(1000).distinctUntilChanged().collect {
                val isNoWalletImported = it == null
                if (isNoWalletImported) {
                   _isNoEvmAccountFound.value = true
                } else {
                    val accounts = getWalletAccountsUseCase(
                        filterHiddenAccounts = true,
                        walletId = it.id
                    ) ?: emptyList()
                    _isNoEvmAccountFound.value = accounts.isEmpty()
                }
            }
        }

        screenModelScope.launch {
            getAntelopeAccountsUseCase.invokeFlow().map { it.isEmpty() }.stateIn(screenModelScope).collectLatest {
                _isNoAntelopeAccountFound.value = it
            }
        }

        initFcmHandler()
    }

    /**
     * turn on eos account in wallet at home screen
     * navigate to backup wallet screen
     * dismiss dialog
     * call back to screen
     */
    fun updateEosAccountStatus(accountName: String, chainId: String, callback: (Boolean) -> Unit) {
        val blockchainType = BlockchainType.fromChainId(chainId)
        println("blockchainType: $blockchainType")
        screenModelScope.launch {
            updateAccountStatusUseCase(
                accountName = accountName,
                isTemp = false,
                blockchainType = blockchainType,
                createAccountState = AntelopeAccount.CreateAccountState.DONE
            )
        }
        _eosAccountCreatedUIState.value = EosAccountCreatedNotificationUIState.Default
        callback(true)
    }

    fun onDismissEosAccountNoticeDialog() {
        _eosAccountCreatedUIState.value = EosAccountCreatedNotificationUIState.Default
    }

    fun isDeviceSecure(): Boolean {
        return systemInfoManager.isDeviceSecure()
    }

    private fun initFcmHandler() {
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onPushNotification(title: String?, body: String?) {
                println("notice tile: $title - body: $body")
                super.onPushNotification(title, body)

                val isAppInForeground = appLifecycleObserver.isAppOpen
                println("isAppInForeground: $isAppInForeground")
                if (isAppInForeground) {
                    println("App is in foreground")
                    println("Show notification dialog")
                    if (title == NOTICE_TITLE_EVM_ACCOUNT_SUCCESS_KEY && body != null) {
                        val eosAccountNoticeBody = Json.decodeFromString<EosAccountNoticeBody>(body)
                        // todo: check the account name already in the blockchain or not.
                        val eosAccountNoticeModel = EosAccountNoticeModel(title, eosAccountNoticeBody)
                        _eosAccountCreatedUIState.value = EosAccountCreatedNotificationUIState.Success(eosAccountNoticeModel)
                    }
                    if (title == NOTICE_TITLE_EVM_ACCOUNT_FAILED_KEY && body != null) {
                        val eosAccountNoticeBody = Json.decodeFromString<EosAccountNoticeBody>(title)
                        _eosAccountCreatedUIState.value = EosAccountCreatedNotificationUIState.Failed(body, eosAccountNoticeBody)
                    }
                }
            }
        })
    }
}
