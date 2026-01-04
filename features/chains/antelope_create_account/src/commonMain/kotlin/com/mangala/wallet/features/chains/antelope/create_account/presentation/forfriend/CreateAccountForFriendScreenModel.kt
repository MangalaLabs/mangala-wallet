package com.mangala.wallet.features.chains.antelope.create_account.presentation.forfriend

import cafe.adriel.voyager.core.model.screenModelScope
import com.linh.antelope_qr.domain.model.CreateAccountForFriendRequest
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAccountForFriendUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.CreateAccountRamOption
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.BaseAntelopeTransactScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateAccountForFriendScreenModel(
    private val createAccountForFriendRequest: CreateAccountForFriendRequest,
    private val createAccountForFriendUseCase: CreateAccountForFriendUseCase,
    private val getAccountsUseCase: GetAccountsUseCase
) : BaseAntelopeTransactScreenModel(
    createAccountForFriendUseCase,
    createAccountForFriendRequest.blockchainUid
) {

    private val _uiState: MutableStateFlow<CreateAccountForFriendUiState> =
        MutableStateFlow(CreateAccountForFriendUiState.Initial(createAccountForFriendRequest))
    val uiState: StateFlow<CreateAccountForFriendUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            _uiState.update {
                val accounts = getAccountsUseCase()

                if (accounts.isEmpty()) CreateAccountForFriendUiState.NoAccount

                CreateAccountForFriendUiState.Loaded(createAccountForFriendRequest, accounts, 0)
            }
        }
    }

    fun onSelectAccount(accountName: String) {
        val currentState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return
        val newSelectedAccountIndex = currentState.accounts.indexOfFirst { it.accountName == accountName }

        if (newSelectedAccountIndex == -1) return

        _uiState.value = currentState.copy(selectedAccountIndex = newSelectedAccountIndex)
    }

    override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
        val uiState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return

        _uiState.value =
            uiState.copy(
                resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                resourceRequiredTotal = resourceProviderResponse.fee,
                isLoading = false
            )
    }

    override fun onRequestTransactionInvalidRequest() {
        val uiState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return

        _uiState.value = uiState.copy(isLoading = false, error = "Invalid request")
    }

    override fun onRequestTransactionResourceCovered() {
        val uiState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return

        _uiState.value = uiState.copy(promptConfirmTransaction = true, isLoading = false)
    }

    override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
        val currentState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return Result.failure(Exception("Invalid state"))
        val selectedAccount = currentState.selectedAccount ?: return Result.failure(Exception("Invalid state"))

        return createAccountForFriendUseCase.requestCreateAccount(
            request = createAccountForFriendRequest,
            selectedAccount,
            CreateAccountRamOption.BUY_RAM
        )
    }

    override fun onDismissTransactionFeeBreakdown() {
        val currentState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return

        _uiState.update {
            currentState.copy(
                resourceRequiredBreakdown = null,
                resourceRequiredTotal = null,
                isLoading = false
            )
        }
    }

    override fun onPinPromptShown() {
        val uiState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return

        _uiState.update {
            uiState.copy(promptConfirmTransaction = false, resourceRequiredBreakdown = null, resourceRequiredTotal = null)
        }
    }

    override fun onConfirmResourceProviderFee() {
        val currentState = _uiState.value as? CreateAccountForFriendUiState.Loaded ?: return

        _uiState.value = currentState.copy(promptConfirmTransaction = true, isLoading = true)
    }

    override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
        val currentState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return Result.failure(Exception("Invalid state"))
        val selectedAccount = currentState.selectedAccount ?: return Result.failure(Exception("Invalid state"))

        return createAccountForFriendUseCase.pushCreateAccount(
            request = createAccountForFriendRequest,
            selectedAccount,
            CreateAccountRamOption.BUY_RAM
        )
    }

    override fun showLoadingState() {
        val uiState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return

        _uiState.value = uiState.copy(isLoading = true)
    }

    override fun onPushTransactionSuccess(txHash: String) {
        _uiState.value = CreateAccountForFriendUiState.Created(txHash)
    }

    override fun onPushTransactionFail(throwable: Throwable) {
        val currentState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return

        _uiState.value = currentState.copy(error = throwable.message, isLoading = false)
    }

    fun onAuthenticationSuccess() {
        val currentState = (_uiState.value as? CreateAccountForFriendUiState.Loaded) ?: return

        super.onAuthenticationSuccess(currentState.selectedAccount?.accountName.orEmpty())
    }
}