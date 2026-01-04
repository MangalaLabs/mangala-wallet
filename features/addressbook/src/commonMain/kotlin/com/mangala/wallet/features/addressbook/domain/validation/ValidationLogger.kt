package com.mangala.wallet.features.addressbook.domain.validation

/**
 * Simple logging interface for validation operations
 * Can be extended with actual logging framework implementation
 */
interface ValidationLogger {
    fun debug(message: String)
    fun info(message: String)
    fun warning(message: String)
    fun error(message: String, throwable: Throwable? = null)
}

/**
 * No-op logger implementation for production
 * Replace with actual logger in debug builds
 */
object NoOpValidationLogger : ValidationLogger {
    override fun debug(message: String) { /* No-op */ }
    override fun info(message: String) { /* No-op */ }
    override fun warning(message: String) { /* No-op */ }
    override fun error(message: String, throwable: Throwable?) { /* No-op */ }
}

/**
 * Debug logger that prints to console
 * Only use in debug builds
 */
object DebugValidationLogger : ValidationLogger {
    override fun debug(message: String) {
        if (isDebugBuild()) {
            println("[DEBUG] $message")
        }
    }
    
    override fun info(message: String) {
        if (isDebugBuild()) {
            println("[INFO] $message")
        }
    }
    
    override fun warning(message: String) {
        if (isDebugBuild()) {
            println("[WARN] $message")
        }
    }
    
    override fun error(message: String, throwable: Throwable?) {
        if (isDebugBuild()) {
            println("[ERROR] $message")
            throwable?.printStackTrace()
        }
    }
    
    private fun isDebugBuild(): Boolean {
        // This should be configured based on your build configuration
        // For now, return false to disable all logging in production
        return false
    }
}