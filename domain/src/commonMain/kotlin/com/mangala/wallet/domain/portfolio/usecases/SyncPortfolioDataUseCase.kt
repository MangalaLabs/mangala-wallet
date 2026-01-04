package com.mangala.wallet.domain.portfolio.usecases

import com.mangala.wallet.domain.portfolio.model.PortfolioDetailResponse
import com.mangala.wallet.domain.portfolio.repository.PortfolioRepository
import kotlinx.coroutines.delay

/**
 * Use case for syncing portfolio data from remote API
 * Handles background synchronization with retry logic and error handling
 */
class SyncPortfolioDataUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    
    /**
     * Sync portfolio data for a specific portfolio
     * @param portfolioId Unique portfolio identifier
     * @return Result containing updated portfolio detail response or error
     */
    suspend operator fun invoke(portfolioId: String): Result<PortfolioDetailResponse> {
        return try {
            logSyncStart(portfolioId)
            val result = portfolioRepository.syncPortfolioData(portfolioId)
            
            if (result.isSuccess) {
                logSyncSuccess(portfolioId, result.getOrNull())
            } else {
                logSyncError(portfolioId, result.exceptionOrNull())
            }
            
            result
        } catch (e: Exception) {
            logSyncError(portfolioId, e)
            Result.failure(e)
        }
    }
    
    /**
     * Sync portfolio data with retry logic
     * @param portfolioId Portfolio identifier
     * @param maxRetries Maximum number of retry attempts
     * @param retryDelayMs Delay between retries in milliseconds
     * @return Result containing updated portfolio detail response or error
     */
    suspend fun syncWithRetry(
        portfolioId: String,
        maxRetries: Int = 3,
        retryDelayMs: Long = 1000
    ): Result<PortfolioDetailResponse> {
        var lastException: Exception? = null
        
        repeat(maxRetries + 1) { attempt ->
            try {
                val result = invoke(portfolioId)
                if (result.isSuccess) {
                    if (attempt > 0) {
                        logRetrySuccess(portfolioId, attempt)
                    }
                    return result
                } else {
                    lastException = result.exceptionOrNull() as? Exception
                }
            } catch (e: Exception) {
                lastException = e
                logRetryAttempt(portfolioId, attempt + 1, maxRetries + 1, e)
            }
            
            // Don't delay after the last attempt
            if (attempt < maxRetries) {
                delay(retryDelayMs * (attempt + 1)) // Exponential backoff
            }
        }
        
        return Result.failure(lastException ?: Exception("Sync failed after $maxRetries retries"))
    }
    
    /**
     * Sync multiple portfolios concurrently
     * @param portfolioIds List of portfolio identifiers
     * @return Map of portfolio ID to sync result
     */
    suspend fun syncMultiplePortfolios(portfolioIds: List<String>): Map<String, Result<PortfolioDetailResponse>> {
        val results = mutableMapOf<String, Result<PortfolioDetailResponse>>()
        
        // For now, sync sequentially to avoid overwhelming the API
        // In the future, this could be made concurrent with proper rate limiting
        portfolioIds.forEach { portfolioId ->
            results[portfolioId] = invoke(portfolioId)
        }
        
        return results
    }
    
    /**
     * Background sync for all user portfolios
     * This could be called periodically or on app resume
     * @return Number of successfully synced portfolios
     */
    suspend fun backgroundSyncAll(): Int {
        var successCount = 0
        
        try {
            val cachedPortfolios = portfolioRepository.getCachedPortfolios()
            logBackgroundSyncStart(cachedPortfolios.size)
            
            cachedPortfolios.forEach { portfolio ->
                val result = syncWithRetry(portfolio.userId, maxRetries = 1, retryDelayMs = 500)
                if (result.isSuccess) {
                    successCount++
                }
            }
            
            logBackgroundSyncComplete(successCount, cachedPortfolios.size)
        } catch (e: Exception) {
            logBackgroundSyncError(e)
        }
        
        return successCount
    }
    
    private fun logSyncStart(portfolioId: String) {
        println("Starting portfolio sync for: $portfolioId")
    }
    
    private fun logSyncSuccess(portfolioId: String, data: PortfolioDetailResponse?) {
        val balance = data?.portfolio?.totals?.balanceUsdt
        println("Portfolio sync successful for $portfolioId, Balance: $balance USDT")
    }
    
    private fun logSyncError(portfolioId: String, error: Throwable?) {
        println("Portfolio sync failed for $portfolioId: ${error?.message}")
    }
    
    private fun logRetryAttempt(portfolioId: String, attempt: Int, maxAttempts: Int, error: Exception) {
        println("Portfolio sync retry $attempt/$maxAttempts for $portfolioId: ${error.message}")
    }
    
    private fun logRetrySuccess(portfolioId: String, attempt: Int) {
        println("Portfolio sync succeeded on retry attempt $attempt for $portfolioId")
    }
    
    private fun logBackgroundSyncStart(portfolioCount: Int) {
        println("Starting background sync for $portfolioCount portfolios")
    }
    
    private fun logBackgroundSyncComplete(successCount: Int, totalCount: Int) {
        println("Background sync complete: $successCount/$totalCount portfolios synced successfully")
    }
    
    private fun logBackgroundSyncError(error: Exception) {
        println("Background sync failed: ${error.message}")
    }
}