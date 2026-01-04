package com.mangala.wallet.features.addressbook.domain.qr

/**
 * QR validation utilities for testing and debugging
 */
object QrValidation {
    
    /**
     * Validate QR display data for completeness
     */
    fun validateQrDisplayData(data: QrDisplayData): List<String> {
        val issues = mutableListOf<String>()
        
        if (data.id.isBlank()) {
            issues.add("ID cannot be blank")
        }
        
        if (data.title.isBlank()) {
            issues.add("Title cannot be blank")
        }
        
        when (data.type) {
            QrType.CONTACT -> {
                if (data.primaryInfo.isNullOrBlank()) {
                    issues.add("Contact QR must have wallet address")
                }
                if (data.secondaryInfo.isNullOrBlank()) {
                    issues.add("Contact QR should have blockchain symbol")
                }
            }
            QrType.GROUP -> {
                if (data.primaryInfo.isNullOrBlank()) {
                    issues.add("Group QR should have member count")
                }
            }
            QrType.TAG -> {
                if (data.primaryInfo.isNullOrBlank()) {
                    issues.add("Tag QR should have contact count")
                }
            }
            QrType.ADDRESS -> {
                if (data.primaryInfo.isNullOrBlank()) {
                    issues.add("Address QR must have wallet address")
                }
            }
        }
        
        return issues
    }
    
    /**
     * Validate QR content format
     */
    fun validateQrContent(content: String): List<String> {
        val issues = mutableListOf<String>()
        
        if (content.isBlank()) {
            issues.add("QR content cannot be blank")
            return issues
        }
        
        try {
            // Try to parse as JSON
            kotlinx.serialization.json.Json.parseToJsonElement(content)
        } catch (e: Exception) {
            issues.add("QR content is not valid JSON: ${e.message}")
        }
        
        return issues
    }
    
    /**
     * Performance metrics for QR operations
     */
    data class QrMetrics(
        val loadTimeMs: Long,
        val generateTimeMs: Long,
        val cacheHit: Boolean,
        val dataSize: Int
    )
    
    /**
     * Helper to measure QR operation performance
     */
    suspend inline fun <T> measureQrOperation(
        operation: suspend () -> T
    ): Pair<T, Long> {
        val startTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val result = operation()
        val endTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        return result to (endTime - startTime)
    }
}