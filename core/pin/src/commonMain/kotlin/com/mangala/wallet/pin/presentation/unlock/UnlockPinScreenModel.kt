package com.mangala.wallet.pin.presentation.unlock

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.pin.domain.PINManager
import com.mangala.wallet.pin.domain.PINValidationResult
import com.mangala.wallet.pin.presentation.base.BasePinScreenModel
import com.mangala.wallet.pin.presentation.base.PinConstants.PIN_DELAY_ENTERED
import com.mangala.wallet.pin.presentation.base.PinScreenFlow
import com.mangala.wallet.pin.presentation.base.PinState
import com.mangala.wallet.pin.presentation.base.PinUnlockCallbacks
import com.mangala.wallet.ui.SharedScreen
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject

class UnlockPinScreenModel(
    private val unlockPinCase: Int? = null,
    private val callbacks: PinUnlockCallbacks? = null,
    private val showForgotPinOption: Boolean = true
) : BasePinScreenModel() {
    private val pinManager: PINManager by inject()
    private val secureStorageWrapper: SecureStorageWrapper by inject()

    protected val mutableShowErrorMessage: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showErrorMessage: StateFlow<Boolean> = mutableShowErrorMessage.asStateFlow()

    protected val mutableShowForgotPin: MutableStateFlow<Boolean> = MutableStateFlow(showForgotPinOption)
    val showForgotPin: StateFlow<Boolean> = mutableShowForgotPin.asStateFlow()

    init {
        // Initialize showForgotPin based on unlockPinCase for backward compatibility
        if (unlockPinCase != null) {
            when(unlockPinCase) {
                SharedScreen.UnlockPinScreen.OPEN_APP,
                SharedScreen.UnlockPinScreen.CHANGE_PIN,
                SharedScreen.UnlockPinScreen.ENABLE_BIOMETRY -> {
                    mutableShowForgotPin.value = true
                }
                else -> {
                    mutableShowForgotPin.value = false
                }
            }
        }
    }

    protected val mutableIncorrectAttempts: MutableStateFlow<Int> = MutableStateFlow(0)
    val incorrectAttempts: StateFlow<Int> = mutableIncorrectAttempts.asStateFlow()

    override fun onDispose() {
        super.onDispose()
    }



    override fun onPinEntered(pin: String) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(PIN_DELAY_ENTERED)
            if (validatePin(pin)) {
                resetIncorrectAttempts()
                // New callback approach
                if (callbacks != null) {
                    callbacks.onSuccess()
                } else {
                    // Old navigation approach
                    navigateScreen()
                }
            } else {
                enableAnimationShakePin()
                mutableIncorrectAttempts.value++
                checkLockPin()
                resetPinEntered()
            }
        }
    }

    private fun checkLockPin() {
        if (mutableIncorrectAttempts.value >= MAX_ATTEMPTS) {
            showErrorMessage()
            disableKeyPad()
        } else {
            hideErrorMessage()
            enableKeyPad()
        }
        saveData()
    }

    internal fun navigateScreen() {
        // New callback approach
        if (callbacks != null) {
            callbacks.onSuccess()
            return
        }

        // Old navigation approach for backward compatibility
        if (unlockPinCase != null) {
            when(unlockPinCase) {
                SharedScreen.UnlockPinScreen.OPEN_APP -> {
                    showHomeScreen()
                }
                SharedScreen.UnlockPinScreen.CHANGE_PIN -> {
                    showSetupPinScreen()
                }
                SharedScreen.UnlockPinScreen.SHOW_WORDS_PHRASE -> {
                    showRecoveryPhraseScreen()
                }
                SharedScreen.UnlockPinScreen.ADD_ACCOUNT -> {
                    showAddAccountScreen()
                }
                SharedScreen.UnlockPinScreen.ADD_ACCOUNT_BITCOIN -> {
                    showAddAccountBitcoinScreen()
                }
                SharedScreen.UnlockPinScreen.CONFIRM_DAPP -> {
                    showConfirmDappScreen()
                }
                SharedScreen.UnlockPinScreen.ENABLE_BIOMETRY -> {
                    showEnableBiometryScreen()
                }
                SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION -> {
                    showVerifyAndSendScreen()
                }
                SharedScreen.UnlockPinScreen.BACKUP_ANTELOPE_ACCOUNT -> {
                    showBackupAntelopeAccountScreen()
                }
                else -> {
                }
            }
        }
    }

    internal fun checkPinIsSetUp() {
        if (!pinManager.isPINSetup()) {
            // New callback approach
            if (callbacks != null) {
                callbacks.onError("PIN not setup")
                return
            }

            // Old navigation approach
            if (unlockPinCase != null) {
                if (unlockPinCase == SharedScreen.UnlockPinScreen.OPEN_APP) {
                    showHomeScreen()
                } else if (unlockPinCase == SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION) {
                    showSetupPinAndContinueScreen()
                }
            }
        }
    }

    // Navigation methods for old screens - keep for backward compatibility
    private fun showHomeScreen(){
        mutablePinScreenFlowState.value = PinScreenFlow.ShowHomeScreen
    }

    private fun showRecoveryPhraseScreen(){
        mutablePinScreenFlowState.value = PinScreenFlow.ShowRecoveryPhraseScreen
    }

    private fun showAddAccountScreen() {
        mutablePinScreenFlowState.value = PinScreenFlow.ShowAddAccountScreen
    }

    private fun showAddAccountBitcoinScreen() {
        mutablePinScreenFlowState.value = PinScreenFlow.ShowBitcoinAddAccountScreen
    }

    private fun showConfirmDappScreen(){
        mutablePinScreenFlowState.value = PinScreenFlow.ConfirmDappScreen
    }

    private fun showEnableBiometryScreen() {
        mutablePinScreenFlowState.value = PinScreenFlow.ShowEnableBiometryScreen
    }

    private fun showVerifyAndSendScreen() {
        mutablePinScreenFlowState.value = PinScreenFlow.ShowVerifyAndSendScreen
    }

    private fun showBackupAntelopeAccountScreen() {
        mutablePinScreenFlowState.value = PinScreenFlow.BackupAntelopeAccountScreen
    }

    private fun showSetupPinAndContinueScreen() {
        mutablePinScreenFlowState.value = PinScreenFlow.ShowSetUpPinAndContinueScreen
    }

    private fun showErrorMessage() {
        mutablePinState.value = PinState.LOCKED
        mutableShowErrorMessage.value = true
    }

    private fun hideErrorMessage() {
        mutablePinState.value = PinState.UNLOCKING
        mutableShowErrorMessage.value = false
    }

    internal fun resetIncorrectAttempts() {
        enableKeyPad()
        hideErrorMessage()
        mutableIncorrectAttempts.value = 0
        saveData()
    }

    private fun validatePin(pin: String): Boolean {
        val pinChars = pin.toCharArray()

        try {
            val result = pinManager.validatePIN(pinChars)

            return result.fold(
                onSuccess = { validationResult ->
                    when (validationResult) {
                        is PINValidationResult.Success -> true
                        is PINValidationResult.Invalid -> {
                            // Update UI with remaining attempts
                            mutableIncorrectAttempts.value = MAX_ATTEMPTS - validationResult.remainingAttempts
                            callbacks?.onError("Invalid PIN. ${validationResult.remainingAttempts} attempts remaining")
                            false
                        }
                        is PINValidationResult.Locked -> {
                            // User is locked out
                            showErrorMessage()
                            disableKeyPad()
                            callbacks?.onLocked(validationResult.unlockTime.toString(), "")
                            false
                        }
                        is PINValidationResult.RateLimited -> {
                            // Rate limited, wait before retry
                            callbacks?.onRateLimited(validationResult.retryAfter.inWholeSeconds)
                            false
                        }
                    }
                },
                onFailure = { error ->
                    callbacks?.onError(error.message ?: "PIN validation failed")
                    false
                }
            )
        } catch (e: Exception) {
            println("Error validating PIN: ${e.message}")
            callbacks?.onError(e.message ?: "Unknown error")
            return false
        }
    }

    private fun getPin(): String {
        // Keep old method for checking if PIN is setup
        // But use PINManager for actual validation
        return if (pinManager.isPINSetup()) "exists" else ""
    }

    private fun saveData() {
        secureStorageWrapper.saveValue("unlock_pin_state", pinState.value.state)
        secureStorageWrapper.saveValue("incorrect_attempts_pin", mutableIncorrectAttempts.value.toString())
    }

    override fun restoreData() {
        val state = secureStorageWrapper.getValue(UNLOCK_PIN_STATE_PREFERENCES_KEY) ?: ""
        val pinState = PinState.fromString(state)

        val incorrectCount = secureStorageWrapper.getValue("incorrect_attempts_pin") ?: ""
        if (incorrectCount.isNotEmpty()) {
            mutableIncorrectAttempts.value = incorrectCount.toInt()
        }

        if (pinState == PinState.LOCKED) {
            checkLockPin()
        } else {
            mutablePinState.value = PinState.UNLOCKING
        }
    }

    companion object {
        private const val MAX_ATTEMPTS = 5
    }
}

class CountdownTimer(
    private val durationSeconds: Long,
    private val onTick: (Long) -> Unit,
    private val onFinish: () -> Unit
) {
    private var job: Job? = null

    internal fun start() {
        job?.cancel()
        job = GlobalScope.launch {
            for (i in durationSeconds downTo 0) {
                delay(1000)
                onTick(i)
            }
            onFinish()
        }
    }

    internal fun cancel() {
        job?.cancel()
    }
}