package com.mangala.wallet.websocket.chat.websocket

import io.github.aakira.napier.Napier
import com.mangala.wallet.websocket.chat.websocket.models.ConnectionState
import com.mangala.wallet.websocket.chat.lifecycle.AppLifecycleManager
import com.mangala.wallet.websocket.chat.lifecycle.AppState
import com.mangala.wallet.websocket.chat.device.DeviceStateMonitor
import com.mangala.wallet.websocket.chat.device.ThermalState
import com.mangala.wallet.websocket.chat.network.NetworkMonitor
import com.mangala.wallet.websocket.chat.network.NetworkState
import com.mangala.wallet.websocket.chat.network.NetworkType
import com.mangala.wallet.websocket.chat.network.NetworkQuality
import com.mangala.wallet.websocket.chat.reconnection.AdaptiveReconnectionStrategy
import com.mangala.wallet.websocket.chat.reconnection.ReconnectionStrategy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ConnectionManager(
    private val webSocketEngine: WebSocketEngine,
    private val coroutineScope: CoroutineScope,
    private val reconnectionStrategy: ReconnectionStrategy = AdaptiveReconnectionStrategy(),
    private val networkMonitor: NetworkMonitor? = null,
    private val deviceStateMonitor: DeviceStateMonitor? = null,
    private val appLifecycleManager: AppLifecycleManager? = null
) {
    
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private var reconnectJob: Job? = null
    private var heartbeatJob: Job? = null
    private var currentRetryCount = 0
    
    // Current environment state
    private var currentNetworkState: NetworkState = NetworkState(
        isConnected = false,
        type = NetworkType.NONE,
        quality = NetworkQuality.UNUSABLE
    )
    private var currentBatteryLevel: Int = 100
    private var currentThermalState: ThermalState = ThermalState.NOMINAL
    private var currentAppState: AppState = AppState.FOREGROUND
    
    init {
        observeEnvironmentChanges()
    }
    
    suspend fun connect() {
        if (_connectionState.value == ConnectionState.CONNECTED || 
            _connectionState.value == ConnectionState.CONNECTING) {
            Napier.d("Already connected or connecting, skipping connect request", tag = "ConnectionManager")
            return
        }
        
        _connectionState.value = ConnectionState.CONNECTING
        
        try {
            webSocketEngine.connect()
            _connectionState.value = ConnectionState.CONNECTED
            currentRetryCount = 0
            startHeartbeat()
            Napier.i("WebSocket connected successfully", tag = "ConnectionManager")
        } catch (e: Exception) {
            Napier.e("Failed to connect WebSocket", e, tag = "ConnectionManager")
            _connectionState.value = ConnectionState.FAILED
            scheduleReconnect()
        }
    }
    
    suspend fun disconnect() {
        Napier.d("Disconnecting WebSocket", tag = "ConnectionManager")
        cancelReconnect()
        stopHeartbeat()
        
        try {
            webSocketEngine.disconnect()
        } catch (e: Exception) {
            Napier.e("Error during disconnect", e, tag = "ConnectionManager")
        } finally {
            _connectionState.value = ConnectionState.DISCONNECTED
            currentRetryCount = 0
        }
    }
    
    fun handleConnectionLost() {
        Napier.w("Connection lost, scheduling reconnect", tag = "ConnectionManager")
        _connectionState.value = ConnectionState.RECONNECTING
        stopHeartbeat()
        scheduleReconnect()
    }
    
    private fun scheduleReconnect() {
        cancelReconnect()
        
        if (_connectionState.value == ConnectionState.DISCONNECTED) {
            Napier.d("Not scheduling reconnect, connection is intentionally disconnected", tag = "ConnectionManager")
            return
        }
        
        reconnectJob = coroutineScope.launch {
            currentRetryCount++
            
            val shouldReconnect = reconnectionStrategy.shouldReconnect(
                attempt = currentRetryCount,
                networkState = currentNetworkState,
                batteryLevel = currentBatteryLevel,
                thermalState = currentThermalState,
                appState = currentAppState
            )
            
            if (!shouldReconnect) {
                Napier.d("Reconnection strategy decided not to reconnect", tag = "ConnectionManager")
                _connectionState.value = ConnectionState.FAILED
                return@launch
            }
            
            val delay = reconnectionStrategy.calculateDelay(
                attempt = currentRetryCount,
                networkState = currentNetworkState,
                batteryLevel = currentBatteryLevel,
                thermalState = currentThermalState,
                appState = currentAppState
            )
            
            if (delay == null) {
                Napier.d("Reconnection strategy returned null delay, stopping reconnection", tag = "ConnectionManager")
                _connectionState.value = ConnectionState.FAILED
                return@launch
            }
            
            Napier.d("Scheduling reconnect in ${delay.inWholeSeconds} seconds (attempt $currentRetryCount)", tag = "ConnectionManager")
            delay(delay)
            
            try {
                connect()
            } catch (e: Exception) {
                Napier.e("Reconnect attempt failed", e, tag = "ConnectionManager")
            }
        }
    }
    
    private fun cancelReconnect() {
        reconnectJob?.cancel()
        reconnectJob = null
    }
    
    private fun observeEnvironmentChanges() {
        // Observe network changes
        networkMonitor?.observeNetworkState()?.let { flow ->
            coroutineScope.launch {
                flow.collect { networkState ->
                    val previousState = currentNetworkState
                    currentNetworkState = networkState
                    
                    // If network became available and we're in reconnecting state, trigger reconnect
                    if (!previousState.isConnected && networkState.isConnected &&
                        _connectionState.value == ConnectionState.RECONNECTING) {
                        Napier.d("Network became available, triggering reconnect", tag = "ConnectionManager")
                        cancelReconnect()
                        scheduleReconnect()
                    }
                }
            }
        }
        
        // Observe battery level
        deviceStateMonitor?.getBatteryLevel()?.let { flow ->
            coroutineScope.launch {
                flow.collect { level ->
                    currentBatteryLevel = level
                }
            }
        }
        
        // Observe thermal state
        deviceStateMonitor?.getThermalState()?.let { flow ->
            coroutineScope.launch {
                flow.collect { state ->
                    currentThermalState = state
                }
            }
        }
        
        // Observe app state
        appLifecycleManager?.observeAppState()?.let { flow ->
            coroutineScope.launch {
                flow.collect { state ->
                    currentAppState = state
                }
            }
        }
    }
    
    private fun startHeartbeat() {
        stopHeartbeat()
        
        heartbeatJob = coroutineScope.launch {
            while (isActive) {
                delay(HEARTBEAT_INTERVAL)
                
                try {
                    webSocketEngine.sendHeartbeat()
                } catch (e: Exception) {
                    Napier.e("Failed to send heartbeat", e, tag = "ConnectionManager")
                    handleConnectionLost()
                    break
                }
            }
        }
    }
    
    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }
    
    fun updateState(state: ConnectionState) {
        _connectionState.value = state
    }
    
    companion object {
        private val HEARTBEAT_INTERVAL = 30.seconds
    }
}

