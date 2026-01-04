package com.mangala.wallet.features.addressbook.utils

/**
 * Standardized address formatter specifically for Contact feature
 * Provides consistent formatting across all contact-related screens
 */
object ContactAddressFormatter {
    
    /**
     * Standard format for contact list/grid views
     * Shows first 6 and last 4 characters - good balance for recognition
     * Example: "0x1234...abcd"
     */
    fun forListView(address: String): String {
        return formatAddress(address, prefixLength = 6, suffixLength = 4)
    }
    
    /**
     * Standard format for contact detail views
     * Shows first 8 and last 6 characters - more info while staying readable
     * Example: "0x123456...abcdef"
     */
    fun forDetailView(address: String): String {
        return formatAddress(address, prefixLength = 8, suffixLength = 6)
    }
    
    /**
     * Format for compact spaces (cards, chips)
     * Shows first 4 and last 4 characters - minimal but functional
     * Example: "0x12...cd"
     */
    fun forCompactView(address: String): String {
        return formatAddress(address, prefixLength = 4, suffixLength = 4)
    }
    
    /**
     * Format for QR code display - more generous
     * Shows first 12 and last 8 characters
     * Example: "0x123456789012...abcdefgh"
     */
    fun forQrDisplay(address: String): String {
        return formatAddress(address, prefixLength = 12, suffixLength = 8)
    }
    
    /**
     * Core formatting logic with consistent minimum length checking
     */
    private fun formatAddress(
        address: String, 
        prefixLength: Int, 
        suffixLength: Int
    ): String {
        // Calculate minimum meaningful length
        // Address must be at least (prefix + suffix + 3 for "...") + 2 extra chars
        // This ensures truncation actually saves space and makes sense
        val minimumLength = prefixLength + suffixLength + 5
        
        return if (address.length <= minimumLength) {
            // Address is too short to meaningfully truncate
            address
        } else {
            val prefix = address.take(prefixLength)
            val suffix = address.takeLast(suffixLength)
            "$prefix...$suffix"
        }
    }
    
    /**
     * Utility to check if an address would be truncated
     * Useful for UI decisions (showing tooltips, etc.)
     */
    fun willBeTruncated(address: String, prefixLength: Int, suffixLength: Int): Boolean {
        val minimumLength = prefixLength + suffixLength + 5
        return address.length > minimumLength
    }
    
    /**
     * Constants for common use cases
     */
    object Presets {
        const val LIST_PREFIX = 6
        const val LIST_SUFFIX = 4
        
        const val DETAIL_PREFIX = 8
        const val DETAIL_SUFFIX = 6
        
        const val COMPACT_PREFIX = 4
        const val COMPACT_SUFFIX = 4
        
        const val QR_PREFIX = 12
        const val QR_SUFFIX = 8
    }
}