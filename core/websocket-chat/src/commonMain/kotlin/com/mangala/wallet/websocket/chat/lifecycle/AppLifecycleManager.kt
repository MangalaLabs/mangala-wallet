package com.mangala.wallet.websocket.chat.lifecycle

import kotlinx.coroutines.flow.Flow

/**
 * Manages app lifecycle events across platforms
 */
interface AppLifecycleManager {
    /**
     * Observable app state changes
     */
    fun observeAppState(): Flow<AppState>
    
    /**
     * Current app state
     */
    fun getCurrentAppState(): AppState
    
    /**
     * Time remaining for background execution (iOS)
     */
    fun getBackgroundTimeRemaining(): Double
    
    /**
     * Request extended background time
     */
    suspend fun requestBackgroundTime(task: suspend () -> Unit): Boolean
    
    /**
     * Register lifecycle observer
     */
    fun registerLifecycleObserver(observer: AppLifecycleObserver)
    
    /**
     * Unregister lifecycle observer
     */
    fun unregisterLifecycleObserver(observer: AppLifecycleObserver)
}

/**
 * App lifecycle states
 */
enum class AppState {
    FOREGROUND,     // App is visible and active
    BACKGROUND,     // App is in background
    INACTIVE,       // App is transitioning (iOS)
    TERMINATED      // App is being terminated
}

/**
 * App lifecycle events
 */
sealed class AppLifecycleEvent {
    object OnStart : AppLifecycleEvent()
    object OnStop : AppLifecycleEvent()
    object OnResume : AppLifecycleEvent()
    object OnPause : AppLifecycleEvent()
    data class OnTrimMemory(val level: Int) : AppLifecycleEvent()
    object OnLowMemory : AppLifecycleEvent()
    object WillTerminate : AppLifecycleEvent()
    data class BackgroundTimeExpiring(val timeRemaining: Double) : AppLifecycleEvent()
}

/**
 * Observer for app lifecycle events
 */
interface AppLifecycleObserver {
    fun onAppStateChanged(state: AppState)
    fun onLifecycleEvent(event: AppLifecycleEvent)
}

/**
 * Background task priorities
 */
enum class BackgroundTaskPriority {
    CRITICAL,   // Must complete (e.g., send urgent message)
    HIGH,       // Should complete (e.g., sync messages)
    NORMAL,     // Nice to have (e.g., cleanup)
    LOW         // Can be deferred
}

/**
 * Background task configuration
 */
data class BackgroundTaskConfig(
    val identifier: String,
    val priority: BackgroundTaskPriority,
    val requiresNetwork: Boolean = true,
    val requiresCharging: Boolean = false,
    val maxDuration: Long = 30_000, // milliseconds
    val canRunInDoze: Boolean = false // Android
)