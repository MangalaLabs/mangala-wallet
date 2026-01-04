package com.mangala.wallet.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class AppLifecycleObserver {
    actual var isAppOpen: Boolean = false

    private val _appLifecycleStateFlow = MutableStateFlow(AppLifecycleState.OPENED)
    actual val appLifecycleStateFlow: StateFlow<AppLifecycleState> = _appLifecycleStateFlow.asStateFlow()

    actual fun onAppOpened() {
        isAppOpen = true
        _appLifecycleStateFlow.value = AppLifecycleState.OPENED
    }

    actual fun onAppClosed() {
        isAppOpen = false
        _appLifecycleStateFlow.value = AppLifecycleState.CLOSED
    }

    actual fun onAppForeground() {
        _appLifecycleStateFlow.value = AppLifecycleState.FOREGROUND
    }

    actual fun onAppBackground() {
        _appLifecycleStateFlow.value = AppLifecycleState.BACKGROUND
    }
}