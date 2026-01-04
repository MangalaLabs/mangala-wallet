package com.mangala.wallet.features.addressbook.domain.validation

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Simple cache for validation results to avoid redundant API calls
 */
class ValidationCache(
    private val maxCacheSize: Int = 100,
    private val cacheDuration: Duration = 5.minutes
) {
    private data class CacheEntry(
        val result: WalletValidationResult,
        val timestamp: Instant
    )
    
    private val cache = mutableMapOf<String, CacheEntry>()
    private val mutex = Mutex()
    
    /**
     * Get cached result if available and not expired
     */
    suspend fun get(key: String): WalletValidationResult? = mutex.withLock {
        val entry = cache[key] ?: return null
        
        val now = Clock.System.now()
        if (now - entry.timestamp > cacheDuration) {
            // Entry expired, remove it
            cache.remove(key)
            return null
        }
        
        return entry.result
    }
    
    /**
     * Store validation result in cache
     */
    suspend fun put(key: String, result: WalletValidationResult) = mutex.withLock {
        // Only cache successful validations and permanent errors
        when (result) {
            is WalletValidationResult.Success,
            is WalletValidationResult.Error -> {
                // If cache is full, remove oldest entries
                if (cache.size >= maxCacheSize) {
                    val oldestKey = cache.entries
                        .minByOrNull { it.value.timestamp }
                        ?.key
                    oldestKey?.let { cache.remove(it) }
                }
                
                cache[key] = CacheEntry(
                    result = result,
                    timestamp = Clock.System.now()
                )
            }
            is WalletValidationResult.Warning -> {
                // Don't cache warnings as they might be temporary
            }
        }
    }
    
    /**
     * Clear all cached entries
     */
    suspend fun clear() = mutex.withLock {
        cache.clear()
    }
    
    /**
     * Clear expired entries
     */
    suspend fun clearExpired() = mutex.withLock {
        val now = Clock.System.now()
        cache.entries.removeIf { (_, entry) ->
            now - entry.timestamp > cacheDuration
        }
    }
    
    /**
     * Generate cache key for validation
     */
    fun generateKey(
        address: String,
        blockchain: String,
        context: ValidationContext
    ): String {
        return "$blockchain:$address:$context"
    }
}