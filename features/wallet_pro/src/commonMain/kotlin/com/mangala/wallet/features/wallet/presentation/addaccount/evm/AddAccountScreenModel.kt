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

    fun onAddNewAccount() {
        screenModelScope.launch {
            createWalletAccountUseCase(_uiModel.value.accountName, getSelectedNetworkUseCase().blockchainType)
            onCreateDone.trySend(Unit)
        }
    }
}