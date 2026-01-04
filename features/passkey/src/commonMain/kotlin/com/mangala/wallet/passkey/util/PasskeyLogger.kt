package com.mangala.wallet.passkey.util

import com.mangala.wallet.passkey.data.config.PasskeyConfig
import io.github.aakira.napier.Napier

/**
 * Logger utility for passkey module
 */
object PasskeyLogger {
    private const val TAG = "Passkey"
    
    fun d(message: String, throwable: Throwable? = null) {
        if (PasskeyConfig.ENABLE_DEBUG_LOGGING) {
            Napier.d(message, throwable, tag = TAG)
            // Also log to platform console for debugging
            println("[$TAG] $message")
        }
    }
    
    fun i(message: String, throwable: Throwable? = null) {
        Napier.i(message, throwable, tag = TAG)
        println("[$TAG] INFO: $message")
    }
    
    fun w(message: String, throwable: Throwable? = null) {
        Napier.w(message, throwable, tag = TAG)
        println("[$TAG] WARN: $message")
    }
    
    fun e(message: String, throwable: Throwable? = null) {
        Napier.e(message, throwable, tag = TAG)
        println("[$TAG] ERROR: $message")
    }
    
    fun v(message: String, throwable: Throwable? = null) {
        if (PasskeyConfig.ENABLE_DEBUG_LOGGING) {
            Napier.v(message, throwable, tag = TAG)
        }
    }
}