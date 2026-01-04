package com.mangala.wallet.biometry.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import dev.icerock.moko.resources.desc.desc
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.LocalAuthentication.LABiometryTypeFaceID
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicy
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.UIKit.UIDevice

@OptIn(ExperimentalForeignApi::class)
actual class BiometryScreenModel: IBiometryScreenModel(), KoinComponent{

    private val secureStorageWrapper: SecureStorageWrapper by inject()

    private val biometryAuthenticator: BiometryAuthenticator by inject()

    init {
        _enableBiometricFlow.value = enableBiometric()
    }

    actual override fun tryToAuth(title: String, reason: String, button: String){
        _enableBiometry.value = BiometryState.UNLOCKING
        CoroutineScope(Dispatchers.Default).launch {
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
        return if(isFaceIdSupported()) BiometryByDevice.IOS_FACE_ID else BiometryByDevice.IOS_TOUCH_ID
    }

    private fun isFaceIdSupported(): Boolean {
        val deviceModel = UIDevice.currentDevice.model
        val context = LAContext()

        return if (deviceModel.contains("iPhone")) {
            if (context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)) {
                val biometryType = context.biometryType
                biometryType == LABiometryTypeFaceID
            } else {
                false
            }
        } else {
            false
        }
    }

    actual override fun enableBiometric(): Boolean {
        val value = secureStorageWrapper.getValue("enable_biometric_iphone") ?: ""
        return value == "true"
    }

    actual override fun enableBiometric(value: Boolean) {
        secureStorageWrapper.saveValue("enable_biometric_iphone", value.toString())
        _enableBiometricFlow.value = value
    }

}