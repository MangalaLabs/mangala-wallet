package com.mangala.wallet.features.wallet.presentation.addaccount.evm

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.account.usecases.CreateWalletAccountUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddAccountScreenModel(
    private val createWalletAccountUseCase: CreateWalletAccountUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) : BaseScreenModel() {

    private val _uiModel = MutableStateFlow(AddAccountScreenUiModel())
    val uiModel: StateFlow<AddAccountScreenUiModel> get() = _uiModel
    val onCreateDone: Channel<Unit> = Channel()

    fun onChangeAccountName(accountName: String) {
        _uiModel.update { it.copy(accountName = accountName) }
    }

    fun onAddNewAccount(walletId: String? = null) {
        screenModelScope.launch {
            if (_uiModel.value.isCreating) {
                return@launch
            }
            val accountName = _uiModel.value.accountName
            _uiModel.update { it.copy(isCreating = true) }
            val selectedNetwork = runCatching { getSelectedNetworkUseCase() }.getOrNull()
            try {
                val result = if (walletId.isNullOrBlank()) {
                    createWalletAccountUseCase(accountName, selectedNetwork?.blockchainType ?: getSelectedNetworkUseCase().blockchainType)
                } else {
                    createWalletAccountUseCase(accountName, walletId, selectedNetwork?.blockchainType ?: getSelectedNetworkUseCase().blockchainType)
                }

                if (result.isSuccess) {
                    onCreateDone.trySend(Unit)
                }
            } catch (_: Exception) {
            } finally {
                _uiModel.update { it.copy(isCreating = false) }
            }
        }
    }
}
