package com.mangala.wallet.menu_base.presentation.menu

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.mangala.wallet.utils.app.AppVersionUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BaseMenuScreenModel(
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider,
    val appVersionUtils: AppVersionUtils
): BaseScreenModel() {

    private val _uiModel = MutableStateFlow(MenuScreenUiModel(null))
    val uiModel: StateFlow<MenuScreenUiModel> get() = _uiModel

    private val _walletName = MutableStateFlow("")

    val walletName: StateFlow<String> get() = _walletName

    init {
        screenModelScope.launch {
            getWallet()
        }
        screenModelScope.launch {
            collectSelectedNetwork()
        }
    }

    override fun doOnComposableStarted() = Unit

    fun isDevelopmentEnvironment(): Boolean {
        return buildEnvironmentProvider.isDevelopmentEnvironment()
    }

    private suspend fun getWallet() {
        getSelectedWalletUseCase.invokeFlow().collect { wallet -> _walletName.value = wallet?.name ?: ""}
    }

    private suspend fun collectSelectedNetwork() {
        getSelectedNetworkUseCase.invokeFlow().collect { selectedNetwork ->
            _uiModel.value = MenuScreenUiModel(selectedNetwork)
        }
    }
}