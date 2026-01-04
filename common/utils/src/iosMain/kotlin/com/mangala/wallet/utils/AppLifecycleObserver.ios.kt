package com.mangala.wallet.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.UIKit.UIApplicationWillTerminateNotification
import platform.darwin.NSObject
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.flow.update

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
actual class AppLifecycleObserver {

    actual var isAppOpen: Boolean = false

    private val _appLifecycleStateFlow = MutableStateFlow(AppLifecycleState.CLOSED)
    actual val appLifecycleStateFlow: StateFlow<AppLifecycleState> = _appLifecycleStateFlow.asStateFlow()

    init {
        // Observe when the app enters the foreground
        NSNotificationCenter.defaultCenter.addObserver(
            observer = this,
            selector = null, // TODO: Reenable
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null
        )

        // Observe when the app goes to the background
        NSNotificationCenter.defaultCenter.addObserver(
            observer = this,
            selector =  null, // TODO: Reenable
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null
        )

        // Observe when the app becomes active (opened)
        NSNotificationCenter.defaultCenter.addObserver(
            observer = this,
            selector = null, // TODO: Reenable
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null
        )

        // Observe when the app is about to terminate (closed)
        NSNotificationCenter.defaultCenter.addObserver(
            observer = this,
            selector = null, // TODO: Reenable
            name = UIApplicationWillTerminateNotification,
            `object` = null
        )
    }

    @ObjCAction
    actual fun onAppOpened() {
        isAppOpen = true
        _appLifecycleStateFlow.update { AppLifecycleState.OPENED }
    }

    @ObjCAction
    actual fun onAppClosed() {
        isAppOpen = false
        _appLifecycleStateFlow.update { AppLifecycleState.CLOSED }
    }

    @ObjCAction
    actual fun onAppForeground() {
        _appLifecycleStateFlow.update { AppLifecycleState.FOREGROUND }
    }

    @ObjCAction
    actual fun onAppBackground() {
        _appLifecycleStateFlow.update { AppLifecycleState.BACKGROUND }
    }
}