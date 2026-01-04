package com.mangala.wallet.utils

import kotlinx.coroutines.flow.StateFlow

expect class AppLifecycleObserver {

    var isAppOpen: Boolean
    val appLifecycleStateFlow: StateFlow<AppLifecycleState>

    fun onAppOpened()
    fun onAppClosed()
    fun onAppForeground()
    fun onAppBackground()
}

enum class AppLifecycleState {
    FOREGROUND,
    BACKGROUND,
    OPENED,
    CLOSED
}