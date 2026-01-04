package com.mangala.wallet.websocket.chat.platform

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import android.content.BroadcastReceiver

class AndroidPowerManager(private val context: Context) {
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    
    /**
     * Check if the app is whitelisted from battery optimizations
     */
    fun isIgnoringBatteryOptimizations(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true // No battery optimizations before M
        }
    }
    
    /**
     * Request to disable battery optimizations for this app
     */
    fun requestIgnoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isIgnoringBatteryOptimizations()) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        }
    }
    
    /**
     * Check if device is in Doze mode
     */
    fun isDeviceIdleMode(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isDeviceIdleMode
        } else {
            false
        }
    }
    
    /**
     * Check if device is in interactive mode (screen on)
     */
    fun isInteractive(): Boolean {
        return powerManager.isInteractive
    }
    
    /**
     * Observe Doze mode changes
     */
    fun observeDozeMode(): Flow<Boolean> = callbackFlow {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (intent?.action) {
                        PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED -> {
                            trySend(isDeviceIdleMode())
                        }
                    }
                }
            }
            
            val filter = IntentFilter().apply {
                addAction(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED)
            }
            
            context.registerReceiver(receiver, filter)
            
            // Send initial state
            trySend(isDeviceIdleMode())
            
            awaitClose {
                context.unregisterReceiver(receiver)
            }
        } else {
            trySend(false)
            awaitClose {}
        }
    }
    
    /**
     * Observe power save mode changes
     */
    fun observePowerSaveMode(): Flow<Boolean> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    PowerManager.ACTION_POWER_SAVE_MODE_CHANGED -> {
                        trySend(powerManager.isPowerSaveMode)
                    }
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }
        
        context.registerReceiver(receiver, filter)
        
        // Send initial state
        trySend(powerManager.isPowerSaveMode)
        
        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
}