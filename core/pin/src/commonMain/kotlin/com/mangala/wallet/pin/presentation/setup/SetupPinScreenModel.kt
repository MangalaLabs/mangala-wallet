package com.mangala.wallet.pin.presentation.setup

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.pin.presentation.base.BasePinScreenModel
import com.mangala.wallet.pin.presentation.base.PinConstants.PIN_DELAY_ENTERED
import com.mangala.wallet.pin.presentation.base.PinState
import com.mangala.wallet.ui.SharedScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SetupPinScreenModel(
    val pinCase: SharedScreen.SetupPinScreen.SetupPinScreenCase
) : BasePinScreenModel() {

    override fun restoreData() {
        mutablePinState.value = PinState.SETUP_PIN
    }

    override fun onPinEntered(pin: String) {
        screenModelScope.launch {
            delay(PIN_DELAY_ENTERED)
            showConfirmPinScreen()
        }
    }
}