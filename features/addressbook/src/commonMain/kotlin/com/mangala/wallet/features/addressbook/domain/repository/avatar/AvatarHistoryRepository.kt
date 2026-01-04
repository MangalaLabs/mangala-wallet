package com.mangala.wallet.features.addressbook.domain.repository.avatar

import com.mangala.wallet.features.addressbook.data.model.avatar.AvatarHistoryEntity
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Avatar History management
 */
interface AvatarHistoryRepository {
    
    // =====================================================
    // SAVE/UPDATE OPERATIONS
    // =====================================================
    
    /**
     * Save or update avatar usage
     */
    suspend fun saveAvatarUsage(
        avatarSource: AvatarSource,
        entityType: String = "general",
        entityId: String? = null
    ): Result<Unit>
    
    /**
     * Batch save multiple avatar usages
     */
    suspend fun batchSaveAvatarUsage(
        avatarSources: List<AvatarSource>
    ): Result<Unit>
    
    // =====================================================
    // QUERY OPERATIONS
    // =====================================================
    
    /**
     * Get recent avatars with pagination
     */
    suspend fun getRecentAvatars(
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<AvatarHistoryEntity>>
    
    /**
     * Get popular avatars (high usage count or global favorites)
     */
    suspend fun getPopularAvatars(
        limit: Int = 20
    ): Result<List<AvatarHistoryEntity>>
    
    /**
     * Get global favorite avatars
     */
    suspend fun getGlobalFavoriteAvatars(): Result<List<AvatarHistoryEntity>>
    
    /**
     * Search avatars by query
     */
    suspend fun searchAvatars(
        query: String,
        limit: Int = 20
    ): Result<List<AvatarHistoryEntity>>
    
    /**
     * Get avatar by hash
     */
    suspend fun getAvatarByHash(hash: String): Result<AvatarHistoryEntity?>
    
    /**
     * Get total avatar count
     */
    suspend fun getTotalAvatarCount(): Result<Long>
    
    // =====================================================
    // FLOW OPERATIONS (for reactive UI)
    // =====================================================
    
    /**
     * Observe recent avatars as Flow
     */
    fun observeRecentAvatars(limit: Int = 20): Flow<List<AvatarHistoryEntity>>
    
    /**
     * Observe popular avatars as Flow
     */
    fun observePopularAvatars(limit: Int = 20): Flow<List<AvatarHistoryEntity>>
    
    /**
     * Observe search results as Flow
     */
    fun observeSearchResults(query: String, limit: Int = 20): Flow<List<AvatarHistoryEntity>>
    
    // =====================================================
    // MANAGEMENT OPERATIONS
    // =====================================================
    
    /**
     * Promote avatar to global favorite
     */
    suspend fun promoteToGlobalFavorite(avatarSourceHash: String): Result<Unit>
    
    /**
     * Remove from global favorites
     */
    suspend fun removeFromGlobalFavorites(avatarSourceHash: String): Result<Unit>
    
    /**
     * Delete old avatars (cleanup)
     */
    suspend fun deleteOldAvatars(olderThanTimestamp: Long): Result<Int>
    
    /**
     * Clear all avatar history
     */
    suspend fun clearAvatarHistory(): Result<Unit>
    
    // =====================================================
    // CACHE OPERATIONS
    // =====================================================
    
    /**
     * Get cached recent avatars (if available)
     */
    suspend fun getCachedRecentAvatars(): List<AvatarHistoryEntity>?
    
    /**
     * Preload popular avatars to cache
     */
    suspend fun preloadPopularAvatarsToCache(): Result<Unit>
    
    /**
     * Clear cache
     */
    suspend fun clearCache()
    
    // =====================================================
    // ANALYTICS & MAINTENANCE
    // =====================================================
    
    /**
     * Get usage analytics
     */
    suspend fun getUsageAnalytics(): Result<AvatarUsageAnalytics>
    
    /**
     * Perform maintenance cleanup
     */
    suspend fun performMaintenance(): Result<MaintenanceResult>
}

/**
 * Avatar usage analytics data
 */
data class AvatarUsageAnalytics(
    val totalAvatars: Long,
    val totalUsage: Long,
    val globalFavorites: Long,
    val recentAvatars: Long,
    val topUsedType: String,
    val averageUsagePerAvatar: Double,
    val cacheHitRate: Double
)

/**
 * Maintenance operation result
 */
data class MaintenanceResult(
    val deletedOldAvatars: Int,
    val promotedToFavorites: Int,
    val cacheCleared: Boolean,
    val databaseOptimized: Boolean,
    val executionTimeMs: Long
)