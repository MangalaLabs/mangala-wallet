package com.mangala.wallet.features.addressbook.domain.cache

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

/**
 * In-memory cache for QR code images to improve performance
 */
class QrCodeCache {
    private val cache = mutableMapOf<String, CacheEntry>()
    private val mutex = Mutex()

    // Maximum number of cached QR codes
    private val maxSize = 50

    // Cache entry TTL in milliseconds (5 minutes)
    private val ttlMillis = 5 * 60 * 1000L

    /**
     * Get cached QR code image if available and not expired
     */
    suspend fun getCachedQr(key: String): ImageBitmap? = mutex.withLock {
        val entry = cache[key]
        if (entry != null && !entry.isExpired()) {
            // Move to end (LRU)
            cache.remove(key)
            cache[key] = entry
            return entry.image
        } else if (entry != null && entry.isExpired()) {
            cache.remove(key)
        }
        return null
    }

    /**
     * Cache QR code image
     */
    suspend fun cacheQr(key: String, image: ImageBitmap) = mutex.withLock {
        // Remove expired entries
        cleanupExpired()

        // If cache is full, remove oldest entry (LRU)
        if (cache.size >= maxSize) {
            val oldestKey = cache.keys.first()
            cache.remove(oldestKey)
        }

        cache[key] = CacheEntry(image, Clock.System.now().toEpochMilliseconds())
    }

    /**
     * Clear all cached QR codes
     */
    suspend fun clearCache() = mutex.withLock {
        cache.clear()
    }

    /**
     * Get cache statistics
     */
    suspend fun getCacheStats(): CacheStats = mutex.withLock {
        cleanupExpired()
        CacheStats(
            size = cache.size,
            maxSize = maxSize,
            hitRatio = 0.0 // Could be tracked if needed
        )
    }

    /**
     * Remove expired entries from cache
     */
    private fun cleanupExpired() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val keysToRemove = cache.filterValues { it.isExpired(currentTime) }.keys
        keysToRemove.forEach { cache.remove(it) }
    }

    /**
     * Generate cache key for QR data
     */
    fun generateCacheKey(qrData: String): String {
        return qrData.hashCode().toString()
    }

    private data class CacheEntry(
        val image: ImageBitmap,
        val timestamp: Long
    ) {
        fun isExpired(currentTime: Long = Clock.System.now().toEpochMilliseconds()): Boolean {
            return currentTime - timestamp > 5 * 60 * 1000L // 5 minutes TTL
        }
    }
}

data class CacheStats(
    val size: Int,
    val maxSize: Int,
    val hitRatio: Double
)