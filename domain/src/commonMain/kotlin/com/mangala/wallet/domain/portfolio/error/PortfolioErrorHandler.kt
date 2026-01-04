package com.mangala.wallet.domain.portfolio.error

import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.random.Random

/**
 * Centralized error handling for portfolio operations
 */
class PortfolioErrorHandler {
    
    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val BASE_DELAY_MS = 1000L
        private const val MAX_DELAY_MS = 30000L
        private const val JITTER_FACTOR = 0.1
    }
    
    /**
     * Maps exceptions to user-friendly error types
     */
    fun mapToPortfolioError(exception: Throwable): PortfolioError {
        Napier.e("Portfolio operation failed", exception)
        
        return when {
            isNetworkError(exception) -> PortfolioError.NetworkError(
                message = "Network connection error. Please check your internet connection.",
                originalException = exception
            )
            isAuthenticationError(exception) -> PortfolioError.AuthenticationError(
                message = "Authentication failed. Please re-authenticate.",
                originalException = exception
            )
            isPortfolioNotFoundError(exception) -> PortfolioError.PortfolioNotFound(
                message = "Portfolio not found. It may need to be created first.",
                originalException = exception
            )
            isServerError(exception) -> PortfolioError.ServerError(
                message = "Server is currently unavailable. Please try again later.",
                originalException = exception
            )
            isTimeoutError(exception) -> PortfolioError.TimeoutError(
                message = "Request timed out. Please check your connection and try again.",
                originalException = exception
            )
            else -> PortfolioError.UnknownError(
                message = "An unexpected error occurred: ${exception.message ?: "Unknown"}",
                originalException = exception
            )
        }
    }
    
    /**
     * Executes a portfolio operation with retry logic and exponential backoff
     */
    suspend fun <T> executeWithRetry(
        operation: suspend () -> T,
        maxAttempts: Int = MAX_RETRY_ATTEMPTS,
        shouldRetry: (Throwable) -> Boolean = ::isRetryableError
    ): Result<T> {
        var lastException: Throwable? = null
        
        repeat(maxAttempts) { attempt ->
            try {
                val result = operation()
                if (attempt > 0) {
                    Napier.i("Portfolio operation succeeded after ${attempt + 1} attempts")
                }
                return Result.success(result)
            } catch (e: Throwable) {
                lastException = e
                
                if (!shouldRetry(e)) {
                    Napier.w("Portfolio operation failed with non-retryable error: ${e.message}")
                    return Result.failure(mapToPortfolioError(e))
                }
                
                if (attempt < maxAttempts - 1) {
                    val delayMs = calculateBackoffDelay(attempt)
                    Napier.w("Portfolio operation failed, retrying in ${delayMs}ms (attempt ${attempt + 1}/$maxAttempts)")
                    delay(delayMs)
                }
            }
        }
        
        Napier.e("Portfolio operation failed after $maxAttempts attempts")
        return Result.failure(
            lastException?.let { mapToPortfolioError(it) } 
                ?: PortfolioError.UnknownError("Operation failed after $maxAttempts attempts")
        )
    }
    
    /**
     * Calculates exponential backoff delay with jitter
     */
    private fun calculateBackoffDelay(attempt: Int): Long {
        val baseDelay = BASE_DELAY_MS * (2.0.pow(attempt)).toLong()
        val jitter = (baseDelay * JITTER_FACTOR * Random.nextDouble()).toLong()
        return (baseDelay + jitter).coerceAtMost(MAX_DELAY_MS)
    }
    
    /**
     * Determines if an error is retryable
     */
    private fun isRetryableError(exception: Throwable): Boolean {
        return when {
            isNetworkError(exception) -> true
            isTimeoutError(exception) -> true
            isServerError(exception) -> true
            isAuthenticationError(exception) -> false // Don't retry auth errors
            isPortfolioNotFoundError(exception) -> false // Don't retry not found
            else -> false
        }
    }
    
    /**
     * Checks if the exception is a network-related error
     */
    private fun isNetworkError(exception: Throwable): Boolean {
        val message = exception.message?.lowercase() ?: ""
        val className = exception::class.simpleName?.lowercase() ?: ""
        
        return message.contains("network") ||
                message.contains("connection") ||
                message.contains("host") ||
                message.contains("timeout") ||
                className.contains("network") ||
                className.contains("socket") ||
                className.contains("connect")
    }
    
    /**
     * Checks if the exception is an authentication error
     */
    private fun isAuthenticationError(exception: Throwable): Boolean {
        val message = exception.message?.lowercase() ?: ""
        
        return message.contains("unauthorized") ||
                message.contains("authentication") ||
                message.contains("401") ||
                message.contains("forbidden") ||
                message.contains("403")
    }
    
    /**
     * Checks if the exception indicates portfolio not found
     */
    private fun isPortfolioNotFoundError(exception: Throwable): Boolean {
        val message = exception.message?.lowercase() ?: ""
        
        return message.contains("not found") ||
                message.contains("404") ||
                message.contains("portfolio not found")
    }
    
    /**
     * Checks if the exception is a server error
     */
    private fun isServerError(exception: Throwable): Boolean {
        val message = exception.message?.lowercase() ?: ""
        
        return message.contains("server") ||
                message.contains("500") ||
                message.contains("502") ||
                message.contains("503") ||
                message.contains("504")
    }
    
    /**
     * Checks if the exception is a timeout error
     */
    private fun isTimeoutError(exception: Throwable): Boolean {
        val message = exception.message?.lowercase() ?: ""
        val className = exception::class.simpleName?.lowercase() ?: ""
        
        return message.contains("timeout") ||
                className.contains("timeout")
    }
}