package com.mangala.wallet.websocket.chat.device

import kotlinx.coroutines.flow.Flow

/**
 * Monitors device state including battery, thermal state, and power modes
 */
interface DeviceStateMonitor {
    /**
     * Battery level from 0 to 100
     */
    fun getBatteryLevel(): Flow<Int>
    
    /**
     * Current battery state
     */
    fun getBatteryState(): Flow<BatteryState>
    
    /**
     * Whether device is in power save mode
     */
    fun isPowerSaveMode(): Flow<Boolean>
    
    /**
     * Device thermal state (iOS) or temperature warnings (Android)
     */
    fun getThermalState(): Flow<ThermalState>
    
    /**
     * Memory pressure warnings
     */
    fun getMemoryPressure(): Flow<MemoryPressure>
    
    /**
     * Current device state snapshot
     */
    suspend fun getCurrentDeviceState(): DeviceState
    
    /**
     * Start monitoring device state
     */
    fun startMonitoring()
    
    /**
     * Stop monitoring device state
     */
    fun stopMonitoring()
}

/**
 * Battery charging state
 */
enum class BatteryState {
    CHARGING,       // Connected to power
    DISCHARGING,    // Running on battery
    FULL,           // Battery full
    UNKNOWN
}

/**
 * Device thermal state
 */
enum class ThermalState {
    NOMINAL,        // Normal temperature
    FAIR,           // Slightly elevated
    SERIOUS,        // Getting hot, throttle operations
    CRITICAL        // Very hot, minimize activity
}

/**
 * Memory pressure levels
 */
enum class MemoryPressure {
    NORMAL,         // Plenty of memory
    WARNING,        // Memory getting low
    CRITICAL,       // Extreme memory pressure
    TERMINATED      // App about to be killed
}

/**
 * Complete device state snapshot
 */
data class DeviceState(
    val batteryLevel: Int,
    val batteryState: BatteryState,
    val isPowerSaveMode: Boolean,
    val thermalState: ThermalState,
    val memoryPressure: MemoryPressure,
    val isLowDataMode: Boolean = false, // iOS specific
    val isDozeMode: Boolean = false,    // Android specific
    val isAppStandby: Boolean = false,  // Android specific
    val backgroundTimeRemaining: Double = -1.0 // iOS specific, in seconds
)

/**
 * Device state thresholds for adaptive behavior
 */
object DeviceStateThresholds {
    const val BATTERY_CRITICAL = 5
    const val BATTERY_LOW = 20
    const val BATTERY_MEDIUM = 50
    
    const val MEMORY_WARNING_THRESHOLD = 80 // % of memory used
    const val MEMORY_CRITICAL_THRESHOLD = 95
}

/**
 * Recommended actions based on device state
 */
sealed class DeviceStateRecommendation {
    object Normal : DeviceStateRecommendation()
    object ReduceActivity : DeviceStateRecommendation()
    object MinimalActivity : DeviceStateRecommendation()
    object SuspendActivity : DeviceStateRecommendation()
    
    companion object {
        fun fromDeviceState(state: DeviceState): DeviceStateRecommendation {
            return when {
                state.batteryLevel <= DeviceStateThresholds.BATTERY_CRITICAL -> SuspendActivity
                state.thermalState == ThermalState.CRITICAL -> SuspendActivity
                state.memoryPressure == MemoryPressure.CRITICAL -> SuspendActivity
                
                state.batteryLevel <= DeviceStateThresholds.BATTERY_LOW -> MinimalActivity
                state.isPowerSaveMode -> MinimalActivity
                state.thermalState == ThermalState.SERIOUS -> MinimalActivity
                state.memoryPressure == MemoryPressure.WARNING -> MinimalActivity
                state.isDozeMode -> MinimalActivity
                
                state.batteryLevel <= DeviceStateThresholds.BATTERY_MEDIUM -> ReduceActivity
                state.thermalState == ThermalState.FAIR -> ReduceActivity
                
                else -> Normal
            }
        }
    }
}