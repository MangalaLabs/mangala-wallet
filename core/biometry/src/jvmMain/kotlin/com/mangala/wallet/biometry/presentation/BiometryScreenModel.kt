package com.mangala.wallet.biometry.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import org.koin.core.component.KoinComponent

actual class BiometryScreenModel : IBiometryScreenModel(), KoinComponent {
    actual override fun tryToAuth(title: String, reason: String, button: String) {

    }

    actual override fun resetBiometryState() {
        _enableBiometry.value = BiometryState.NONE
    }

    actual override fun isBiometricAvailable(): Boolean {
        return false
    }

    actual override fun bioMetricByDevice(): BiometryByDevice {
        return BiometryByDevice.DESKTOP_FINGERPRINT
    }

    actual override fun enableBiometric(): Boolean {
       return false
    }

    actual override fun enableBiometric(value: Boolean) {

    }

}