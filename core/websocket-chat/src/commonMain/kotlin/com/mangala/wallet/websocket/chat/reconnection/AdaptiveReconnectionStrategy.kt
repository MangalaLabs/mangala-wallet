package com.mangala.wallet.websocket.chat.reconnection

import com.mangala.wallet.websocket.chat.lifecycle.AppState
import com.mangala.wallet.websocket.chat.network.NetworkState
import com.mangala.wallet.websocket.chat.network.NetworkType
import com.mangala.wallet.websocket.chat.network.NetworkQuality
import com.mangala.wallet.websocket.chat.device.ThermalState
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface ReconnectionStrategy {
    suspend fun calculateDelay(
        attempt: Int,
        networkState: NetworkState,
        batteryLevel: Int,
        thermalState: ThermalState,
        appState: AppState
    ): Duration?
    
    fun shouldReconnect(
        attempt: Int,
        networkState: NetworkState,
        batteryLevel: Int,
        thermalState: ThermalState,
        appState: AppState
    ): Boolean
}

class AdaptiveReconnectionStrategy(
    private val baseDelay: Duration = 1.seconds,
    private val maxDelay: Duration = 5.minutes,
    private val maxAttempts: Int = Int.MAX_VALUE,
    private val criticalBatteryThreshold: Int = 15,
    private val lowBatteryThreshold: Int = 30
) : ReconnectionStrategy {
    
    override suspend fun calculateDelay(
        attempt: Int,
        networkState: NetworkState,
        batteryLevel: Int,
        thermalState: ThermalState,
        appState: AppState
    ): Duration? {
        if (!shouldReconnect(attempt, networkState, batteryLevel, thermalState, appState)) {
            return null
        }
        
        // Base exponential backoff
        val exponentialDelay = baseDelay * (1 shl minOf(attempt - 1, 10))
        
        // Apply multipliers based on conditions
        val networkMultiplier = getNetworkMultiplier(networkState)
        val batteryMultiplier = getBatteryMultiplier(batteryLevel)
        val thermalMultiplier = getThermalMultiplier(thermalState)
        val appStateMultiplier = getAppStateMultiplier(appState)
        
        val totalMultiplier = networkMultiplier * batteryMultiplier * thermalMultiplier * appStateMultiplier
        val adjustedDelay = exponentialDelay * totalMultiplier.toInt()
        
        // Cap at max delay
        return minOf(adjustedDelay, maxDelay)
    }
    
    override fun shouldReconnect(
        attempt: Int,
        networkState: NetworkState,
        batteryLevel: Int,
        thermalState: ThermalState,
        appState: AppState
    ): Boolean {
        // Don't reconnect if we've exceeded max attempts
        if (attempt > maxAttempts) return false
        
        // Don't reconnect if network is not available
        if (!networkState.isConnected) return false
        
        // Don't reconnect in extreme conditions
        if (thermalState == ThermalState.CRITICAL) return false
        
        // Don't reconnect if battery is critically low and on mobile data
        if (batteryLevel < criticalBatteryThreshold && (networkState.type == NetworkType.CELLULAR_5G || 
            networkState.type == NetworkType.CELLULAR_4G || 
            networkState.type == NetworkType.CELLULAR_3G ||
            networkState.type == NetworkType.CELLULAR_2G)) {
            return false
        }
        
        // Don't reconnect if app is in background and battery is low
        if (appState == AppState.BACKGROUND && batteryLevel < lowBatteryThreshold) {
            return false
        }
        
        return true
    }
    
    private fun getNetworkMultiplier(networkState: NetworkState): Double {
        return when {
            !networkState.isConnected -> Double.MAX_VALUE // Won't reconnect anyway
            networkState.isRoaming -> 4.0
            networkState.type == NetworkType.CELLULAR_4G -> when (networkState.quality) {
                NetworkQuality.POOR -> 3.0
                NetworkQuality.FAIR -> 2.0
                NetworkQuality.GOOD -> 1.5
                NetworkQuality.EXCELLENT -> 1.2
                else -> 2.0
            }
            networkState.type == NetworkType.WIFI -> when (networkState.quality) {
                NetworkQuality.POOR -> 2.0
                NetworkQuality.FAIR -> 1.5
                NetworkQuality.GOOD -> 1.0
                NetworkQuality.EXCELLENT -> 0.8
                else -> 1.5
            }
            else -> 2.0
        }
    }
    
    private fun getBatteryMultiplier(batteryLevel: Int): Double {
        return when {
            batteryLevel < criticalBatteryThreshold -> 5.0
            batteryLevel < lowBatteryThreshold -> 3.0
            batteryLevel < 50 -> 1.5
            else -> 1.0
        }
    }
    
    private fun getThermalMultiplier(thermalState: ThermalState): Double {
        return when (thermalState) {
            ThermalState.NOMINAL -> 1.0
            ThermalState.FAIR -> 2.0
            ThermalState.SERIOUS -> 4.0
            ThermalState.CRITICAL -> Double.MAX_VALUE // Won't reconnect anyway
        }
    }
    
    private fun getAppStateMultiplier(appState: AppState): Double {
        return when (appState) {
            AppState.FOREGROUND -> 1.0
            AppState.BACKGROUND -> 3.0
            AppState.INACTIVE -> 2.0
            AppState.TERMINATED -> Double.MAX_VALUE
        }
    }
}

// Specialized strategies for specific scenarios

class AggressiveReconnectionStrategy : ReconnectionStrategy {
    private val baseStrategy = AdaptiveReconnectionStrategy(
        baseDelay = 0.5.seconds,
        maxDelay = 30.seconds,
        maxAttempts = 50
    )
    
    override suspend fun calculateDelay(
        attempt: Int,
        networkState: NetworkState,
        batteryLevel: Int,
        thermalState: ThermalState,
        appState: AppState
    ): Duration? {
        // Always use minimal delays when app is in foreground
        if (appState == AppState.FOREGROUND) {
            return minOf(attempt.seconds, 10.seconds)
        }
        return baseStrategy.calculateDelay(attempt, networkState, batteryLevel, thermalState, appState)
    }
    
    override fun shouldReconnect(
        attempt: Int,
        networkState: NetworkState,
        batteryLevel: Int,
        thermalState: ThermalState,
        appState: AppState
    ): Boolean {
        // Only stop if network is completely unavailable or thermal state is critical
        return networkState.isConnected && thermalState != ThermalState.CRITICAL
    }
}

class ConservativeReconnectionStrategy : ReconnectionStrategy {
    private val baseStrategy = AdaptiveReconnectionStrategy(
        baseDelay = 5.seconds,
        maxDelay = 30.minutes,
        maxAttempts = 10,
        criticalBatteryThreshold = 25,
        lowBatteryThreshold = 50
    )
    
    override suspend fun calculateDelay(
        attempt: Int,
        networkState: NetworkState,
        batteryLevel: Int,
        thermalState: ThermalState,
        appState: AppState
    ): Duration? {
        return baseStrategy.calculateDelay(attempt, networkState, batteryLevel, thermalState, appState)
    }
    
    override fun shouldReconnect(
        attempt: Int,
        networkState: NetworkState,
        batteryLevel: Int,
        thermalState: ThermalState,
        appState: AppState
    ): Boolean {
        // Be very conservative in background
        if (appState == AppState.BACKGROUND) {
            return networkState.type == NetworkType.WIFI && 
                   networkState.quality.ordinal >= NetworkQuality.GOOD.ordinal &&
                   batteryLevel > 50 &&
                   thermalState == ThermalState.NOMINAL
        }
        
        return baseStrategy.shouldReconnect(attempt, networkState, batteryLevel, thermalState, appState)
    }
}