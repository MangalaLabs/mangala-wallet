package com.mangala.wallet.features.chains.antelope.create_account.presentation.step4

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.antelope_key_manager.domain.usecase.GenerateAccountKeyPairsUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAndSaveAccountWithInAppPurchaseUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAccountWithInAppPurchaseUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.GetFirstUnassignedPurchaseUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Step4CreatingAccountScreenModel(
    private val accountName: String,
    private val accountSuffix: String,
    private val operationType: com.mangala.wallet.ui.SharedScreen.Step4CreatingAccountScreen.AccountOperationType = com.mangala.wallet.ui.SharedScreen.Step4CreatingAccountScreen.AccountOperationType.CREATE,
    private val createAndSaveAccountWithInAppPurchaseUseCase: CreateAndSaveAccountWithInAppPurchaseUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getFirstUnassignedPurchaseUseCase: GetFirstUnassignedPurchaseUseCase,
    private val generateAccountKeyPairsUseCase: GenerateAccountKeyPairsUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(Step4CreatingAccountUiState())
    val uiState: StateFlow<Step4CreatingAccountUiState> = _uiState.asStateFlow()
    
    private val _navigateToBackupScreen = MutableSharedFlow<Unit>(replay = 0)
    val navigateToBackupScreen: SharedFlow<Unit> = _navigateToBackupScreen.asSharedFlow()
    
    private var blockchainUid: String = ""
    private var newAccountKeyPairs: AccountKeyPairs? = null

    init {
        screenModelScope.launch {
            blockchainUid = getSelectedNetworkUseCase().blockChainUid
        }
    }
    
    fun startAccountCreation() {
        if (operationType == com.mangala.wallet.ui.SharedScreen.Step4CreatingAccountScreen.AccountOperationType.IMPORT) {
            startAccountImport()
        } else {
            startAccountCreate()
        }
    }
    
    private fun startAccountCreate() {
        screenModelScope.launch {
            try {
                // Step 1: Generate account keys
                _uiState.update { it.copy(step1Status = StepStatus.IN_PROGRESS) }
                animateProgress(0f, 0.33f)
                
                newAccountKeyPairs = generateAccountKeyPairsUseCase()
                delay(1000) // Show progress
                
                _uiState.update { it.copy(step1Status = StepStatus.COMPLETE) }
                
                // Step 2: Find purchase and prepare account creation
                _uiState.update { it.copy(step2Status = StepStatus.IN_PROGRESS) }
                animateProgress(0.33f, 0.66f)
                
                val purchase = getFirstUnassignedPurchaseUseCase(
                    accountNameWithSuffix = "$accountName$accountSuffix"
                ) ?: throw Exception("No valid purchase found")
                
                delay(1000) // Show progress
                _uiState.update { it.copy(step2Status = StepStatus.COMPLETE) }
                
                // Step 3: Create account on blockchain
                _uiState.update { it.copy(step3Status = StepStatus.IN_PROGRESS) }
                animateProgress(0.66f, 1f)
                
                val blockchainType = BlockchainType.fromUid(blockchainUid)
                val result = createAndSaveAccountWithInAppPurchaseUseCase(
                    accountName = "$accountName$accountSuffix",
                    blockchainType = blockchainType,
                    paymentInfo = purchase
                )
                
                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(step3Status = StepStatus.COMPLETE) }
                        delay(500)
                        onAccountCreationComplete()
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                step3Status = StepStatus.IN_PROGRESS,
                                error = getErrorMessage(error)
                            )
                        }
                    }
                )
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to create account: ${e.message}"
                    )
                }
            }
        }
    }
    
    private fun startAccountImport() {
        screenModelScope.launch {
            try {
                _uiState.update { it.copy(step1Status = StepStatus.IN_PROGRESS) }
                animateProgress(0f, 0.33f)
                
                delay(1500) // Simulate verification
                
                _uiState.update { it.copy(step1Status = StepStatus.COMPLETE) }
                
                _uiState.update { it.copy(step2Status = StepStatus.IN_PROGRESS) }
                animateProgress(0.33f, 0.66f)
                
                delay(1500) // Simulate checking
                
                _uiState.update { it.copy(step2Status = StepStatus.COMPLETE) }
                
                _uiState.update { it.copy(step3Status = StepStatus.IN_PROGRESS) }
                animateProgress(0.66f, 1f)
                
                updateAccountStatusUseCase(
                    accountName,
                    isTemp = false,
                    BlockchainType.fromUid(blockchainUid),
                    createAccountState = AntelopeAccount.CreateAccountState.DONE
                )
                
                _uiState.update { it.copy(step3Status = StepStatus.COMPLETE) }
                delay(500)

                onAccountCreationComplete()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to import account: ${e.message}"
                    )
                }
            }
        }
    }
    
    private fun getErrorMessage(error: Throwable): String {
        return when (error) {
            is CreateAccountWithInAppPurchaseUseCase.CreateAccountError.NetworkError -> 
                "Network error. Please check your connection and try again."
            is CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseAlreadyConsumed -> 
                "This purchase has already been used. Please contact support."
            is CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseCancelled -> 
                "Purchase was cancelled. Please try again."
            is CreateAccountWithInAppPurchaseUseCase.CreateAccountError.AntelopeNodeError -> 
                "Blockchain error. Please try again later."
            else -> "An unexpected error occurred: ${error.message}"
        }
    }
    
    private suspend fun animateProgress(from: Float, to: Float) {
        val duration = 2000L // 2 seconds
        val steps = 20
        val stepDelay = duration / steps
        val stepIncrement = (to - from) / steps
        
        repeat(steps) { step ->
            _uiState.update { 
                it.copy(progress = from + (stepIncrement * (step + 1)))
            }
            delay(stepDelay)
        }
    }
    
    private fun onAccountCreationComplete() {
        screenModelScope.launch {
            _navigateToBackupScreen.emit(Unit)
        }
    }
    
    fun retryAccountCreation() {
        _uiState.update { 
            it.copy(
                progress = 0f,
                step1Status = StepStatus.PENDING,
                step2Status = StepStatus.PENDING,
                step3Status = StepStatus.PENDING,
                error = null
            )
        }
        startAccountCreation()
    }
}

data class Step4CreatingAccountUiState(
    val progress: Float = 0f,
    val step1Status: StepStatus = StepStatus.PENDING,
    val step2Status: StepStatus = StepStatus.PENDING,
    val step3Status: StepStatus = StepStatus.PENDING,
    val error: String? = null
) {
    val isCompleted: Boolean
        get() = step1Status == StepStatus.COMPLETE && 
                step2Status == StepStatus.COMPLETE && 
                step3Status == StepStatus.COMPLETE && 
                progress >= 1f
                
    val hasError: Boolean
        get() = error != null
}