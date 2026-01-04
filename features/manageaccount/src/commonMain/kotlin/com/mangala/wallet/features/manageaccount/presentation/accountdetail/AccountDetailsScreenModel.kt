package com.mangala.wallet.features.manageaccount.presentation.accountdetail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.account.usecases.UpdateAccountUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.utils.ClipboardFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountDetailsScreenModel(val accountId: String) : ScreenModel, KoinComponent {

    private val getAccountByIdUseCase: GetAccountByIdUseCase by inject()
    private val updateAccountUseCase: UpdateAccountUseCase by inject()
    private val clipboardFactory: ClipboardFactory by inject()

    private val _uiState =
        MutableStateFlow<AccountDetailsScreenUiState>(AccountDetailsScreenUiState.Loading)
    val uiState: StateFlow<AccountDetailsScreenUiState> get() = _uiState

    init {
        loadAccountData()
    }

    private fun loadAccountData() {
        screenModelScope.launch {
            val account = getAccountByIdUseCase(accountId)
            _uiState.update {
                AccountDetailsScreenUiState.Success(
                    account
                )
            }
        }
    }

    fun onClickSave() {
        val account =
            (_uiState.value as? AccountDetailsScreenUiState.Success)
                ?.accountBlockchainModel

        account?.let {
            updateAccountUseCase(it)
        }
    }

    fun onUpdateAccountName(accountName: String) {
        val it = _uiState.value
        if (it !is AccountDetailsScreenUiState.Success) return

        _uiState.update {
            (it as? AccountDetailsScreenUiState.Success)?.copy(
                accountBlockchainModel = it.accountBlockchainModel.copy(
                    name = accountName
                )
            ) ?: it
        }
    }

    fun onToggleHide(isHidden: Boolean) {
        val it = _uiState.value
        if (it !is AccountDetailsScreenUiState.Success) return

        _uiState.update {
            (it as? AccountDetailsScreenUiState.Success)?.copy(
                accountBlockchainModel = it.accountBlockchainModel.copy(
                    isHidden = isHidden
                )
            ) ?: it
        }
    }

    fun onClickCopy() {
        val it = _uiState.value
        if (it !is AccountDetailsScreenUiState.Success) return

        val address =
            Address(it.accountBlockchainModel.bip44Address).eip55 // TODO: Determine type of address copied

        clipboardFactory.copyText("Mangala copy", address)
    }
}