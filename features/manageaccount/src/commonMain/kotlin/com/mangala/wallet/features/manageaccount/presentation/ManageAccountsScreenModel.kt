package com.mangala.wallet.features.manageaccount.presentation

import com.mangala.wallet.domain.account.usecases.GetAccountBalanceUseCase
import com.mangala.wallet.domain.account.usecases.UpdateAccountsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageAccountsScreenModel(
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    private val updateAccountsUseCase: UpdateAccountsUseCase,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase
) : BaseScreenModel() {

    override fun doOnComposableStarted() {
        lifecycleScope.launch { getAccounts() }
    }

    private val _uiModel = MutableStateFlow(ManageAccountsScreenUiModel())
    val uiModel: StateFlow<ManageAccountsScreenUiModel> get() = _uiModel

    fun onRearrangeItem(fromIndex: Int, toIndex: Int) {
        if (fromIndex < 0 || toIndex < 0) return

        val accounts = _uiModel.value.accounts.toMutableList()
        accounts.add(toIndex, accounts.removeAt(fromIndex))
        _uiModel.update { it.copy(accounts = accounts) }
    }

    fun onClickSave() {
        val uiAccounts = _uiModel.value.accounts.map { it.account.account }
        val accountsToSave = uiAccounts.mapIndexed { index, account ->
            account.copy(sortingOrder = index)
        }
        updateAccountsUseCase(accountsToSave)
    }

    private suspend fun getAccounts() {
        getSelectedWalletAccountsUseCase(filterHiddenAccounts = false)?.map {
            AccountItemUiModel(
                account = it,
                balance = getAccountBalanceUseCase(
                    forceReload = false,
                    it.bip44Address,
                    it.account.id,
                    sparkline = false
                )
            )
        }?.let { accountItemUiModel ->
            _uiModel.update { it.copy(accounts = accountItemUiModel) }
        }
    }
}