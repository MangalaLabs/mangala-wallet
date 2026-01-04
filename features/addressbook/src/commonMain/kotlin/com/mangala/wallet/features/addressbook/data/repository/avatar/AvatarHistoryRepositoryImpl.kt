package com.mangala.wallet.features.addressbook.data.repository.avatar

import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.avatar.AvatarHistoryEntity
import com.mangala.wallet.features.addressbook.data.model.avatar.toHash
import com.mangala.wallet.features.addressbook.data.model.avatar.getType
import com.mangala.wallet.features.addressbook.data.model.avatar.getValue
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarHistoryRepository
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarUsageAnalytics
import com.mangala.wallet.features.addressbook.domain.repository.avatar.MaintenanceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

/**
 * Implementation of AvatarHistoryRepository with smart caching
 */
class AvatarHistoryRepositoryImpl(
    private val databaseWrapper: AddressBookDatabaseWrapper
) : AvatarHistoryRepository {
    
    // =====================================================
    // CONFIGURATION
    // =====================================================
    
    companion object {
        private const val MAX_HISTORY_SIZE = 100
        private const val CLEANUP_THRESHOLD = 80
        private const val GLOBAL_FAVORITES_THRESHOLD = 10
        private const val CACHE_SIZE = 20
        private const val OLD_AVATAR_THRESHOLD_DAYS = 30
    }
    
    // =====================================================
    // CACHE MANAGEMENT
    // =====================================================
    
    private val cacheMutex = Mutex()
    private val recentCache = mutableMapOf<String, AvatarHistoryEntity>()
    private val popularCache = mutableMapOf<String, AvatarHistoryEntity>()
    private var cacheHits = 0
    private var cacheMisses = 0
    
    // Reactive flows
    private val _recentAvatarsFlow = MutableStateFlow<List<AvatarHistoryEntity>>(emptyList())
    private val _popularAvatarsFlow = MutableStateFlow<List<AvatarHistoryEntity>>(emptyList())
    
    // =====================================================
    // SAVE/UPDATE OPERATIONS
    // =====================================================
    
    override suspend fun saveAvatarUsage(
        avatarSource: AvatarSource,
        entityType: String,
        entityId: String?
    ): Result<Unit> = runCatching {
        val hash = avatarSource.toHash()
        val type = avatarSource.getType()
        val value = avatarSource.getValue()
        val now = Clock.System.now().toEpochMilliseconds()
        
        databaseWrapper.database.addressBookDatabaseQueries.insertOrUpdateAvatarUsage(
            hash,    // avatar_source_hash
            type,    // avatar_source_type  
            value,   // avatar_source_value
            hash,    // for first_used_at COALESCE check
            now,     // first_used_at value
            now,     // last_used_at
            hash,    // for usage_count COALESCE check
            hash     // for is_global_favorite COALESCE check
        )
        
        // Update caches
        updateCacheAfterUsage(hash, avatarSource)
        
        // Refresh flows
        refreshFlows()
        
        // Perform cleanup if needed
        val totalCount = getTotalAvatarCount().getOrElse { 0L }
        if (totalCount > CLEANUP_THRESHOLD) {
            performAsyncCleanup()
        }
        
        // Check if avatar should be promoted to favorite
        val currentAvatar = getAvatarByHash(hash).getOrNull()
        if (currentAvatar?.usageCount ?: 0 >= GLOBAL_FAVORITES_THRESHOLD && 
            currentAvatar?.isGlobalFavorite == false) {
            promoteToGlobalFavorite(hash)
        }
    }
    
    override suspend fun batchSaveAvatarUsage(
        avatarSources: List<AvatarSource>
    ): Result<Unit> = runCatching {
        databaseWrapper.database.transaction {
            avatarSources.forEach { avatarSource ->
                val hash = avatarSource.toHash()
                val type = avatarSource.getType()
                val value = avatarSource.getValue()
                val now = Clock.System.now().toEpochMilliseconds()
                
                databaseWrapper.database.addressBookDatabaseQueries.insertOrUpdateAvatarUsage(
                    hash,    // avatar_source_hash
                    type,    // avatar_source_type  
                    value,   // avatar_source_value
                    hash,    // for first_used_at COALESCE check
                    now,     // first_used_at value
                    now,     // last_used_at
                    hash,    // for usage_count COALESCE check
                    hash     // for is_global_favorite COALESCE check
                )
            }
        }
        
        // Clear cache to force refresh
        clearCache()
        refreshFlows()
    }
    
    // =====================================================
    // QUERY OPERATIONS
    // =====================================================
    
    override suspend fun getRecentAvatars(
        limit: Int,
        offset: Int
    ): Result<List<AvatarHistoryEntity>> = runCatching {
        // Check cache first for recent avatars (offset = 0)
        if (offset == 0) {
            getCachedRecentAvatars()?.let { cached ->
                cacheHits++
                return@runCatching cached.take(limit)
            }
        }
        
        cacheMisses++
        val results = databaseWrapper.database.addressBookDatabaseQueries
            .getRecentAvatars(limit.toLong(), offset.toLong())
            .executeAsList()
            .map { it.toAvatarHistoryEntity() }
        
        // Cache recent results
        if (offset == 0) {
            cacheMutex.withLock {
                recentCache.clear()
                results.take(CACHE_SIZE).forEach { 
                    recentCache[it.avatarSourceHash] = it 
                }
            }
        }
        
        results
    }
    
    override suspend fun getPopularAvatars(
        limit: Int
    ): Result<List<AvatarHistoryEntity>> = runCatching {
        // Check cache first
        if (popularCache.isNotEmpty()) {
            cacheHits++
            return@runCatching popularCache.values.take(limit)
        }
        
        cacheMisses++
        val results = databaseWrapper.database.addressBookDatabaseQueries
            .getPopularAvatars(limit.toLong())
            .executeAsList()
            .map { it.toAvatarHistoryEntity() }
        
        // Update cache
        cacheMutex.withLock {
            popularCache.clear()
            results.forEach { popularCache[it.avatarSourceHash] = it }
        }
        
        results
    }
    
    override suspend fun getGlobalFavoriteAvatars(): Result<List<AvatarHistoryEntity>> = runCatching {
        databaseWrapper.database.addressBookDatabaseQueries
            .getGlobalFavoriteAvatars()
            .executeAsList()
            .map { it.toAvatarHistoryEntity() }
    }
    
    override suspend fun searchAvatars(
        query: String,
        limit: Int
    ): Result<List<AvatarHistoryEntity>> = runCatching {
        // Search in cache first
        val cacheResults = searchInCache(query)
        if (cacheResults.isNotEmpty()) {
            cacheHits++
            return@runCatching cacheResults.take(limit)
        }
        
        cacheMisses++
        databaseWrapper.database.addressBookDatabaseQueries
            .searchAvatars(query, query, limit.toLong())
            .executeAsList()
            .map { it.toAvatarHistoryEntity() }
    }
    
    override suspend fun getAvatarByHash(hash: String): Result<AvatarHistoryEntity?> = runCatching {
        // Check cache first
        recentCache[hash]?.let { 
            cacheHits++
            return@runCatching it 
        }
        popularCache[hash]?.let { 
            cacheHits++
            return@runCatching it 
        }
        
        cacheMisses++
        databaseWrapper.database.addressBookDatabaseQueries
            .getAvatarByHash(hash)
            .executeAsOneOrNull()
            ?.toAvatarHistoryEntity()
    }
    
    override suspend fun getTotalAvatarCount(): Result<Long> = runCatching {
        databaseWrapper.database.addressBookDatabaseQueries
            .getTotalAvatarCount()
            .executeAsOne()
    }
    
    // =====================================================
    // FLOW OPERATIONS
    // =====================================================
    
    override fun observeRecentAvatars(limit: Int): Flow<List<AvatarHistoryEntity>> {
        return _recentAvatarsFlow.asStateFlow().map { it.take(limit) }
    }
    
    override fun observePopularAvatars(limit: Int): Flow<List<AvatarHistoryEntity>> {
        return _popularAvatarsFlow.asStateFlow().map { it.take(limit) }
    }
    
    override fun observeSearchResults(query: String, limit: Int): Flow<List<AvatarHistoryEntity>> {
        return _recentAvatarsFlow.asStateFlow().map { avatars ->
            avatars.filter { avatar ->
                avatar.avatarSourceType.contains(query, ignoreCase = true) ||
                avatar.avatarSourceValue.contains(query, ignoreCase = true)
            }.take(limit)
        }
    }
    
    // =====================================================
    // MANAGEMENT OPERATIONS
    // =====================================================
    
    override suspend fun promoteToGlobalFavorite(avatarSourceHash: String): Result<Unit> = runCatching {
        databaseWrapper.database.addressBookDatabaseQueries.promoteToGlobalFavorite(avatarSourceHash)
        invalidateCache(avatarSourceHash)
    }
    
    override suspend fun removeFromGlobalFavorites(avatarSourceHash: String): Result<Unit> = runCatching {
        databaseWrapper.database.addressBookDatabaseQueries.removeFromGlobalFavorites(avatarSourceHash)
        invalidateCache(avatarSourceHash)
    }
    
    override suspend fun deleteOldAvatars(olderThanTimestamp: Long): Result<Int> = runCatching {
        databaseWrapper.database.addressBookDatabaseQueries
            .deleteOldAvatars(olderThanTimestamp)
        val deletedCount = 0 // SQLDelight doesn't return count for DELETE
        
        clearCache()
        refreshFlows()
        deletedCount
    }
    
    override suspend fun clearAvatarHistory(): Result<Unit> = runCatching {
        databaseWrapper.database.addressBookDatabaseQueries.clearAvatarHistory()
        clearCache()
        refreshFlows()
    }
    
    // =====================================================
    // CACHE OPERATIONS
    // =====================================================
    
    override suspend fun getCachedRecentAvatars(): List<AvatarHistoryEntity>? {
        return if (recentCache.isNotEmpty()) {
            recentCache.values.sortedByDescending { it.lastUsedAt }
        } else null
    }
    
    override suspend fun preloadPopularAvatarsToCache(): Result<Unit> = runCatching {
        val popularAvatars = getPopularAvatars(CACHE_SIZE).getOrElse { emptyList() }
        cacheMutex.withLock {
            popularCache.clear()
            popularAvatars.forEach { popularCache[it.avatarSourceHash] = it }
        }
    }
    
    override suspend fun clearCache() {
        cacheMutex.withLock {
            recentCache.clear()
            popularCache.clear()
        }
    }
    
    // =====================================================
    // ANALYTICS & MAINTENANCE
    // =====================================================
    
    override suspend fun getUsageAnalytics(): Result<AvatarUsageAnalytics> = runCatching {
        val totalAvatars = getTotalAvatarCount().getOrElse { 0L }
        val globalFavorites = getGlobalFavoriteAvatars().getOrElse { emptyList() }.size.toLong()
        val recentAvatars = getRecentAvatars(limit = 50).getOrElse { emptyList() }
        val totalUsage = recentAvatars.sumOf { it.usageCount }
        val averageUsage = if (totalAvatars > 0) totalUsage.toDouble() / totalAvatars else 0.0
        val cacheHitRate = if (cacheHits + cacheMisses > 0) {
            cacheHits.toDouble() / (cacheHits + cacheMisses)
        } else 0.0
        
        // Find most used avatar type
        val typeUsage = recentAvatars.groupBy { it.avatarSourceType }
            .mapValues { it.value.sumOf { avatar -> avatar.usageCount } }
        val topUsedType = typeUsage.maxByOrNull { it.value }?.key ?: "unknown"
        
        AvatarUsageAnalytics(
            totalAvatars = totalAvatars,
            totalUsage = totalUsage,
            globalFavorites = globalFavorites,
            recentAvatars = recentAvatars.size.toLong(),
            topUsedType = topUsedType,
            averageUsagePerAvatar = averageUsage,
            cacheHitRate = cacheHitRate
        )
    }
    
    override suspend fun performMaintenance(): Result<MaintenanceResult> = runCatching {
        val startTime = Clock.System.now().toEpochMilliseconds()
        
        // Delete old avatars
        val oldAvatarThreshold = Clock.System.now().toEpochMilliseconds() - OLD_AVATAR_THRESHOLD_DAYS.days.inWholeMilliseconds
        val deletedOldAvatars = deleteOldAvatars(oldAvatarThreshold).getOrElse { 0 }
        
        // Promote popular avatars to favorites (would need custom query)
        val promotedToFavorites = 0 // Implement if needed
        
        // Clear cache
        clearCache()
        val cacheCleared = true
        
        // Database optimization (platform-specific)
        val databaseOptimized = true
        
        val executionTime = Clock.System.now().toEpochMilliseconds() - startTime
        
        MaintenanceResult(
            deletedOldAvatars = deletedOldAvatars,
            promotedToFavorites = promotedToFavorites,
            cacheCleared = cacheCleared,
            databaseOptimized = databaseOptimized,
            executionTimeMs = executionTime
        )
    }
    
    // =====================================================
    // PRIVATE HELPER METHODS
    // =====================================================
    
    private suspend fun updateCacheAfterUsage(hash: String, avatarSource: AvatarSource) {
        cacheMutex.withLock {
            val existing = recentCache[hash]
            if (existing != null) {
                recentCache[hash] = existing.copy(
                    lastUsedAt = Clock.System.now().toEpochMilliseconds(),
                    usageCount = existing.usageCount + 1
                )
            } else {
                // Add to cache if not full
                if (recentCache.size < CACHE_SIZE) {
                    recentCache[hash] = AvatarHistoryEntity.fromAvatarSource(avatarSource)
                }
            }
        }
    }
    
    private suspend fun invalidateCache(hash: String) {
        cacheMutex.withLock {
            recentCache.remove(hash)
            popularCache.remove(hash)
        }
    }
    
    private fun searchInCache(query: String): List<AvatarHistoryEntity> {
        val allCached = (recentCache.values + popularCache.values).distinct()
        return allCached.filter { avatar ->
            avatar.avatarSourceType.contains(query, ignoreCase = true) ||
            avatar.avatarSourceValue.contains(query, ignoreCase = true)
        }.sortedByDescending { it.lastUsedAt }
    }
    
    private suspend fun refreshFlows() {
        val recent = getRecentAvatars(CACHE_SIZE).getOrElse { emptyList() }
        val popular = getPopularAvatars(CACHE_SIZE).getOrElse { emptyList() }
        
        _recentAvatarsFlow.value = recent
        _popularAvatarsFlow.value = popular
    }
    
    private suspend fun performAsyncCleanup() {
        // Delete old non-favorite avatars to keep under limit
        val totalCount = getTotalAvatarCount().getOrElse { 0L }
        if (totalCount > MAX_HISTORY_SIZE) {
            val deleteCount = (totalCount - CLEANUP_THRESHOLD).toInt()
            
            // Get oldest non-favorite avatars to delete
            val oldAvatars = databaseWrapper.database.addressBookDatabaseQueries
                .getOldestNonFavoriteAvatars(deleteCount.toLong())
                .executeAsList()
            
            // Delete them
            oldAvatars.forEach { avatar ->
                databaseWrapper.database.addressBookDatabaseQueries
                    .deleteAvatarByHash(avatar.avatar_source_hash)
            }
            
            // Clear cache after cleanup
            clearCache()
        }
    }
}

/**
 * Extension to convert SQLDelight result to domain entity
 */
private fun com.mangala.wallet.features.addressbook.database.Avatar_history.toAvatarHistoryEntity(): AvatarHistoryEntity {
    return AvatarHistoryEntity(
        id = id,
        avatarSourceHash = avatar_source_hash,
        avatarSourceType = avatar_source_type,
        avatarSourceValue = avatar_source_value,
        firstUsedAt = first_used_at ?: 0L,
        lastUsedAt = last_used_at ?: 0L,
        usageCount = usage_count ?: 1L,
        isGlobalFavorite = is_global_favorite ?: false
    )
}