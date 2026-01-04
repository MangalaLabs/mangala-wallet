package com.mangala.wallet.features.chains.antelope.create_account.presentation.step2

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckAccountAvailableToCreateUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GenerateRandomAccountNameUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class Step2SelectAccountNameScreenModel(
    initialAccountName: String = "",
    initialAccountSuffix: String?,
    accountNameType: AccountNameType,
    private val checkAccountAvailableToCreateUseCase: CheckAccountAvailableToCreateUseCase,
    private val validateAccountUseCase: ValidateAccountUseCase,
    private val generateRandomAccountNameUseCase: GenerateRandomAccountNameUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<Step2SelectAccountNameUiState> = MutableStateFlow(
        Step2SelectAccountNameUiState.Ready(
            accountType = accountNameType,
            validationResult = validateAccountUseCase.validateAccountName(
                "",
                accountNameType,
                checkForSuffix = true
            )
        )
    )
    val uiState: StateFlow<Step2SelectAccountNameUiState> = _uiState.asStateFlow()

    private lateinit var blockchainType: BlockchainType

    init {
        screenModelScope.launch {
            blockchainType = getSelectedNetworkUseCase().blockchainType
            onAccountNameChange(TextFieldValue(initialAccountName), initialAccountSuffix)
            observeAccountName()
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeAccountName() {
        uiState
            .filterIsInstance<Step2SelectAccountNameUiState.Ready>()
            .map { it.accountName.text }
            .debounce(CHECK_ACCOUNT_EXISTS_DEBOUNCE_TIME_MILLISECONDS)
            .distinctUntilChanged()
            .flatMapLatest { accountName ->
                flow {
                    val currentUiState = _uiState.value
                    val validationResult =
                        (currentUiState as? Step2SelectAccountNameUiState.Ready)?.validationResult

                    if (validationResult?.isValid == true) {
                        _uiState.update { currentState ->
                            (currentState as? Step2SelectAccountNameUiState.Ready)?.copy(
                                isCheckingAccountExistence = true
                            ) ?: currentState
                        }

                        val fullAccountName = accountName + currentUiState.accountNameSuffix

                        val isAccountNotTaken =
                            checkAccountAvailableToCreateUseCase(
                                blockchainType,
                                fullAccountName,
                                currentUiState.accountType
                            )
                        emit(isAccountNotTaken)
                    } else {
                        _uiState.update { currentState ->
                            (currentState as? Step2SelectAccountNameUiState.Ready)?.copy(
                                isCheckingAccountExistence = false,
                                isAccountNotTaken = null
                            ) ?: currentState
                        }
                        emit(null)
                    }
                }
            }
            .onEach { doesExist ->
                _uiState.update { currentState ->
                    (currentState as? Step2SelectAccountNameUiState.Ready)?.copy(
                        isCheckingAccountExistence = false,
                        isAccountNotTaken = doesExist
                    ) ?: currentState
                }
            }
            .launchIn(screenModelScope)
    }

    fun onAccountNameChange(updatedTextFieldValue: TextFieldValue, accountNameSuffix: String? = null) {
        val currentState = _uiState.value as Step2SelectAccountNameUiState.Ready
        val suffix = accountNameSuffix ?: currentState.accountNameSuffix

        val updatedText = updatedTextFieldValue.text
        val maxLength = AntelopeAccount.MAX_LENGTH_ACCOUNT_NAME
        val suffixLength = suffix.length

        if (updatedText.length + suffixLength <= maxLength || updatedText.length < currentState.accountName.text.length) {
            _uiState.value = currentState.copy(
                accountName = updatedTextFieldValue,
                validationResult = validateAccountUseCase.validateAccountName(updatedText + suffix, currentState.accountType, checkForSuffix = true),
                blockchainType = blockchainType,
                isAccountNotTaken = null  // Reset availability status when account name changes
            )
        } else {
            // We have this else in case user paste the account name that is too long
            val newText = updatedText.substring(
                0,
                max(0, maxLength - suffixLength)
            )

            if (newText == currentState.accountName.text) return

            _uiState.value = currentState.copy(
                accountName = updatedTextFieldValue.copy(text = newText),
                validationResult = validateAccountUseCase.validateAccountName(newText + AntelopeAccount.getPremiumAccountSuffix(blockchainType), currentState.accountType, checkForSuffix = true),
                blockchainType = blockchainType,
                isAccountNotTaken = null  // Reset availability status when account name changes
            )
        }
    }

    fun onAccountTypeChange(newAccountType: AccountNameType) {
        val currentState = _uiState.value as Step2SelectAccountNameUiState.Ready
        _uiState.value = currentState.copy(accountType = newAccountType)
        onAccountNameChange(TextFieldValue())
    }

    fun suggestValidName() {
        val currentState = _uiState.value as Step2SelectAccountNameUiState.Ready
        val generatedName =
            generateRandomAccountNameUseCase(currentState.accountType, blockchainType)
        _uiState.value = currentState.copy(
            accountName = TextFieldValue(generatedName),
            validationResult = validateAccountUseCase.validateAccountName(generatedName + currentState.accountNameSuffix, currentState.accountType, checkForSuffix = true),
            isAccountNotTaken = null  // Reset availability status when suggesting new name
        )
    }

    fun isDevelopmentEnvironment(): Boolean {
        return buildEnvironmentProvider.isDevelopmentEnvironment()
    }

    companion object {
        private const val CHECK_ACCOUNT_EXISTS_DEBOUNCE_TIME_MILLISECONDS = 500L
    }
}