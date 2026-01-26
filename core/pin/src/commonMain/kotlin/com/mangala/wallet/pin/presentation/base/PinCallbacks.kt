package com.mangala.wallet.pin.presentation.base

/**
 * Callbacks for PIN setup flow
 */
interface PinSetupCallbacks {
    /**
     * Called when PIN is successfully set up
     */
    fun onSuccess()

    /**
     * Called when PIN setup fails
     */
    fun onError(error: String)

    /**
     * Called when user cancels PIN setup
     */
    fun onCancel()
}

/**
 * Callbacks for PIN unlock flow
 */
interface PinUnlockCallbacks {
    /**
     * Called when PIN is successfully validated
     */
    fun onSuccess()

    /**
     * Called when PIN validation fails
     */
    fun onError(error: String)

    /**
     * Called when account is locked
     */
    fun onLocked(unlockTime: String, remainingTime: String)

    /**
     * Called when rate limited
     */
    fun onRateLimited(retryAfterSeconds: Long)

    /**
     * Called when user cancels unlock
     */
    fun onCancel()
}

/**
 * Result of PIN operation
 */
sealed class PinResult {
    object Success : PinResult()
    data class Error(val message: String) : PinResult()
    data class Locked(val unlockTime: String) : PinResult()
    data class RateLimited(val retryAfterSeconds: Long) : PinResult()
}
