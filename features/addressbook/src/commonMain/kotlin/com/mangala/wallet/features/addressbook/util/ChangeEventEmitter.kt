package com.mangala.wallet.features.addressbook.util

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Helper class to emit change events with consistent error handling
 */
class ChangeEventEmitter<T>(
    private val flow: MutableSharedFlow<T>,
    private val logTag: String
) {
    /**
     * Emits a change event with error handling and logging
     * @param event The event to emit
     * @param logMessage The log message to display on success
     */
    suspend fun emit(event: T, logMessage: String) {
        try {
            flow.emit(event)
            AddressBookLogger.d(logMessage, tag = logTag)
        } catch (e: Exception) {
            AddressBookLogger.e("Failed to emit change event: ${e.message}", tag = logTag, throwable = e)
        }
    }
    
    /**
     * Tries to emit a change event without suspending
     * @param event The event to emit
     * @param logMessage The log message to display on success
     * @return true if the event was emitted successfully
     */
    fun tryEmit(event: T, logMessage: String): Boolean {
        return try {
            val emitted = flow.tryEmit(event)
            if (emitted) {
                AddressBookLogger.d(logMessage, tag = logTag)
            } else {
                AddressBookLogger.w("Failed to emit change event (buffer full): $logMessage", tag = logTag)
            }
            emitted
        } catch (e: Exception) {
            AddressBookLogger.e("Failed to emit change event: ${e.message}", tag = logTag, throwable = e)
            false
        }
    }
}

object AddressBookLogger {
    private const val TAG = "AddressBook"

    fun v(message: String, tag: String = TAG) {
        Napier.v(message, tag = tag)
    }

    fun d(message: String, tag: String = TAG) {
        Napier.d(message, tag = tag)
    }

    fun i(message: String, tag: String = TAG) {
        Napier.i(message, tag = tag)
    }

    fun w(message: String, tag: String = TAG, throwable: Throwable? = null) {
        Napier.w(message, throwable, tag = tag)
    }

    fun e(message: String, tag: String = TAG, throwable: Throwable? = null) {
        Napier.e(message, throwable, tag = tag)
    }

    fun wtf(message: String, tag: String = TAG, throwable: Throwable? = null) {
        Napier.wtf(message, throwable, tag = tag)
    }
}