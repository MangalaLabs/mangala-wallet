package com.mangala.wallet.features.addressbook.domain.qr

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

/**
 * In-memory QR cache implementation with TTL support
 */
class QrCacheImpl(
    private val maxSize: Int = 100,
    private val ttlMs: Long = 5 * 60 * 1000L // 5 minutes
) : QrCache {
    
    private data class CacheEntry(
        val data: QrDisplayData,
        val timestamp: Long
    )
    
    private val cache = mutableMapOf<String, CacheEntry>()
    private val mutex = Mutex()
    
    override suspend fun get(key: String): QrDisplayData? = mutex.withLock {
        val entry = cache[key] ?: return null
        
        // Check if entry is expired
        val currentTime = Clock.System.now().toEpochMilliseconds()
        if (currentTime - entry.timestamp > ttlMs) {
            cache.remove(key)
            return null
        }
        
        entry.data
    }
    
    override suspend fun put(key: String, data: QrDisplayData) = mutex.withLock {
        // Remove oldest entries if cache is full
        if (cache.size >= maxSize) {
            val oldestKey = cache.entries.minByOrNull { it.value.timestamp }?.key
            oldestKey?.let { cache.remove(it) }
        }
        
        val currentTime = Clock.System.now().toEpochMilliseconds()
        cache[key] = CacheEntry(data, currentTime)
    }
    
    override suspend fun invalidate(key: String): Unit = mutex.withLock {
        cache.remove(key)
    }
    
    override suspend fun clear(): Unit = mutex.withLock {
        cache.clear()
    }
    
    /**
     * Remove expired entries from cache
     */
    suspend fun cleanup(): Unit = mutex.withLock {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val expiredKeys = cache.entries
            .filter { currentTime - it.value.timestamp > ttlMs }
            .map { it.key }
        
        expiredKeys.forEach { cache.remove(it) }
    }
}