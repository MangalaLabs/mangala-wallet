package com.mangala.wallet.features.chains.antelope.presentation.manageaccount

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.DeleteAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ToastFactory
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManageAntelopeAccountScreenModel(
    private val getAntelopeAccountsUseCase: GetAccountsUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val toastFactory: ToastFactory
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow<ManageAntelopeAccountScreenUiState>(ManageAntelopeAccountScreenUiState.Loading)
    val uiState: StateFlow<ManageAntelopeAccountScreenUiState> get() = _uiState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _toastMessage = mutableStateOf<StringResource?>(null)
    val toastMessage: State<StringResource?> get() = _toastMessage

    var selectedAccount: AntelopeAccount? = null

    init {
        loadAntelopeAccounts()
    }

    override fun doOnComposableStarted() {
        loadAntelopeAccounts()
    }

    fun displayToast(message: String) {
        // Since using Transient in the Screen doesn't work, we move the logic here
        toastFactory.show(message)
        _toastMessage.value = null
    }

    private fun loadAntelopeAccounts() {
        screenModelScope.launch {
            getAntelopeAccountsUseCase.invokeFlow(includeTempAccounts = true)
                .collectLatest { accounts ->
                    _uiState.value = if (accounts.isEmpty()) {
                        ManageAntelopeAccountScreenUiState.NoAccount
                    } else {
                        ManageAntelopeAccountScreenUiState.Success(accounts = accounts)
                    }
            }
        }
    }

    fun deleteAccount(accountName: String) {
        screenModelScope.launch {
            deleteAccountUseCase(accountName)
            loadAntelopeAccounts()
        }
    }
}