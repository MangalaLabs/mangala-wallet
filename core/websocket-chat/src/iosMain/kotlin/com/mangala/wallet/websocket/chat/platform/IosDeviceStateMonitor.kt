package com.mangala.wallet.websocket.chat.platform

import com.mangala.wallet.websocket.chat.device.BatteryState
import com.mangala.wallet.websocket.chat.device.DeviceState
import com.mangala.wallet.websocket.chat.device.DeviceStateMonitor
import com.mangala.wallet.websocket.chat.device.MemoryPressure
import com.mangala.wallet.websocket.chat.device.ThermalState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.Foundation.*
import platform.UIKit.UIDevice
import platform.UIKit.UIApplication
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
class IosDeviceStateMonitor : DeviceStateMonitor {
    private val _batteryLevel = MutableStateFlow(50)
    private val _batteryState = MutableStateFlow(BatteryState.UNKNOWN)
    private val _isPowerSaveMode = MutableStateFlow(false)
    private val _thermalState = MutableStateFlow(ThermalState.NOMINAL)
    private val _memoryPressure = MutableStateFlow(MemoryPressure.NORMAL)
    
    private val scope = MainScope()
    private var isMonitoring = false
    
    init {
        // Enable battery monitoring
        UIDevice.currentDevice.batteryMonitoringEnabled = true
    }
    
    override fun getBatteryLevel(): Flow<Int> = _batteryLevel.asStateFlow()
    
    override fun getBatteryState(): Flow<BatteryState> = _batteryState.asStateFlow()
    
    override fun isPowerSaveMode(): Flow<Boolean> = _isPowerSaveMode.asStateFlow()
    
    override fun getThermalState(): Flow<ThermalState> = _thermalState.asStateFlow()
    
    override fun getMemoryPressure(): Flow<MemoryPressure> = _memoryPressure.asStateFlow()
    
    override suspend fun getCurrentDeviceState(): DeviceState {
        updateBatteryState()
        updatePowerMode()
        updateMemoryPressure()
        
        return DeviceState(
            batteryLevel = _batteryLevel.value,
            batteryState = _batteryState.value,
            isPowerSaveMode = _isPowerSaveMode.value,
            thermalState = _thermalState.value,
            memoryPressure = _memoryPressure.value,
            isLowDataMode = false, // Could be implemented with NWPathMonitor
            isDozeMode = false, // Android specific
            isAppStandby = false, // Android specific
            backgroundTimeRemaining = UIApplication.sharedApplication.backgroundTimeRemaining
        )
    }
    
    override fun startMonitoring() {
        isMonitoring = true
        startBatteryMonitoring()
        startPowerModeMonitoring()
        startMemoryMonitoring()
    }
    
    override fun stopMonitoring() {
        isMonitoring = false
    }
    
    private fun startBatteryMonitoring() {
        UIDevice.currentDevice.batteryMonitoringEnabled = true
        
        scope.launch {
            while (isMonitoring) {
                updateBatteryState()
                kotlinx.coroutines.delay(30000) // Check every 30 seconds
            }
        }
    }
    
    private fun updateBatteryState() {
        val device = UIDevice.currentDevice
        val level = device.batteryLevel
        
        _batteryLevel.value = if (level >= 0) (level * 100).toInt() else 50
        
        // Simplified battery state - iOS doesn't give us direct charging state
        // We'll use battery level for now
        val state = when {
            level >= 1.0f -> BatteryState.FULL
            level < 0.0f -> BatteryState.UNKNOWN
            else -> BatteryState.DISCHARGING
        }
        
        _batteryState.value = state
    }
    
    private fun startPowerModeMonitoring() {
        scope.launch {
            while (isMonitoring) {
                updatePowerMode()
                kotlinx.coroutines.delay(5000) // Check every 5 seconds
            }
        }
    }
    
    private fun updatePowerMode() {
        _isPowerSaveMode.value = NSProcessInfo.processInfo.lowPowerModeEnabled
    }
    
    private fun startMemoryMonitoring() {
        scope.launch {
            while (isMonitoring) {
                updateMemoryPressure()
                kotlinx.coroutines.delay(5000) // Check every 5 seconds
            }
        }
    }
    
    private fun updateMemoryPressure() {
        // Simplified memory pressure detection
        val processInfo = NSProcessInfo.processInfo
        val activeProcessorCount = processInfo.activeProcessorCount.toInt()
        
        // Simple heuristic based on processor count
        val pressure = when {
            activeProcessorCount < 2 -> MemoryPressure.WARNING
            else -> MemoryPressure.NORMAL
        }
        _memoryPressure.value = pressure
    }
}