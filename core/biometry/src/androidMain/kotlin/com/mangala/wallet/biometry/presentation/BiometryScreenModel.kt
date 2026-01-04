package com.mangala.wallet.biometry.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import java.io.Serializable

actual class BiometryScreenModel: IBiometryScreenModel(), KoinComponent, Serializable {

    private val biometryAuthenticator: BiometryAuthenticator by inject()

    private val secureStorageWrapper: SecureStorageWrapper by inject()

    init {
        _enableBiometricFlow.value = enableBiometric()
    }

    actual override fun tryToAuth(title: String, reason: String, button: String){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val isSuccess = biometryAuthenticator?.checkBiometryAuthentication(
                    requestTitle = title.desc(),
                    requestReason = reason.desc(),
                    failureButtonText = button.desc()
                )
                if (isSuccess == true) {
                    _enableBiometry.value = BiometryState.SUCCESS
                }else{
                    _enableBiometry.value = BiometryState.FAIL
                }
            } catch (throwable: Throwable) {
                _enableBiometry.value = BiometryState.FAIL
            }
        }
    }

    actual override fun resetBiometryState() {
        _enableBiometry.value = BiometryState.NONE
    }

    actual override fun isBiometricAvailable(): Boolean {
        return biometryAuthenticator.isBiometricAvailable()
    }

    actual override fun bioMetricByDevice(): BiometryByDevice {
        return BiometryByDevice.ANDROID_FINGERPRINT
    }

    actual override fun enableBiometric(): Boolean {
        val value = secureStorageWrapper.getValue("enable_biometric") ?: ""
        return value == "true"
    }

    actual override fun enableBiometric(value: Boolean) {
        secureStorageWrapper.saveValue("enable_biometric", value.toString())
        _enableBiometricFlow.value = value
    }

}