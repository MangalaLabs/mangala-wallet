package com.mangala.wallet.websocket.chat.device

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine

class AndroidDeviceStateMonitor(
    private val context: Context
) : DeviceStateMonitor {
    
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    private val _batteryLevel = MutableStateFlow(100)
    private val _batteryState = MutableStateFlow(BatteryState.UNKNOWN)
    private val _powerSaveMode = MutableStateFlow(false)
    private val _thermalState = MutableStateFlow(ThermalState.NOMINAL)
    private val _memoryPressure = MutableStateFlow(MemoryPressure.NORMAL)
    
    private var batteryReceiver: BroadcastReceiver? = null
    private var powerSaveReceiver: BroadcastReceiver? = null
    
    override fun getBatteryLevel(): Flow<Int> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = if (level >= 0 && scale > 0) {
                    (level * 100) / scale
                } else {
                    100
                }
                trySend(batteryPct)
                _batteryLevel.value = batteryPct
            }
        }
        
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(receiver, filter)
        
        // Emit current battery level
        batteryStatus?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = if (level >= 0 && scale > 0) {
                (level * 100) / scale
            } else {
                100
            }
            trySend(batteryPct)
            _batteryLevel.value = batteryPct
        }
        
        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
    
    override fun getBatteryState(): Flow<BatteryState> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val state = when (status) {
                    BatteryManager.BATTERY_STATUS_CHARGING,
                    BatteryManager.BATTERY_STATUS_FULL -> BatteryState.CHARGING
                    BatteryManager.BATTERY_STATUS_DISCHARGING,
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryState.DISCHARGING
                    else -> BatteryState.UNKNOWN
                }
                trySend(state)
                _batteryState.value = state
            }
        }
        
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(receiver, filter)
        
        // Emit current state
        batteryStatus?.let {
            val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val state = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> BatteryState.CHARGING
                BatteryManager.BATTERY_STATUS_FULL -> BatteryState.FULL
                BatteryManager.BATTERY_STATUS_DISCHARGING,
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryState.DISCHARGING
                else -> BatteryState.UNKNOWN
            }
            trySend(state)
            _batteryState.value = state
        }
        
        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
    
    override fun isPowerSaveMode(): Flow<Boolean> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val isEnabled = powerManager.isPowerSaveMode
                trySend(isEnabled)
                _powerSaveMode.value = isEnabled
            }
        }
        
        val filter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        context.registerReceiver(receiver, filter)
        
        // Emit current state
        val isEnabled = powerManager.isPowerSaveMode
        trySend(isEnabled)
        _powerSaveMode.value = isEnabled
        
        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
    
    override fun getThermalState(): Flow<ThermalState> = callbackFlow {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val listener = PowerManager.OnThermalStatusChangedListener { status ->
                val state = when (status) {
                    PowerManager.THERMAL_STATUS_NONE,
                    PowerManager.THERMAL_STATUS_LIGHT -> ThermalState.NOMINAL
                    PowerManager.THERMAL_STATUS_MODERATE -> ThermalState.FAIR
                    PowerManager.THERMAL_STATUS_SEVERE -> ThermalState.SERIOUS
                    PowerManager.THERMAL_STATUS_CRITICAL,
                    PowerManager.THERMAL_STATUS_EMERGENCY,
                    PowerManager.THERMAL_STATUS_SHUTDOWN -> ThermalState.CRITICAL
                    else -> ThermalState.NOMINAL
                }
                trySend(state)
                _thermalState.value = state
            }
            
            powerManager.addThermalStatusListener(context.mainExecutor, listener)
            
            // Emit current state
            val currentStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                powerManager.currentThermalStatus
            } else {
                PowerManager.THERMAL_STATUS_NONE
            }
            
            val state = when (currentStatus) {
                PowerManager.THERMAL_STATUS_NONE,
                PowerManager.THERMAL_STATUS_LIGHT -> ThermalState.NOMINAL
                PowerManager.THERMAL_STATUS_MODERATE -> ThermalState.FAIR
                PowerManager.THERMAL_STATUS_SEVERE -> ThermalState.SERIOUS
                PowerManager.THERMAL_STATUS_CRITICAL,
                PowerManager.THERMAL_STATUS_EMERGENCY,
                PowerManager.THERMAL_STATUS_SHUTDOWN -> ThermalState.CRITICAL
                else -> ThermalState.NOMINAL
            }
            trySend(state)
            _thermalState.value = state
            
            awaitClose {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    powerManager.removeThermalStatusListener(listener)
                }
            }
        } else {
            // For older Android versions, assume nominal
            trySend(ThermalState.NOMINAL)
            awaitClose {}
        }
    }
    
    override fun getMemoryPressure(): Flow<MemoryPressure> = callbackFlow {
        // Monitor memory using ComponentCallbacks2
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val pressure = when {
            memoryInfo.lowMemory -> MemoryPressure.CRITICAL
            memoryInfo.availMem < memoryInfo.threshold -> MemoryPressure.WARNING
            else -> MemoryPressure.NORMAL
        }
        
        trySend(pressure)
        _memoryPressure.value = pressure
        
        // Note: For real-time updates, implement ComponentCallbacks2 in Application class
        awaitClose {}
    }
    
    override suspend fun getCurrentDeviceState(): DeviceState {
        return DeviceState(
            batteryLevel = _batteryLevel.value,
            batteryState = _batteryState.value,
            isPowerSaveMode = _powerSaveMode.value,
            thermalState = _thermalState.value,
            memoryPressure = _memoryPressure.value,
            isLowDataMode = isDataSaverEnabled(),
            isDozeMode = isInDozeMode(),
            isAppStandby = isInAppStandby()
        )
    }
    
    override fun startMonitoring() {
        // Battery monitoring
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateBatteryInfo(intent)
            }
        }
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        
        // Power save mode monitoring
        powerSaveReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                _powerSaveMode.value = powerManager.isPowerSaveMode
            }
        }
        context.registerReceiver(powerSaveReceiver, IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED))
        
        Napier.d("Device state monitoring started", tag = "AndroidDeviceStateMonitor")
    }
    
    override fun stopMonitoring() {
        batteryReceiver?.let {
            context.unregisterReceiver(it)
            batteryReceiver = null
        }
        
        powerSaveReceiver?.let {
            context.unregisterReceiver(it)
            powerSaveReceiver = null
        }
        
        Napier.d("Device state monitoring stopped", tag = "AndroidDeviceStateMonitor")
    }
    
    private fun updateBatteryInfo(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        
        if (level >= 0 && scale > 0) {
            _batteryLevel.value = (level * 100) / scale
        }
        
        _batteryState.value = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> BatteryState.CHARGING
            BatteryManager.BATTERY_STATUS_FULL -> BatteryState.FULL
            BatteryManager.BATTERY_STATUS_DISCHARGING,
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryState.DISCHARGING
            else -> BatteryState.UNKNOWN
        }
    }
    
    private fun isDataSaverEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED
        } else {
            false
        }
    }
    
    private fun isInDozeMode(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isDeviceIdleMode
        } else {
            false
        }
    }
    
    private fun isInAppStandby(): Boolean {
        // This would require UsageStatsManager permission
        // For now, return false
        return false
    }
}