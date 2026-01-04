package com.mangala.wallet.pin.presentation.confirm

import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.biometry.presentation.IBiometryScreenModel
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants
import com.mangala.wallet.pin.presentation.base.BasePinScreenModel
import com.mangala.wallet.pin.presentation.base.PinConstants
import com.mangala.wallet.pin.presentation.base.PinScreenFlow
import com.mangala.wallet.pin.presentation.base.PinState
import com.mangala.wallet.ui.SharedScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class ConfirmPinScreenModel(
    val pin: String,
    private val pinCase: SharedScreen.SetupPinScreen.SetupPinScreenCase,
) : BasePinScreenModel() {

    private val secureStorageWrapper: SecureStorageWrapper by inject()
    private val biometryAuthenticator: BiometryAuthenticator by inject()
    private val biometryScreenModel: IBiometryScreenModel by inject()
    
    private val _shouldShowBiometricSetup = MutableStateFlow(false)
    val shouldShowBiometricSetup: StateFlow<Boolean> = _shouldShowBiometricSetup
    
    private val _biometricSetupComplete = MutableStateFlow(false)
    val biometricSetupComplete: StateFlow<Boolean> = _biometricSetupComplete

    override fun restoreData() {
        mutablePinState.value = PinState.CONFIRM_PIN
    }

    override fun onPinEntered(confirmPin: String) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(PinConstants.PIN_DELAY_ENTERED)
            if(validateConfirmPin(pin, confirmPin)){
                savePin(confirmPin)
                navigateScreen()
            }else{
                enableAnimationShakePin()
                resetPinEntered()
            }
        }
    }

    private fun validateConfirmPin(pin: String, confirmPin: String): Boolean {
        return pin == confirmPin
    }

    private fun savePin(pin: String) {
        secureStorageWrapper.saveValue(SecureStorageWrapperConstants.PIN_KEY, pin)
    }

    private fun navigateScreen() {
        val isBiometricAvailable = biometryAuthenticator.isBiometricAvailable()
        val isBiometricEnabled = biometryScreenModel.enableBiometric()

        if (isBiometricAvailable && !isBiometricEnabled) {
            _shouldShowBiometricSetup.value = true
        } else {
            proceedToNextScreen()
        }
    }

    fun proceedToNextScreen() {
        when(pinCase) {
            SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET -> {
                showCreateWalletScreen()
            }

            SharedScreen.SetupPinScreen.SetupPinScreenCase.CHANGE_PIN -> {
                showHomeScreen()
            }

            SharedScreen.SetupPinScreen.SetupPinScreenCase.RESTORE_WALLET -> {
                showPopFromSetupPinScreen()
            }

            SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN -> {
                showBackLastScreen()
            }

            SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_BACKUP_ANTELOPE -> {
                showBackupAntelopeAccountScreen()
            }

            SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_CONTINUE_HOME_SCREEN -> {
                showHomeScreen()
            }

            SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_CONTINUE -> {
                showSetupPinAndContinueScreen()
            }

            else -> TODO("Invalid case")
        }
    }
    
    fun onBiometricSetupComplete(enabled: Boolean) {
        _biometricSetupComplete.value = true
        _shouldShowBiometricSetup.value = false
        proceedToNextScreen()
    }
}