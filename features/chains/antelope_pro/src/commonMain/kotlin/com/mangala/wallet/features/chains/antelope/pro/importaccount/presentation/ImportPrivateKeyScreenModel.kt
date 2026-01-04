package com.mangala.wallet.features.chains.antelope.pro.importaccount.presentation

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.domain.usecase.account.ImportAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.createimport.GetAccountsByAuthorizersUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.KeyType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class ImportPrivateKeyScreenModel(
    private val getAccountsByAuthorizersUseCase: GetAccountsByAuthorizersUseCase,
    private val importAccountUseCase: ImportAccountUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider,
    private val saveSelectedNetworkUseCase: SaveSelectedNetworkUseCase
) : BaseScreenModel() {
    
    private val _uiState = MutableStateFlow<ImportPrivateKeyUiState>(
        ImportPrivateKeyUiState.InputPhase()
    )
    val uiState: StateFlow<ImportPrivateKeyUiState> = _uiState.asStateFlow()
    
    private val _navigationState = MutableStateFlow<NavigationState?>(null)
    val navigationState: StateFlow<NavigationState?> = _navigationState.asStateFlow()
    
    private var discoveryJob: Job? = null
    
    fun onPrivateKeyChange(privateKey: String) {
        discoveryJob?.cancel()

        when (val currentState = _uiState.value) {
            is ImportPrivateKeyUiState.InputPhase -> {
                val validation = validatePrivateKey(privateKey)
                _uiState.value = currentState.copy(
                    privateKey = privateKey,
                    validation = validation,
                    error = null
                )
                
                // Auto-discover accounts when key is valid, with a delay to avoid interference
                if (validation.isValid()) {
                    discoveryJob = screenModelScope.launch {
                        delay(500) // Wait 500ms before triggering discovery
                        discoverAccounts(privateKey)
                    }
                }
            }
            is ImportPrivateKeyUiState.AccountsFound -> {
                // If user edits private key when accounts are found, go back to input phase
                val validation = validatePrivateKey(privateKey)
                _uiState.value = ImportPrivateKeyUiState.InputPhase(
                    privateKey = privateKey,
                    validation = validation,
                    error = null,
                    contentVisible = currentState.contentVisible,
                    isTermsAgreed = currentState.isTermsAgreed,
                    isKeyVisible = currentState.isKeyVisible
                )
                
                // Auto-discover accounts when key is valid, with a delay
                if (validation.isValid()) {
                    discoveryJob = screenModelScope.launch {
                        delay(500) // Wait 500ms before triggering discovery
                        discoverAccounts(privateKey)
                    }
                }
            }
            else -> {}
        }
    }
    
    private fun validatePrivateKey(privateKey: String): PrivateKeyValidation {
        return PrivateKeyValidation(
            correctPrefix = privateKey.startsWith("5") || privateKey.startsWith(KeyType.K1.privateKeyPrefix),
            isValidWif = isValidWifFormat(privateKey)
        )
    }
    
    private fun isValidWifFormat(privateKey: String): Boolean {
        return try {
            EosPrivateKey.fromString(privateKey)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun discoverAccounts(privateKey: String) {
        screenModelScope.launch {
            try {
                val currentContentVisible = _uiState.value.contentVisible
                val currentTermsAgreed = _uiState.value.isTermsAgreed
                val currentKeyVisible = _uiState.value.isKeyVisible
                
                _uiState.value = ImportPrivateKeyUiState.InputPhase(
                    privateKey = privateKey,
                    validation = validatePrivateKey(privateKey),
                    isValidating = true,
                    contentVisible = currentContentVisible,
                    isTermsAgreed = currentTermsAgreed,
                    isKeyVisible = currentKeyVisible
                )

                val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()
                val antelopeNetworks = BlockchainNetworkData.getAllBlockchainNetworkSupported(isDevelopmentEnvironment)
                    .filter { it.blockchainType.networkType == NetworkType.ANTELOPE }

                val accountsByAuthorizers = antelopeNetworks.map { network ->
                    async {
                        getAccountsByAuthorizersUseCase(
                            privateKey = privateKey,
                            blockchainType = network.blockchainType
                        )
                    }
                }.awaitAll().mapNotNull { it.getOrNull() }.flatten()
                
                if (accountsByAuthorizers.isNotEmpty()) {
                    val accounts = accountsByAuthorizers.map { it.accountName }.distinct()
                    
                    // Detect which key types are available
                    val hasActiveKey = accountsByAuthorizers.any { 
                        it.permissionName.lowercase() == "active" 
                    }
                    val hasOwnerKey = accountsByAuthorizers.any { 
                        it.permissionName.lowercase() == "owner" 
                    }
                    
                    val currentTermsAgreed = _uiState.value.isTermsAgreed
                    val currentKeyVisible = _uiState.value.isKeyVisible
                    
                    _uiState.value = ImportPrivateKeyUiState.AccountsFound(
                        privateKey = privateKey,
                        accounts = accounts,
                        accountsByAuthorizers = accountsByAuthorizers,
                        selectedAccount = accounts.firstOrNull(), // Auto-select first account
                        hasActiveKey = hasActiveKey,
                        hasOwnerKey = hasOwnerKey,
                        contentVisible = true, // Always show content when accounts are found
                        isTermsAgreed = currentTermsAgreed,
                        isKeyVisible = currentKeyVisible
                    )
                } else {
                    val currentContentVisible = _uiState.value.contentVisible
                    val currentTermsAgreed = _uiState.value.isTermsAgreed
                    val currentKeyVisible = _uiState.value.isKeyVisible
                    
                    _uiState.value = ImportPrivateKeyUiState.InputPhase(
                        privateKey = privateKey,
                        validation = validatePrivateKey(privateKey),
                        error = "No accounts found for this private key",
                        contentVisible = currentContentVisible,
                        isTermsAgreed = currentTermsAgreed,
                        isKeyVisible = currentKeyVisible
                    )
                }
            } catch (e: Exception) {
                val currentContentVisible = _uiState.value.contentVisible
                val currentTermsAgreed = _uiState.value.isTermsAgreed
                val currentKeyVisible = _uiState.value.isKeyVisible
                
                _uiState.value = ImportPrivateKeyUiState.InputPhase(
                    privateKey = privateKey,
                    validation = validatePrivateKey(privateKey),
                    error = e.message ?: "Failed to discover accounts",
                    contentVisible = currentContentVisible,
                    isTermsAgreed = currentTermsAgreed,
                    isKeyVisible = currentKeyVisible
                )
            }
        }
    }
    
    fun onImportAccount() {
        val currentState = _uiState.value
        if (currentState is ImportPrivateKeyUiState.AccountsFound && currentState.selectedAccount != null) {
            importSelectedAccount(currentState)
        }
    }
    
    private fun importSelectedAccount(state: ImportPrivateKeyUiState.AccountsFound) {
        screenModelScope.launch {
            try {
                _uiState.value = ImportPrivateKeyUiState.Importing(
                    privateKey = state.privateKey,
                    accountName = state.selectedAccount!!
                )
                
                val selectedAccountAuthorizers = state.accountsByAuthorizers
                    .filter { it.accountName == state.selectedAccount }
                val blockchainType = selectedAccountAuthorizers.firstOrNull()?.blockchainUid?.let {
                    BlockchainType.fromUid(it)
                }
                
                importAccountUseCase(
                    accountName = state.selectedAccount,
                    privateKey = state.privateKey,
                    authorizers = selectedAccountAuthorizers,
                    isTemp = true,
                    blockchainType = blockchainType
                ).fold(
                    onSuccess = {
                        val isPinSetup = getIsPinSetupUseCase()
                        _uiState.value = ImportPrivateKeyUiState.AccountCreated(
                            accountName = state.selectedAccount,
                            isPinSetup = isPinSetup
                        )
                        val blockchainNetworkData = blockchainType?.let {
                            BlockchainNetworkData.getBlockchainByUid(it.uid, buildEnvironmentProvider.isDevelopmentEnvironment())
                        }
                        blockchainNetworkData?.let { saveSelectedNetworkUseCase(it) }
                        handleSuccessNavigation(state.selectedAccount, isPinSetup)
                    },
                    onFailure = { error ->
                        _uiState.value = ImportPrivateKeyUiState.ImportError(
                            privateKey = state.privateKey,
                            error = error.message ?: "Failed to import account"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = ImportPrivateKeyUiState.ImportError(
                    privateKey = state.privateKey,
                    error = e.message ?: "Import failed"
                )
            }
        }
    }
    
    private suspend fun handleSuccessNavigation(accountName: String, isPinSetup: Boolean) {
        if (isPinSetup) {
            // Update account status and go to home
            updateAccountStatusUseCase(
                accountName = accountName,
                isTemp = false,
                blockchainType = getSelectedNetworkUseCase().blockchainType,
                createAccountState = com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount.CreateAccountState.DONE
            )
            _navigationState.value = NavigationState.NavigateToHome
        } else {
            // Navigate to PIN setup
            _navigationState.value = NavigationState.NavigateToSetupPin(
                accountName = accountName,
                blockchainUid = getSelectedNetworkUseCase().blockchainType.uid
            )
        }
    }
    
    
    fun onBackToInput() {
        val currentState = _uiState.value
        when (currentState) {
            is ImportPrivateKeyUiState.ImportError -> {
                _uiState.value = ImportPrivateKeyUiState.InputPhase(
                    privateKey = currentState.privateKey,
                    validation = validatePrivateKey(currentState.privateKey)
                )
            }
            else -> {}
        }
    }
    
    fun clearNavigationState() {
        _navigationState.value = null
        _uiState.value = ImportPrivateKeyUiState.InputPhase()
    }
    
    fun setContentVisible(visible: Boolean) {
        _uiState.value = when (val current = _uiState.value) {
            is ImportPrivateKeyUiState.InputPhase -> current.copy(contentVisible = visible)
            is ImportPrivateKeyUiState.AccountsFound -> current.copy(contentVisible = visible)
            is ImportPrivateKeyUiState.Importing -> current.copy(contentVisible = visible)
            is ImportPrivateKeyUiState.AccountCreated -> current.copy(contentVisible = visible)
            is ImportPrivateKeyUiState.ImportError -> current.copy(contentVisible = visible)
        }
    }
    
    fun toggleKeyVisibility() {
        _uiState.value = when (val current = _uiState.value) {
            is ImportPrivateKeyUiState.InputPhase -> current.copy(isKeyVisible = !current.isKeyVisible)
            is ImportPrivateKeyUiState.AccountsFound -> current.copy(isKeyVisible = !current.isKeyVisible)
            is ImportPrivateKeyUiState.Importing -> current.copy(isKeyVisible = !current.isKeyVisible)
            is ImportPrivateKeyUiState.AccountCreated -> current.copy(isKeyVisible = !current.isKeyVisible)
            is ImportPrivateKeyUiState.ImportError -> current.copy(isKeyVisible = !current.isKeyVisible)
        }
    }
    
    fun setTermsAgreed(agreed: Boolean) {
        _uiState.value = when (val current = _uiState.value) {
            is ImportPrivateKeyUiState.InputPhase -> current.copy(isTermsAgreed = agreed)
            is ImportPrivateKeyUiState.AccountsFound -> current.copy(isTermsAgreed = agreed)
            is ImportPrivateKeyUiState.Importing -> current.copy(isTermsAgreed = agreed)
            is ImportPrivateKeyUiState.AccountCreated -> current.copy(isTermsAgreed = agreed)
            is ImportPrivateKeyUiState.ImportError -> current.copy(isTermsAgreed = agreed)
        }
    }
}

sealed interface NavigationState {
    data object NavigateToHome : NavigationState
    data class NavigateToSetupPin(
        val accountName: String,
        val blockchainUid: String
    ) : NavigationState
}

data class PrivateKeyValidation(
    val correctPrefix: Boolean = false,
    val isValidWif: Boolean = false
) {
    fun isValid(): Boolean = correctPrefix && isValidWif
}