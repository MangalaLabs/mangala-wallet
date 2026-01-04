package com.mangala.wallet.biometry.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import com.mangala.wallet.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class IBiometryScreenModel {

    internal val _enableBiometry = MutableStateFlow(BiometryState.NONE)
    val enableBiometry: StateFlow<BiometryState> get() = _enableBiometry

    internal val _enableBiometricFlow = MutableStateFlow(false)
    val enableBiometricFlow: StateFlow<Boolean> get() = _enableBiometricFlow

    abstract fun tryToAuth(title: String, reason: String, button: String)

    abstract fun resetBiometryState()

    abstract fun isBiometricAvailable(): Boolean
    abstract fun bioMetricByDevice(): BiometryByDevice
    abstract fun enableBiometric(): Boolean
    abstract fun enableBiometric(value: Boolean)
}

expect class BiometryScreenModel constructor() : IBiometryScreenModel, KoinComponent {
    override fun tryToAuth(title: String, reason: String, button: String)
    override fun resetBiometryState()
    override fun isBiometricAvailable(): Boolean
    override fun bioMetricByDevice(): BiometryByDevice
    override fun enableBiometric(): Boolean
    override fun enableBiometric(value: Boolean)
}

