package com.mangala.wallet.websocket.chat.platform

import com.mangala.wallet.websocket.chat.websocket.ConnectionManager
import kotlinx.cinterop.ExperimentalForeignApi
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import platform.BackgroundTasks.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * iOS-specific handler for WebSocket connections that uses BackgroundTasks framework
 * to maintain connections when app is in background
 */
@OptIn(ExperimentalForeignApi::class)
class IosBackgroundTaskHandler(
    private val connectionManager: ConnectionManager,
    private val coroutineScope: CoroutineScope
) {
    private var backgroundTaskIdentifier: UIBackgroundTaskIdentifier = UIBackgroundTaskInvalid
    private var bgAppRefreshTaskRequest: BGAppRefreshTaskRequest? = null
    private var bgProcessingTaskRequest: BGProcessingTaskRequest? = null
    
    companion object {
        const val BG_TASK_IDENTIFIER_REFRESH = "com.mangala.wallet.websocket.refresh"
        const val BG_TASK_IDENTIFIER_PROCESSING = "com.mangala.wallet.websocket.processing"
    }
    
    init {
        registerBackgroundTasks()
        setupNotificationObservers()
    }
    
    /**
     * Register background tasks with the system
     * Note: These identifiers must be added to Info.plist under BGTaskSchedulerPermittedIdentifiers
     */
    private fun registerBackgroundTasks() {
        // Register app refresh task
        BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
            identifier = BG_TASK_IDENTIFIER_REFRESH,
            usingQueue = dispatch_get_main_queue()
        ) { task ->
            handleAppRefreshTask(task as BGAppRefreshTask)
        }
        
        // Register processing task for longer operations
        BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
            identifier = BG_TASK_IDENTIFIER_PROCESSING,
            usingQueue = dispatch_get_main_queue()
        ) { task ->
            handleProcessingTask(task as BGProcessingTask)
        }
    }
    
    private fun setupNotificationObservers() {
        val notificationCenter = NSNotificationCenter.defaultCenter
        
        // App entering background
        notificationCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                handleAppEnteredBackground()
            }
        )
        
        // App entering foreground
        notificationCenter.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                handleAppWillEnterForeground()
            }
        )
    }
    
    private fun handleAppEnteredBackground() {
        Napier.d("App entered background, starting background task", tag = "IosBackgroundTaskHandler")
        
        // Begin background task to get extra time
        backgroundTaskIdentifier = UIApplication.sharedApplication.beginBackgroundTaskWithExpirationHandler {
            // Clean up when background time expires
            handleBackgroundTaskExpiration()
        }
        
        // Schedule background refresh
        scheduleAppRefresh()
        
        // Schedule processing task if needed
        if (hasUnsentMessages()) {
            scheduleProcessingTask()
        }
    }
    
    private fun handleAppWillEnterForeground() {
        Napier.d("App will enter foreground, ending background task", tag = "IosBackgroundTaskHandler")
        
        // End background task
        if (backgroundTaskIdentifier != UIBackgroundTaskInvalid) {
            UIApplication.sharedApplication.endBackgroundTask(backgroundTaskIdentifier)
            backgroundTaskIdentifier = UIBackgroundTaskInvalid
        }
        
        // Ensure connection is active
        coroutineScope.launch {
            try {
                connectionManager.connect()
            } catch (e: Exception) {
                Napier.e("Failed to reconnect on foreground", e, tag = "IosBackgroundTaskHandler")
            }
        }
    }
    
    private fun handleBackgroundTaskExpiration() {
        Napier.d("Background task expiring", tag = "IosBackgroundTaskHandler")
        
        // Gracefully disconnect
        coroutineScope.launch {
            try {
                connectionManager.disconnect()
            } catch (e: Exception) {
                Napier.e("Failed to disconnect on background expiration", e, tag = "IosBackgroundTaskHandler")
            }
        }
        
        // End the background task
        if (backgroundTaskIdentifier != UIBackgroundTaskInvalid) {
            UIApplication.sharedApplication.endBackgroundTask(backgroundTaskIdentifier)
            backgroundTaskIdentifier = UIBackgroundTaskInvalid
        }
    }
    
    private fun scheduleAppRefresh() {
        val request = BGAppRefreshTaskRequest(BG_TASK_IDENTIFIER_REFRESH).apply {
            earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(15.0 * 60.0) // 15 minutes
        }
        
        do {
            val scheduled = try {
                BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
                true
            } catch (e: Exception) {
                Napier.e("Failed to schedule app refresh", e, tag = "IosBackgroundTaskHandler")
                false
            }
        } while (false)
        
        bgAppRefreshTaskRequest = request
    }
    
    private fun scheduleProcessingTask() {
        val request = BGProcessingTaskRequest(BG_TASK_IDENTIFIER_PROCESSING).apply {
            requiresNetworkConnectivity = true
            requiresExternalPower = false
            earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(5.0 * 60.0) // 5 minutes
        }
        
        do {
            val scheduled = try {
                BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
                true
            } catch (e: Exception) {
                Napier.e("Failed to schedule processing task", e, tag = "IosBackgroundTaskHandler")
                false
            }
        } while (false)
        
        bgProcessingTaskRequest = request
    }
    
    private fun handleAppRefreshTask(task: BGAppRefreshTask) {
        Napier.d("Handling app refresh background task", tag = "IosBackgroundTaskHandler")
        
        // Set expiration handler
        task.expirationHandler = {
            Napier.d("App refresh task expired", tag = "IosBackgroundTaskHandler")
            task.setTaskCompletedWithSuccess(false)
        }
        
        // Perform quick WebSocket operations
        coroutineScope.launch {
            try {
                // Quick connect and sync
                connectionManager.connect()
                delay(5000) // Give some time for sync
                
                // Schedule next refresh
                scheduleAppRefresh()
                
                task.setTaskCompletedWithSuccess(true)
            } catch (e: Exception) {
                Napier.e("App refresh task failed", e, tag = "IosBackgroundTaskHandler")
                task.setTaskCompletedWithSuccess(false)
            }
        }
    }
    
    private fun handleProcessingTask(task: BGProcessingTask) {
        Napier.d("Handling processing background task", tag = "IosBackgroundTaskHandler")
        
        // Set expiration handler
        task.expirationHandler = {
            Napier.d("Processing task expired", tag = "IosBackgroundTaskHandler")
            task.setTaskCompletedWithSuccess(false)
        }
        
        // Perform longer WebSocket operations
        coroutineScope.launch {
            try {
                // Connect and process pending messages
                connectionManager.connect()
                
                // Process pending messages (would integrate with message queue)
                processPendingMessages()
                
                task.setTaskCompletedWithSuccess(true)
            } catch (e: Exception) {
                Napier.e("Processing task failed", e, tag = "IosBackgroundTaskHandler")
                task.setTaskCompletedWithSuccess(false)
            }
        }
    }
    
    private fun hasUnsentMessages(): Boolean {
        // This would check with the message repository
        // For now, return false
        return false
    }
    
    private suspend fun processPendingMessages() {
        // This would process messages from the queue
        // For now, just log
        Napier.d("Processing pending messages in background", tag = "IosBackgroundTaskHandler")
        delay(2000) // Simulate work
    }
}