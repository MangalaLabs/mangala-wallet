package com.mangala.wallet.websocket.chat.lifecycle

import com.mangala.wallet.websocket.chat.device.DeviceStateMonitor
import com.mangala.wallet.websocket.chat.device.DeviceStateRecommendation
import com.mangala.wallet.websocket.chat.network.NetworkMonitor
import com.mangala.wallet.websocket.chat.network.NetworkQuality
import com.mangala.wallet.websocket.chat.websocket.ConnectionManager
import com.mangala.wallet.websocket.chat.websocket.models.ConnectionState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Manages WebSocket connection with awareness of app lifecycle, network, and device state
 */
class LifecycleAwareConnectionManager(
    private val connectionManager: ConnectionManager,
    private val appLifecycleManager: AppLifecycleManager,
    private val networkMonitor: NetworkMonitor,
    private val deviceStateMonitor: DeviceStateMonitor,
    private val coroutineScope: CoroutineScope
) : AppLifecycleObserver {
    
    private val tag = "LifecycleAwareConnectionManager"
    
    // Configuration
    private var config = ConnectionConfig()
    
    // State tracking
    private var isUserInitiatedDisconnect = false
    private var lastDisconnectTime = 0L
    private var consecutiveFailures = 0
    
    // Jobs
    private var lifecycleMonitoringJob: Job? = null
    private var networkMonitoringJob: Job? = null
    private var deviceStateMonitoringJob: Job? = null
    private var backgroundTaskJob: Job? = null
    
    init {
        appLifecycleManager.registerLifecycleObserver(this)
    }
    
    fun startManaging() {
        Napier.d("Starting lifecycle-aware connection management", tag = tag)
        
        // Start monitoring network changes
        networkMonitoringJob = coroutineScope.launch {
            networkMonitor.observeNetworkState()
                .distinctUntilChanged()
                .collect { networkState ->
                    handleNetworkStateChange(networkState)
                }
        }
        
        // Start monitoring device state changes
        deviceStateMonitoringJob = coroutineScope.launch {
            deviceStateMonitor.getCurrentDeviceState()
                .let { deviceState ->
                    val recommendation = DeviceStateRecommendation.fromDeviceState(deviceState)
                    adjustConnectionBehavior(recommendation)
                }
        }
        
        // Monitor combined state for intelligent decisions
        lifecycleMonitoringJob = coroutineScope.launch {
            combine(
                appLifecycleManager.observeAppState(),
                networkMonitor.observeNetworkState(),
                deviceStateMonitor.getBatteryLevel()
            ) { appState, networkState, batteryLevel ->
                Triple(appState, networkState, batteryLevel)
            }.collect { (appState, networkState, batteryLevel) ->
                makeConnectionDecision(appState, networkState, batteryLevel)
            }
        }
        
        // Start network and device monitoring
        networkMonitor.startMonitoring()
        deviceStateMonitor.startMonitoring()
    }
    
    fun stopManaging() {
        Napier.d("Stopping lifecycle-aware connection management", tag = tag)
        
        lifecycleMonitoringJob?.cancel()
        networkMonitoringJob?.cancel()
        deviceStateMonitoringJob?.cancel()
        backgroundTaskJob?.cancel()
        
        networkMonitor.stopMonitoring()
        deviceStateMonitor.stopMonitoring()
        
        appLifecycleManager.unregisterLifecycleObserver(this)
    }
    
    override fun onAppStateChanged(state: AppState) {
        coroutineScope.launch {
            when (state) {
                AppState.FOREGROUND -> handleAppForeground()
                AppState.BACKGROUND -> handleAppBackground()
                AppState.INACTIVE -> handleAppInactive()
                AppState.TERMINATED -> handleAppTermination()
            }
        }
    }
    
    override fun onLifecycleEvent(event: AppLifecycleEvent) {
        when (event) {
            is AppLifecycleEvent.OnTrimMemory -> handleMemoryPressure(event.level)
            is AppLifecycleEvent.OnLowMemory -> handleLowMemory()
            is AppLifecycleEvent.BackgroundTimeExpiring -> handleBackgroundTimeExpiring(event.timeRemaining)
            else -> { /* Handle other events as needed */ }
        }
    }
    
    private suspend fun handleAppForeground() {
        Napier.d("App entered foreground", tag = tag)
        
        // Cancel any background disconnect timer
        backgroundTaskJob?.cancel()
        
        // Check if we should reconnect
        if (shouldReconnectOnForeground()) {
            connectionManager.connect()
        }
        
        // Reset to normal operation parameters
        config = config.copy(
            heartbeatInterval = 30.seconds,
            maxReconnectDelay = 60.seconds
        )
    }
    
    private suspend fun handleAppBackground() {
        Napier.d("App entered background", tag = tag)
        
        val deviceState = deviceStateMonitor.getCurrentDeviceState()
        val shouldMaintainConnection = shouldMaintainConnectionInBackground(deviceState)
        
        if (!shouldMaintainConnection) {
            // Schedule graceful disconnect
            scheduleBackgroundDisconnect()
        } else {
            // Adjust for background operation
            config = config.copy(
                heartbeatInterval = 60.seconds, // Reduce heartbeat frequency
                maxReconnectDelay = 5.minutes    // Less aggressive reconnection
            )
        }
    }
    
    private suspend fun handleAppInactive() {
        // iOS specific - app is transitioning
        Napier.d("App became inactive", tag = tag)
    }
    
    private suspend fun handleAppTermination() {
        Napier.d("App is terminating", tag = tag)
        
        // Quickly save any critical state
        isUserInitiatedDisconnect = true
        connectionManager.disconnect()
    }
    
    private fun handleNetworkStateChange(networkState: com.mangala.wallet.websocket.chat.network.NetworkState) {
        coroutineScope.launch {
            Napier.d("Network state changed: $networkState", tag = tag)
            
            val currentConnectionState = connectionManager.connectionState.value
            
            when {
                !networkState.isConnected && currentConnectionState == ConnectionState.CONNECTED -> {
                    // Lost network while connected
                    Napier.w("Network lost while connected", tag = tag)
                    // ConnectionManager will handle this via heartbeat failure
                }
                
                networkState.isConnected && currentConnectionState == ConnectionState.DISCONNECTED -> {
                    // Network became available while disconnected
                    if (shouldAutoReconnect()) {
                        delay(1.seconds) // Brief delay to let network stabilize
                        connectionManager.connect()
                    }
                }
                
                networkState.captivePortalDetected -> {
                    Napier.w("Captive portal detected, disconnecting", tag = tag)
                    connectionManager.disconnect()
                }
            }
            
            // Adjust behavior based on network quality
            adjustForNetworkQuality(networkState.quality)
        }
    }
    
    private fun adjustForNetworkQuality(quality: NetworkQuality) {
        config = when (quality) {
            NetworkQuality.EXCELLENT -> config.copy(
                heartbeatInterval = 30.seconds,
                messageTimeout = 10.seconds
            )
            NetworkQuality.GOOD -> config.copy(
                heartbeatInterval = 45.seconds,
                messageTimeout = 15.seconds
            )
            NetworkQuality.FAIR -> config.copy(
                heartbeatInterval = 60.seconds,
                messageTimeout = 20.seconds
            )
            NetworkQuality.POOR -> config.copy(
                heartbeatInterval = 90.seconds,
                messageTimeout = 30.seconds
            )
            NetworkQuality.UNUSABLE -> config // Don't bother adjusting
        }
    }
    
    private fun adjustConnectionBehavior(recommendation: DeviceStateRecommendation) {
        config = when (recommendation) {
            DeviceStateRecommendation.Normal -> config.copy(
                enableAutoReconnect = true,
                maxReconnectAttempts = 10
            )
            DeviceStateRecommendation.ReduceActivity -> config.copy(
                enableAutoReconnect = true,
                maxReconnectAttempts = 5,
                heartbeatInterval = config.heartbeatInterval * 1.5
            )
            DeviceStateRecommendation.MinimalActivity -> config.copy(
                enableAutoReconnect = true,
                maxReconnectAttempts = 3,
                heartbeatInterval = config.heartbeatInterval * 2
            )
            DeviceStateRecommendation.SuspendActivity -> config.copy(
                enableAutoReconnect = false,
                maxReconnectAttempts = 0
            )
        }
    }
    
    private suspend fun makeConnectionDecision(
        appState: AppState,
        networkState: com.mangala.wallet.websocket.chat.network.NetworkState,
        batteryLevel: Int
    ) {
        val currentConnectionState = connectionManager.connectionState.value
        
        // Decision matrix for connection management
        val shouldBeConnected = when {
            // Never connect conditions
            !networkState.isConnected -> false
            networkState.quality == NetworkQuality.UNUSABLE -> false
            appState == AppState.TERMINATED -> false
            batteryLevel <= 5 && appState == AppState.BACKGROUND -> false
            
            // Always connect conditions
            appState == AppState.FOREGROUND && networkState.quality >= NetworkQuality.FAIR -> true
            
            // Conditional connect
            appState == AppState.BACKGROUND -> {
                batteryLevel > 20 && networkState.quality >= NetworkQuality.GOOD
            }
            
            else -> false
        }
        
        // Take action based on decision
        when {
            shouldBeConnected && currentConnectionState == ConnectionState.DISCONNECTED -> {
                if (!isUserInitiatedDisconnect) {
                    connectionManager.connect()
                }
            }
            !shouldBeConnected && currentConnectionState == ConnectionState.CONNECTED -> {
                connectionManager.disconnect()
            }
        }
    }
    
    private fun scheduleBackgroundDisconnect() {
        backgroundTaskJob?.cancel()
        backgroundTaskJob = coroutineScope.launch {
            val disconnectDelay = calculateBackgroundDisconnectDelay()
            Napier.d("Scheduling background disconnect in ${disconnectDelay.inWholeSeconds}s", tag = tag)
            
            delay(disconnectDelay)
            
            if (appLifecycleManager.getCurrentAppState() == AppState.BACKGROUND) {
                connectionManager.disconnect()
            }
        }
    }
    
    private suspend fun calculateBackgroundDisconnectDelay(): Duration {
        val deviceState = deviceStateMonitor.getCurrentDeviceState()
        
        return when {
            deviceState.batteryLevel <= 20 -> 10.seconds
            deviceState.isPowerSaveMode -> 15.seconds
            deviceState.isDozeMode -> 5.seconds
            else -> 30.seconds // Default grace period
        }
    }
    
    private fun shouldReconnectOnForeground(): Boolean {
        if (isUserInitiatedDisconnect) return false
        
        val timeSinceDisconnect = Clock.System.now().toEpochMilliseconds() - lastDisconnectTime
        val recentDisconnect = timeSinceDisconnect < 5.minutes.inWholeMilliseconds
        
        return recentDisconnect && networkMonitor.isNetworkSuitable()
    }
    
    private suspend fun shouldMaintainConnectionInBackground(deviceState: com.mangala.wallet.websocket.chat.device.DeviceState): Boolean {
        // Decision factors for maintaining background connection
        return when {
            // Never maintain in background
            deviceState.batteryLevel <= 10 -> false
            deviceState.isPowerSaveMode -> false
            deviceState.isDozeMode -> false
            
            // Check other conditions
            !networkMonitor.isNetworkSuitable() -> false
            consecutiveFailures > 3 -> false
            
            // Default: maintain for a short period
            else -> true
        }
    }
    
    private fun shouldAutoReconnect(): Boolean {
        return config.enableAutoReconnect && 
               !isUserInitiatedDisconnect && 
               appLifecycleManager.getCurrentAppState() != AppState.TERMINATED
    }
    
    private fun handleMemoryPressure(level: Int) {
        Napier.w("Memory pressure level: $level", tag = tag)
        // Could implement message queue pruning or connection throttling
    }
    
    private fun handleLowMemory() {
        Napier.w("Low memory warning", tag = tag)
        // Could clear caches or reduce queue size
    }
    
    private fun handleBackgroundTimeExpiring(timeRemaining: Double) {
        Napier.d("Background time expiring: ${timeRemaining}s remaining", tag = tag)
        
        if (timeRemaining < 5.0) {
            // Disconnect gracefully before iOS kills us
            coroutineScope.launch {
                connectionManager.disconnect()
            }
        }
    }
    
    /**
     * Configuration for adaptive connection behavior
     */
    private data class ConnectionConfig(
        val enableAutoReconnect: Boolean = true,
        val heartbeatInterval: Duration = 30.seconds,
        val messageTimeout: Duration = 10.seconds,
        val maxReconnectDelay: Duration = 60.seconds,
        val maxReconnectAttempts: Int = 10
    )
}