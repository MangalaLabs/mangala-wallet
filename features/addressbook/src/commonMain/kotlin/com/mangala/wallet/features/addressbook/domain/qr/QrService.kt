package com.mangala.wallet.features.addressbook.domain.qr

import com.mangala.wallet.features.addressbook.domain.qr.loaders.ContactQrDataLoader
import com.mangala.wallet.features.addressbook.domain.qr.loaders.GroupQrDataLoader
import com.mangala.wallet.features.addressbook.domain.qr.loaders.TagQrDataLoader
import com.mangala.wallet.features.addressbook.domain.qr.loaders.AddressQrDataLoader

/**
 * Unified QR service that handles all QR operations
 */
class QrService(
    private val contactLoader: ContactQrDataLoader,
    private val groupLoader: GroupQrDataLoader,
    private val tagLoader: TagQrDataLoader,
    private val addressLoader: AddressQrDataLoader,
    private val contentGenerator: QrContentGenerator,
    private val cache: QrCache
) {
    
    /**
     * Load QR data for any data type
     */
    suspend fun loadQrData(dataType: QrDataType): QrLoadResult {
        val cacheKey = generateCacheKey(dataType)
        
        // Try cache first
        val cachedData = cache.get(cacheKey)
        if (cachedData != null) {
            return QrLoadResult.Success(cachedData)
        }
        
        // Load from appropriate loader
        val result = when (dataType) {
            is QrDataType.Contact -> contactLoader.loadData(dataType.contactId)
            is QrDataType.Group -> groupLoader.loadData(dataType.groupId)
            is QrDataType.Tag -> tagLoader.loadData(dataType.tagId)
            is QrDataType.Address -> addressLoader.loadData(dataType.addressId)
        }
        
        // Cache successful results
        if (result is QrLoadResult.Success) {
            cache.put(cacheKey, result.data)
        }
        
        return result
    }
    
    /**
     * Generate QR content string
     */
    fun generateQrContent(displayData: QrDisplayData): QrContentResult {
        return contentGenerator.generateContent(displayData)
    }
    
    /**
     * Invalidate cache for specific data
     */
    suspend fun invalidateCache(dataType: QrDataType) {
        val cacheKey = generateCacheKey(dataType)
        cache.invalidate(cacheKey)
    }
    
    /**
     * Clear all cache
     */
    suspend fun clearCache() {
        cache.clear()
    }
    
    private fun generateCacheKey(dataType: QrDataType): String {
        return when (dataType) {
            is QrDataType.Contact -> "contact_${dataType.contactId}"
            is QrDataType.Group -> "group_${dataType.groupId}"
            is QrDataType.Tag -> "tag_${dataType.tagId}"
            is QrDataType.Address -> "address_${dataType.addressId}"
        }
    }
}