package com.mangala.wallet.pin.presentation.base

import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

abstract class BasePinScreenModel: BaseScreenModel(), KoinComponent {

    protected val mutablePinState: MutableStateFlow<PinState> = MutableStateFlow(PinState.EMPTY)
    val pinState: StateFlow<PinState> = mutablePinState.asStateFlow()

    protected val mutablePinScreenFlowState: MutableStateFlow<PinScreenFlow> = MutableStateFlow(PinScreenFlow.ShowCurrentScreen)
    val pinScreenFlowState: StateFlow<PinScreenFlow> = mutablePinScreenFlowState.asStateFlow()

    private val _pinEntered = MutableStateFlow("")
    val pinEntered: StateFlow<String> = _pinEntered.asStateFlow()

    protected val _keyPadEnabled = MutableStateFlow(true)
    val keyPadEnabled: StateFlow<Boolean> = _keyPadEnabled.asStateFlow()

    protected val _enableAnimationShakePin = MutableStateFlow(false)
    val enableAnimationShakePin: StateFlow<Boolean> = _enableAnimationShakePin.asStateFlow()

    protected val _keyPadClickBiometry = MutableStateFlow(false)
    val keyPadClickBiometry: StateFlow<Boolean> = _keyPadClickBiometry.asStateFlow()

    internal fun onStarted() {
        resetPinEntered()
        restoreData()
        resetKeyPadClickBiometry()
    }

    override fun onDispose() {
        super.onDispose()
    }

    fun resetPinEntered() {
        _pinEntered.value = ""
    }

    abstract fun onPinEntered(pin: String)

    fun onDigitPressed(digit: String) {
        if (_pinEntered.value.length < PinConstants.PIN_LENGTH) {
            _pinEntered.value += digit
            if (_pinEntered.value.length == PinConstants.PIN_LENGTH) {
                onPinEntered(_pinEntered.value)
            }
        }
    }

    fun onDelete(){
        if (_pinEntered.value.isNotEmpty()) {
            _pinEntered.value = _pinEntered.value.dropLast(1)
        }
    }

    fun onClickBiometry(){
        _keyPadClickBiometry.value = true
    }

    fun resetKeyPadClickBiometry(){
        _keyPadClickBiometry.value = false
    }

    protected fun enableKeyPad(){
        _keyPadEnabled.value = true
    }

    protected fun disableKeyPad(){
        _keyPadEnabled.value = false
    }

    abstract protected fun restoreData()

    internal fun setPinScreenFlowState(pinScreenFlow: PinScreenFlow){
        mutablePinScreenFlowState.value = pinScreenFlow
    }

    // Internal PIN module navigation - keep these for within-module navigation
    internal fun showForgotPinScreen(){
        mutablePinScreenFlowState.value = PinScreenFlow.ShowForgotPinScreen
    }

    protected fun showConfirmPinScreen(){
        mutablePinScreenFlowState.value = PinScreenFlow.ShowConfirmPinScreen
    }

    protected fun showSetupPinScreen(){
        mutablePinScreenFlowState.value = PinScreenFlow.ShowSetUpPinScreen
    }

    internal fun showUnlockPinScreen(){
        mutablePinScreenFlowState.value = PinScreenFlow.ShowUnlockPinScreen
    }

    internal fun showLockScreen() {
        mutablePinScreenFlowState.value = PinScreenFlow.ShowLockScreen
    }

    internal fun resetPinScreenFlowState(){
        mutablePinScreenFlowState.value = PinScreenFlow.ShowCurrentScreen
    }
    
    /**
     * Deferred reset that executes after a short delay to ensure proper navigation
     * Uses the screen's lifecycle scope for proper cancellation
     */
    internal fun resetPinScreenFlowStateDeferred(delayMs: Long = 100L) {
        lifecycleScope.launch {
            delay(delayMs)
            mutablePinScreenFlowState.value = PinScreenFlow.ShowCurrentScreen
        }
    }

    protected fun enableAnimationShakePin(){
        _enableAnimationShakePin.value = true
    }

    fun disableAnimationShakePin(){
        _enableAnimationShakePin.value = false
    }

    companion object {
        internal const val UNLOCK_PIN_STATE_PREFERENCES_KEY = "unlock_pin_state"
    }
}