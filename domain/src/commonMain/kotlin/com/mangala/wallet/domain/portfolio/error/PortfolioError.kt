package com.mangala.wallet.domain.portfolio.error

/**
 * Sealed class representing portfolio-specific errors
 */
sealed class PortfolioError(
    override val message: String,
    open val originalException: Throwable? = null
) : Exception(message, originalException) {
    
    /**
     * Network connectivity issues
     */
    data class NetworkError(
        override val message: String,
        override val originalException: Throwable? = null
    ) : PortfolioError(message, originalException)
    
    /**
     * Authentication or authorization failures
     */
    data class AuthenticationError(
        override val message: String,
        override val originalException: Throwable? = null
    ) : PortfolioError(message, originalException)
    
    /**
     * Portfolio not found for the given user/network
     */
    data class PortfolioNotFound(
        override val message: String,
        override val originalException: Throwable? = null
    ) : PortfolioError(message, originalException)
    
    /**
     * Server-side errors (5xx)
     */
    data class ServerError(
        override val message: String,
        override val originalException: Throwable? = null
    ) : PortfolioError(message, originalException)
    
    /**
     * Request timeout errors
     */
    data class TimeoutError(
        override val message: String,
        override val originalException: Throwable? = null
    ) : PortfolioError(message, originalException)
    
    /**
     * Balance calculation or data processing errors
     */
    data class BalanceCalculationError(
        override val message: String,
        override val originalException: Throwable? = null
    ) : PortfolioError(message, originalException)
    
    /**
     * Unknown or unexpected errors
     */
    data class UnknownError(
        override val message: String,
        override val originalException: Throwable? = null
    ) : PortfolioError(message, originalException)
    
    /**
     * Get user-friendly error message for display
     */
    fun getUserMessage(): String = message
    
    /**
     * Check if this error type should trigger a retry
     */
    fun isRetryable(): Boolean = when (this) {
        is NetworkError -> true
        is TimeoutError -> true
        is ServerError -> true
        is AuthenticationError -> false
        is PortfolioNotFound -> false
        is BalanceCalculationError -> false
        is UnknownError -> false
    }
    
    /**
     * Check if this error should trigger fallback to legacy balance loading
     */
    fun shouldFallbackToLegacy(): Boolean = when (this) {
        is NetworkError -> true
        is TimeoutError -> true
        is ServerError -> true
        is PortfolioNotFound -> true
        is AuthenticationError -> false // Don't fallback on auth errors
        is BalanceCalculationError -> true
        is UnknownError -> true
    }
}