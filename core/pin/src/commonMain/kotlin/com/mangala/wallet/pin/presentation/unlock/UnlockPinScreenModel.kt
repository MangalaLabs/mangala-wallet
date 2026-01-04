package com.mangala.wallet.pin.presentation.unlock

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants.PIN_KEY
import com.mangala.wallet.pin.presentation.base.BasePinScreenModel
import com.mangala.wallet.pin.presentation.base.PinConstants.PIN_DELAY_ENTERED
import com.mangala.wallet.pin.presentation.base.PinScreenFlow
import com.mangala.wallet.pin.presentation.base.PinState
import com.mangala.wallet.ui.SharedScreen
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject

class UnlockPinScreenModel(
    private val unlockPinCase: Int
) : BasePinScreenModel() {
    private val secureStorageWrapper: SecureStorageWrapper by inject()

    protected val mutableShowErrorMessage: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showErrorMessage: StateFlow<Boolean> = mutableShowErrorMessage.asStateFlow()

    protected val mutableShowForgotPin: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val showForgotPin: StateFlow<Boolean> = mutableShowForgotPin.asStateFlow()

    protected val mutableIncorrectAttempts: MutableStateFlow<Int> = MutableStateFlow(0)
    val incorrectAttempts: StateFlow<Int> = mutableIncorrectAttempts.asStateFlow()

    override fun onDispose() {
        super.onDispose()
    }

    init {
        when(unlockPinCase) {
            SharedScreen.UnlockPinScreen.OPEN_APP -> {
                mutableShowForgotPin.value = true
            }
            SharedScreen.UnlockPinScreen.CHANGE_PIN -> {
                mutableShowForgotPin.value = true
            }
            SharedScreen.UnlockPinScreen.SHOW_WORDS_PHRASE -> {
                mutableShowForgotPin.value = false
            }
            SharedScreen.UnlockPinScreen.ADD_ACCOUNT -> {
                mutableShowForgotPin.value = false
            }
            SharedScreen.UnlockPinScreen.ADD_ACCOUNT_BITCOIN -> {
                mutableShowForgotPin.value = false
            }
            SharedScreen.UnlockPinScreen.CONFIRM_DAPP -> {
                mutableShowForgotPin.value = false
            }
            SharedScreen.UnlockPinScreen.ENABLE_BIOMETRY -> {
                mutableShowForgotPin.value = true
            }
            SharedScreen.UnlockPinScreen.BACKUP_ANTELOPE_ACCOUNT -> {
                mutableShowForgotPin.value = false
            }
            else -> {

            }
        }
    }


    override fun onPinEntered(pin: String) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(PIN_DELAY_ENTERED)
            if (validatePin(pin)) {
                resetIncorrectAttempts()
                navigateScreen()
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

    internal fun checkPinIsSetUp() {
        if (getPin().isEmpty()) {
            if (unlockPinCase == SharedScreen.UnlockPinScreen.OPEN_APP) {
                showHomeScreen()
            } else if (unlockPinCase == SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION) {
                showSetupPinAndContinueScreen()
            }
        }
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
        return pin == getPin()
    }

    private fun getPin(): String {
        return secureStorageWrapper.getValue(PIN_KEY) ?: ""
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