package com.mangala.wallet.websocket.chat.device

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

class DesktopDeviceStateMonitor : DeviceStateMonitor {
    
    private val _batteryLevel = MutableStateFlow(100) // Desktop assumed to be plugged in
    private val _batteryState = MutableStateFlow(BatteryState.CHARGING)
    private val _powerSaveMode = MutableStateFlow(false)
    private val _thermalState = MutableStateFlow(ThermalState.NOMINAL)
    private val _memoryPressure = MutableStateFlow(calculateMemoryPressure())
    
    override fun getBatteryLevel(): Flow<Int> = _batteryLevel.asStateFlow()
    
    override fun getBatteryState(): Flow<BatteryState> = _batteryState.asStateFlow()
    
    override fun isPowerSaveMode(): Flow<Boolean> = _powerSaveMode.asStateFlow()
    
    override fun getThermalState(): Flow<ThermalState> = _thermalState.asStateFlow()
    
    override fun getMemoryPressure(): Flow<MemoryPressure> = _memoryPressure.asStateFlow()
    
    override suspend fun getCurrentDeviceState(): DeviceState {
        return DeviceState(
            batteryLevel = _batteryLevel.value,
            batteryState = _batteryState.value,
            isPowerSaveMode = _powerSaveMode.value,
            thermalState = _thermalState.value,
            memoryPressure = _memoryPressure.value,
            isLowDataMode = false,
            isDozeMode = false,
            isAppStandby = false,
            backgroundTimeRemaining = -1.0
        )
    }
    
    override fun startMonitoring() {
        // Desktop doesn't need active monitoring for most metrics
        // Could periodically update memory pressure if needed
    }
    
    override fun stopMonitoring() {
        // No active monitoring to stop on desktop
    }
    
    private fun calculateMemoryPressure(): MemoryPressure {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val usagePercent = (usedMemory.toDouble() / maxMemory.toDouble()) * 100
        
        return when {
            usagePercent < DeviceStateThresholds.MEMORY_WARNING_THRESHOLD -> MemoryPressure.NORMAL
            usagePercent < DeviceStateThresholds.MEMORY_CRITICAL_THRESHOLD -> MemoryPressure.WARNING
            else -> MemoryPressure.CRITICAL
        }
    }
}