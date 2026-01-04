package com.mangala.wallet.features.chains.antelope.presentation.importaccount.step2

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.domain.usecase.account.ImportAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Step2ImportAccountSelectAccountScreenModel(
    private val privateKey: String,
    accountsByAuthorizers: ArrayList<AntelopeAccountByAuthorizer>,
    private val importAccountUseCase: ImportAccountUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase
) : BaseScreenModel() {
    private val _uiState: MutableStateFlow<Step2ImportAccountSelectAccountUiState> =
        MutableStateFlow(
            Step2ImportAccountSelectAccountUiState.NotImported(
                emptyList()
            )
        )
    val uiState: StateFlow<Step2ImportAccountSelectAccountUiState> = _uiState.asStateFlow()

    private val _stateReturn: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val stateReturn: StateFlow<Boolean> = _stateReturn.asStateFlow()

    private var accounts: Map<String, List<AntelopeAccountByAuthorizer>> =
        accountsByAuthorizers.groupBy { it.accountName }

    init {
        screenModelScope.launch {
            _uiState.value =
                Step2ImportAccountSelectAccountUiState.NotImported(accounts.keys.toList())

            _uiState.update {
                if (it is Step2ImportAccountSelectAccountUiState.NotImported) {
                    it.copy(blockchainType = getSelectedNetworkUseCase().blockchainType)
                } else it
            }
        }
    }

    fun onSelectAccount(accountName: String) {
        screenModelScope.launch {
            val isPinSetup = getIsPinSetupUseCase()
            _uiState.value = Step2ImportAccountSelectAccountUiState.Imported(
                accountName = accountName,
                blockchainType = getSelectedNetworkUseCase().blockchainType,
                isPinSetup = isPinSetup
            )
        }
    }

    fun createAccount(accountName: String) {
        screenModelScope.launch {
            val selectedAccountAuthorizers = accounts[accountName] ?: return@launch
            val isPinSetup = getIsPinSetupUseCase()

            importAccountUseCase(
                accountName,
                privateKey,
                selectedAccountAuthorizers,
                true
            ).fold(
                onSuccess = {
                    _uiState.value =
                        Step2ImportAccountSelectAccountUiState.AccountCreated(
                            accountName,
                            blockchainType = getSelectedNetworkUseCase().blockchainType,
                            isPinSetup,
                        )
                },
                onFailure = {
                    _uiState.value =
                        (_uiState.value as? Step2ImportAccountSelectAccountUiState.NotImported)?.copy(
                            error = it.message
                        ) ?: _uiState.value
                }
            )
        }
    }

    suspend fun returnCreatedAccount(accountName: String) {
        val isPinSetup = getIsPinSetupUseCase()
        withContext(Dispatchers.Main) {
            if (isPinSetup) {
                _stateReturn.value = true
                updateAccountStatusUseCase(
                    accountName = accountName,
                    isTemp = false,
                    blockchainType = getSelectedNetworkUseCase().blockchainType,
                    createAccountState = AntelopeAccount.CreateAccountState.DONE
                )
            } else {
                _stateReturn.value = true
                _uiState.value =
                    Step2ImportAccountSelectAccountUiState.NotImported(accounts.keys.toList())
            }
        }
    }
}