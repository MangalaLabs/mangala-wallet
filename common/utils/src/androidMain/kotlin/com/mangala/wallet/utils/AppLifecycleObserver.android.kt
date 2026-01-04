package com.mangala.wallet.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

actual class AppLifecycleObserver(application: Application) {

    actual var isAppOpen: Boolean = false

    private val _appLifecycleStateFlow = MutableStateFlow(AppLifecycleState.CLOSED)
    actual val appLifecycleStateFlow: StateFlow<AppLifecycleState> = _appLifecycleStateFlow.asStateFlow()
    init {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                onAppOpened()
            }

            override fun onActivityStarted(activity: Activity) {
                onAppForeground()
            }
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {
                onAppBackground()
            }
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                onAppClosed()
            }
        })
    }

    actual fun onAppOpened() {
        isAppOpen = true
        _appLifecycleStateFlow.update { AppLifecycleState.OPENED }
        println("App opened")
    }

    actual fun onAppClosed() {
        isAppOpen = false
        _appLifecycleStateFlow.update { AppLifecycleState.CLOSED }
        println("App closed")
    }

    actual fun onAppForeground() {
        _appLifecycleStateFlow.update { AppLifecycleState.FOREGROUND }
    }

    actual fun onAppBackground() {
        _appLifecycleStateFlow.update { AppLifecycleState.BACKGROUND }
    }
}