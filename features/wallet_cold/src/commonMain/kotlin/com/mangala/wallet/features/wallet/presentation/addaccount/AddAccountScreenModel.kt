package com.mangala.wallet.features.wallet.presentation.addaccount

import cafe.adriel.voyager.core.model.ScreenModel
import com.mangala.wallet.domain.account.usecases.CreateWalletAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddAccountScreenModel : ScreenModel, KoinComponent {

    private val createWalletAccountUseCase: CreateWalletAccountUseCase by inject()

    private val _uiModel = MutableStateFlow(AddAccountScreenUiModel())
    val uiModel: StateFlow<AddAccountScreenUiModel> get() = _uiModel

    fun onChangeAccountName(accountName: String) {
        _uiModel.update { it.copy(accountName = accountName) }
    }

    fun onAddNewAccount() {
        createWalletAccountUseCase(_uiModel.value.accountName)
    }
}