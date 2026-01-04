package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.selectpaymentaccount

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.domain.usecase.GetCurrencyBalanceAntelopeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectPaymentAccountScreenModel(
    private val initialAccountName: String,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getCurrencyBalanceAntelopeUseCase: GetCurrencyBalanceAntelopeUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<SelectPaymentAccountUiState> =
        MutableStateFlow(SelectPaymentAccountUiState.Loading)
    val uiState: StateFlow<SelectPaymentAccountUiState> = _uiState.asStateFlow()

    private lateinit var selectedBlockchainType: BlockchainType

    private var job: Job? = null

    init {
        screenModelScope.launch {
            selectedBlockchainType = getSelectedNetworkUseCase().blockchainType

            val accounts = getAccountsUseCase().map {
                AccountsUiModel(
                    it.accountName,
                    null
                )
            }
            val selectedAccountIndex =
                accounts.indexOfFirst { it.accountName == initialAccountName }
            _uiState.value = SelectPaymentAccountUiState.Ready(
                accounts = accounts,
                selectedAccountIndex = selectedAccountIndex
            )
            if (selectedAccountIndex != -1) {
                onSelectAccount(accounts[selectedAccountIndex])
            }
        }
    }

    fun onSelectAccount(accountsUiModel: AccountsUiModel) {
        job?.cancel()
        job = screenModelScope.launch {
            _uiState.update {
                val oldUiState = (it as? SelectPaymentAccountUiState.Ready) ?: return@launch
                oldUiState.copy(selectedAccountIndex = oldUiState.accounts.indexOfFirst { it.accountName == accountsUiModel.accountName })
            }

            if (accountsUiModel.nativeCoinBalance != null) return@launch

            val result = getCurrencyBalanceAntelopeUseCase(
                selectedBlockchainType,
                accountsUiModel.accountName
            )
            val balance = result.firstOrNull().orEmpty()
            _uiState.update {
                val oldUiState = (it as? SelectPaymentAccountUiState.Ready) ?: return@launch

                val accountsWithUpdatedBalance = oldUiState.accounts.map {
                    if (it.accountName == accountsUiModel.accountName) {
                        it.copy(nativeCoinBalance = balance)
                    } else {
                        it.copy(nativeCoinBalance = it.nativeCoinBalance)
                    }
                }

                oldUiState.copy(
                    accounts = accountsWithUpdatedBalance
                )
            }
        }
    }
}