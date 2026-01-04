package com.mangala.wallet.features.menu.presentation.wallet.add_wallet

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants.PIN_KEY
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddWalletScreenModel(
    private val secureStorageWrapper: SecureStorageWrapper,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
): BaseScreenModel() {

    private val _uiModel = MutableStateFlow(AddWalletScreenUiModel(null))
    val uiModel: StateFlow<AddWalletScreenUiModel> get() = _uiModel

    private fun getPin(): String {
        return secureStorageWrapper.getValue(PIN_KEY) ?: ""
    }

    fun isPinExist(): Boolean {
        return getPin().isNotEmpty()
    }

    override fun doOnComposableStarted() {
        screenModelScope.launch {
            collectSelectedNetwork()
        }
    }

    private suspend fun collectSelectedNetwork() {
        getSelectedNetworkUseCase.invokeFlow().collect { selectedNetwork ->
            _uiModel.value = AddWalletScreenUiModel(selectedNetwork)
        }
    }
}