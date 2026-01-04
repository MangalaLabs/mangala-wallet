package com.mangala.wallet.features.addressbook.util

import com.mangala.wallet.features.addressbook.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * Helper class to emit change events with debouncing support
 * This helps prevent too many rapid updates when multiple operations happen in quick succession
 */
class DebouncedChangeEventEmitter<T>(
    private val flow: MutableSharedFlow<T>,
    private val scope: CoroutineScope,
    private val logTag: String,
    private val debounceDelayMs: Long = Constants.Search.DEBOUNCE_DELAY_MS
) {
    private val baseEmitter = ChangeEventEmitter(flow, logTag)
    private var debounceJob: Job? = null
    
    /**
     * Emits a change event immediately without debouncing
     * Use this for critical updates that must be sent immediately
     */
    suspend fun emitImmediate(event: T, logMessage: String) {
        // Cancel any pending debounced event
        debounceJob?.cancel()
        baseEmitter.emit(event, logMessage)
    }
    
    /**
     * Emits a change event with debouncing
     * If called multiple times within the debounce delay, only the last event will be emitted
     */
    fun emitDebounced(event: T, logMessage: String) {
        // Cancel previous debounce job if exists
        debounceJob?.cancel()
        
        // Create new debounce job
        debounceJob = scope.launch {
            delay(debounceDelayMs)
            baseEmitter.emit(event, "$logMessage (debounced)")
        }
    }
    
    /**
     * Tries to emit a change event immediately without suspending
     * @return true if the event was emitted successfully
     */
    fun tryEmitImmediate(event: T, logMessage: String): Boolean {
        // Cancel any pending debounced event
        debounceJob?.cancel()
        return baseEmitter.tryEmit(event, logMessage)
    }
    
    /**
     * Cancels any pending debounced events
     */
    fun cancelPending() {
        debounceJob?.cancel()
        debounceJob = null
    }
}