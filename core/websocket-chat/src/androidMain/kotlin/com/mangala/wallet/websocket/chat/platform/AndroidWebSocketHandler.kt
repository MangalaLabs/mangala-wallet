package com.mangala.wallet.websocket.chat.platform

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.SystemClock
import androidx.work.*
import com.mangala.wallet.websocket.chat.websocket.ConnectionManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Android-specific handler for WebSocket connections that respects Doze mode
 * and other Android power management features
 */
class AndroidWebSocketHandler(
    private val context: Context,
    private val connectionManager: ConnectionManager,
    private val powerManager: AndroidPowerManager,
    private val coroutineScope: CoroutineScope
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var isInDozeMode = false
    
    init {
        observePowerStates()
        schedulePeriodicWork()
    }
    
    private fun observePowerStates() {
        // Observe Doze mode
        coroutineScope.launch {
            powerManager.observeDozeMode().collect { inDozeMode ->
                isInDozeMode = inDozeMode
                Napier.d("Doze mode changed: $inDozeMode", tag = "AndroidWebSocketHandler")
                
                if (inDozeMode) {
                    // In Doze mode, schedule alarm for maintenance windows
                    scheduleDozeAlarm()
                } else {
                    // Out of Doze mode, ensure connection is active
                    ensureConnection()
                }
            }
        }
        
        // Observe power save mode
        coroutineScope.launch {
            powerManager.observePowerSaveMode().collect { inPowerSaveMode ->
                Napier.d("Power save mode changed: $inPowerSaveMode", tag = "AndroidWebSocketHandler")
                
                if (inPowerSaveMode) {
                    // Reduce connection frequency in power save mode
                    adjustConnectionStrategy(conservative = true)
                } else {
                    adjustConnectionStrategy(conservative = false)
                }
            }
        }
    }
    
    /**
     * Schedule periodic work using WorkManager for background execution
     */
    private fun schedulePeriodicWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val periodicWorkRequest = PeriodicWorkRequestBuilder<WebSocketWorker>(
            15, TimeUnit.MINUTES // Minimum interval for periodic work
        )
            .setConstraints(constraints)
            .addTag("websocket_sync")
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "websocket_periodic_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
    
    /**
     * Schedule an alarm for Doze maintenance windows
     */
    private fun scheduleDozeAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(context, DozeAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                DOZE_ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Schedule alarm that can wake device from Doze
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + DOZE_MAINTENANCE_INTERVAL,
                    pendingIntent
                )
            }
        }
    }
    
    private fun ensureConnection() {
        coroutineScope.launch {
            try {
                connectionManager.connect()
            } catch (e: Exception) {
                Napier.e("Failed to ensure connection", e, tag = "AndroidWebSocketHandler")
            }
        }
    }
    
    private fun adjustConnectionStrategy(conservative: Boolean) {
        // This would communicate with ConnectionManager to adjust reconnection strategy
        // For now, we'll just log the intention
        Napier.d("Adjusting connection strategy: conservative=$conservative", tag = "AndroidWebSocketHandler")
    }
    
    companion object {
        private const val DOZE_ALARM_REQUEST_CODE = 12345
        private const val DOZE_MAINTENANCE_INTERVAL = 15 * 60 * 1000L // 15 minutes
    }
}

/**
 * WorkManager Worker for periodic WebSocket operations
 */
class WebSocketWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Get ConnectionManager from dependency injection
            // For now, we'll just log
            Napier.d("WebSocketWorker executing periodic sync", tag = "WebSocketWorker")
            
            // Perform sync operations
            // - Check for pending messages
            // - Attempt to send queued messages
            // - Clean up expired messages
            
            Result.success()
        } catch (e: Exception) {
            Napier.e("WebSocketWorker failed", e, tag = "WebSocketWorker")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

/**
 * BroadcastReceiver for handling Doze maintenance window alarms
 */
class DozeAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Napier.d("Doze maintenance window alarm received", tag = "DozeAlarmReceiver")
        
        // Launch a coroutine to handle WebSocket operations
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get ConnectionManager and perform maintenance
                // For now, we'll just log
                Napier.d("Performing WebSocket maintenance in Doze window", tag = "DozeAlarmReceiver")
            } catch (e: Exception) {
                Napier.e("Failed to perform Doze maintenance", e, tag = "DozeAlarmReceiver")
            }
        }
    }
}