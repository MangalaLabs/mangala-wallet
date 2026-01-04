package com.mangala.wallet.features.addressbook.domain.qr

/**
 * Base interface for QR data loading
 */
interface QrDataLoader<T> {
    suspend fun loadData(id: String): QrLoadResult
    fun getQrType(): QrType
}

/**
 * QR content generator interface
 */
interface QrContentGenerator {
    fun generateContent(displayData: QrDisplayData): QrContentResult
}

/**
 * QR cache interface for performance optimization
 */
interface QrCache {
    suspend fun get(key: String): QrDisplayData?
    suspend fun put(key: String, data: QrDisplayData)
    suspend fun invalidate(key: String)
    suspend fun clear()
}