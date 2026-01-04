package com.mangala.wallet.websocket.chat.lifecycle

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
class IosAppLifecycleManager : AppLifecycleManager {
    private val appStateFlow = MutableStateFlow(AppState.FOREGROUND)
    private val observers = mutableListOf<AppLifecycleObserver>()
    private var backgroundTaskIdentifier: UIBackgroundTaskIdentifier = UIBackgroundTaskInvalid
    
    init {
        setupLifecycleObservers()
    }
    
    override fun observeAppState(): Flow<AppState> = appStateFlow.asStateFlow()
    
    override fun getCurrentAppState(): AppState = appStateFlow.value
    
    override fun getBackgroundTimeRemaining(): Double {
        return UIApplication.sharedApplication.backgroundTimeRemaining
    }
    
    override suspend fun requestBackgroundTime(task: suspend () -> Unit): Boolean {
        return withContext(Dispatchers.Main) {
            suspendCoroutine { continuation ->
                val app = UIApplication.sharedApplication
                
                backgroundTaskIdentifier = app.beginBackgroundTaskWithExpirationHandler {
                    // Clean up when time expires
                    if (backgroundTaskIdentifier != UIBackgroundTaskInvalid) {
                        app.endBackgroundTask(backgroundTaskIdentifier)
                        backgroundTaskIdentifier = UIBackgroundTaskInvalid
                    }
                }
                
                if (backgroundTaskIdentifier != UIBackgroundTaskInvalid) {
                    // Execute the task
                    dispatch_async(dispatch_get_main_queue()) {
                        kotlinx.coroutines.GlobalScope.launch {
                            try {
                                task()
                            } finally {
                                // End the background task
                                if (backgroundTaskIdentifier != UIBackgroundTaskInvalid) {
                                    app.endBackgroundTask(backgroundTaskIdentifier)
                                    backgroundTaskIdentifier = UIBackgroundTaskInvalid
                                }
                            }
                        }
                    }
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
        }
    }
    
    override fun registerLifecycleObserver(observer: AppLifecycleObserver) {
        observers.add(observer)
        // Notify the observer of the current state
        observer.onAppStateChanged(appStateFlow.value)
    }
    
    override fun unregisterLifecycleObserver(observer: AppLifecycleObserver) {
        observers.remove(observer)
    }
    
    private fun setupLifecycleObservers() {
        val notificationCenter = NSNotificationCenter.defaultCenter
        
        // App will enter foreground
        notificationCenter.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                handleAppWillEnterForeground()
            }
        )
        
        // App did become active
        notificationCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                handleAppDidBecomeActive()
            }
        )
        
        // App will resign active
        notificationCenter.addObserverForName(
            name = UIApplicationWillResignActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                handleAppWillResignActive()
            }
        )
        
        // App did enter background
        notificationCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                handleAppDidEnterBackground()
            }
        )
        
        // App will terminate
        notificationCenter.addObserverForName(
            name = UIApplicationWillTerminateNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                handleAppWillTerminate()
            }
        )
        
        // Memory warning
        notificationCenter.addObserverForName(
            name = UIApplicationDidReceiveMemoryWarningNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                handleMemoryWarning()
            }
        )
        
        // Background refresh status changed
        notificationCenter.addObserverForName(
            name = UIApplicationBackgroundRefreshStatusDidChangeNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                checkBackgroundTimeExpiring()
            }
        )
    }
    
    private fun handleAppWillEnterForeground() {
        // App is transitioning from background to foreground
        appStateFlow.value = AppState.INACTIVE
        observers.forEach { it.onAppStateChanged(AppState.INACTIVE) }
    }
    
    private fun handleAppDidBecomeActive() {
        // App is now active and in foreground
        appStateFlow.value = AppState.FOREGROUND
        observers.forEach { 
            it.onAppStateChanged(AppState.FOREGROUND)
            it.onLifecycleEvent(AppLifecycleEvent.OnResume)
        }
    }
    
    private fun handleAppWillResignActive() {
        // App is about to move from active to inactive state
        appStateFlow.value = AppState.INACTIVE
        observers.forEach { it.onAppStateChanged(AppState.INACTIVE) }
    }
    
    private fun handleAppDidEnterBackground() {
        // App is now in background
        appStateFlow.value = AppState.BACKGROUND
        observers.forEach { 
            it.onAppStateChanged(AppState.BACKGROUND)
            it.onLifecycleEvent(AppLifecycleEvent.OnPause)
        }
        
        // Check background time
        checkBackgroundTimeExpiring()
    }
    
    private fun handleAppWillTerminate() {
        // App is about to terminate
        appStateFlow.value = AppState.TERMINATED
        observers.forEach { 
            it.onAppStateChanged(AppState.TERMINATED)
            it.onLifecycleEvent(AppLifecycleEvent.WillTerminate)
            it.onLifecycleEvent(AppLifecycleEvent.OnStop)
        }
    }
    
    private fun handleMemoryWarning() {
        // System is running low on memory
        dispatch_async(dispatch_get_main_queue()) {
            observers.forEach { observer ->
                observer.onLifecycleEvent(AppLifecycleEvent.OnLowMemory)
            }
        }
    }
    
    private fun checkBackgroundTimeExpiring() {
        val timeRemaining = UIApplication.sharedApplication.backgroundTimeRemaining
        if (timeRemaining < 30.0 && timeRemaining > 0) {
            observers.forEach { observer ->
                observer.onLifecycleEvent(AppLifecycleEvent.BackgroundTimeExpiring(timeRemaining))
            }
        }
    }
}

// UIBackgroundTaskIdentifier constants
private val UIBackgroundTaskInvalid: UIBackgroundTaskIdentifier = 0uL