package com.mangala.wallet.features.chains.antelope.create_account.presentation.step5

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.CompleteOnboardingUseCase
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Step5BackupOptionsScreenModel(
    private val accountName: String,
    private val accountSuffix: String,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(Step5BackupOptionsUiState())
    val uiState: StateFlow<Step5BackupOptionsUiState> = _uiState.asStateFlow()
    
    private val _navigationState = MutableStateFlow<NavigationState?>(null)
    val navigationState: StateFlow<NavigationState?> = _navigationState.asStateFlow()

    fun selectOption(option: BackupOption) {
        _uiState.update { it.copy(selectedOption = option) }
    }

    fun onContinueClick() {
        when (_uiState.value.selectedOption) {
            BackupOption.ICLOUD -> {
                // TODO: Navigate to iCloud backup flow
                handleICloudBackup()
            }
            BackupOption.RECOVERY_PHRASE -> {
                // TODO: Navigate to recovery phrase flow
                handleRecoveryPhraseBackup()
            }
            null -> {
                // No option selected
            }
        }
    }
    
    private fun handleICloudBackup() {
        screenModelScope.launch {
            // TODO: Implement iCloud Keychain backup
            println("Starting iCloud backup for $accountName$accountSuffix")
        }
    }
    
    private fun handleRecoveryPhraseBackup() {
        screenModelScope.launch {
            // TODO: Navigate to recovery phrase screen
            println("Showing recovery phrase for $accountName$accountSuffix")
        }
    }
    
    fun onContinueWithoutBackup() {
        screenModelScope.launch {
            val isPinSetup = getIsPinSetupUseCase()
            
            if (isPinSetup) {
                completeOnboardingUseCase()
                _navigationState.value = NavigationState.NavigateToHome
            } else {
                _navigationState.value = NavigationState.NavigateToSetupPin(
                    accountName = "$accountName$accountSuffix",
                    blockchainUid = "antelope" // Default blockchain UID for create account flow
                )
            }
        }
    }
    
    fun clearNavigationState() {
        _navigationState.value = null
    }
}

data class Step5BackupOptionsUiState(
    val selectedOption: BackupOption? = null,
    val isLoading: Boolean = false
)

enum class BackupOption {
    ICLOUD,
    RECOVERY_PHRASE
}

sealed interface NavigationState {
    data object NavigateToHome : NavigationState
    data class NavigateToSetupPin(
        val accountName: String,
        val blockchainUid: String
    ) : NavigationState
}