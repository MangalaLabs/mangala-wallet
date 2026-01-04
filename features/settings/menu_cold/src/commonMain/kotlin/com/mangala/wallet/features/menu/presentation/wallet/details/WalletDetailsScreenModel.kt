package com.mangala.wallet.features.menu.presentation.wallet.details

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.account.usecases.SetHiddenAccountUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletAccountsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletByIdUseCase
import com.mangala.wallet.domain.wallet.usecases.SaveWalletNameUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class WalletDetailsScreenModel(
    private val walletId: String,
    private val getWalletByIdUseCase: GetWalletByIdUseCase,
    private val saveWalletNameUseCase: SaveWalletNameUseCase,
    private val getWalletAccountsUseCase: GetWalletAccountsUseCase,
    private val setHiddenAccountUseCase: SetHiddenAccountUseCase
) : BaseScreenModel(), KoinComponent {

    val _uiModel = MutableStateFlow(WalletDetailsScreenUiModel())
    val _walletName: MutableStateFlow<String> = MutableStateFlow("")

    init {
        screenModelScope.launch {
            getWalletDetails(walletId)
        }
        screenModelScope.launch {
            getAccounts()
        }
    }

    private fun saveWalletName(walletName: String, walletId: String) {
        screenModelScope.launch {
            saveWalletNameUseCase.invoke(walletName, walletId)
        }
    }

    private suspend fun getWalletDetails(walletId: String) {
        getWalletByIdUseCase.invokeFlow(walletId).collect { wallet ->
            if (wallet != null) {
                _walletName.value = wallet.name
            }
        }
    }

    fun updateWalletName(newName: String) {
        _walletName.value = newName
        saveWalletName(newName, walletId)
    }

    fun onClickChangeHiddenAccount(accountId: String) {
        screenModelScope.launch {
            setHiddenAccountUseCase.invoke(accountId)
        }
    }

    private suspend fun getAccounts() {
        val result = getWalletAccountsUseCase.invokeFlow(filterHiddenAccounts = false, walletId)
        result.collect { list ->
            list?.map {
                AccountItemUiModel(
                    account = it
                )
            }?.let { accountItemUiModel ->
                _uiModel.update { it.copy(accounts = accountItemUiModel) }
            }
        }
    }

}