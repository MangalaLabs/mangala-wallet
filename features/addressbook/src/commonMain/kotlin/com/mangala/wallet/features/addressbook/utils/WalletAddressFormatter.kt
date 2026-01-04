package com.mangala.wallet.features.addressbook.utils

/**
 * Utility object for formatting wallet addresses consistently across the app
 */
object WalletAddressFormatter {
    
    /**
     * Formats a wallet address to a shortened version for display
     * 
     * @param address The full wallet address to format
     * @param prefixLength Number of characters to show at the beginning (default: 6)
     * @param suffixLength Number of characters to show at the end (default: 4)
     * @param minLengthToShorten Minimum length before shortening is applied (default: 12)
     * @return Formatted address string
     */
    fun formatForDisplay(
        address: String,
        prefixLength: Int = 6,
        suffixLength: Int = 4,
        minLengthToShorten: Int = 12
    ): String {
        if (address.length <= minLengthToShorten) {
            return address
        }
        
        val prefix = address.take(prefixLength)
        val suffix = address.takeLast(suffixLength)
        return "${prefix}...${suffix}"
    }
    
    /**
     * Validates if an address format looks valid (basic validation)
     * 
     * @param address The address to validate
     * @return True if the address appears to be in a valid format
     */
    fun isValidAddressFormat(address: String): Boolean {
        return address.isNotBlank() && 
               address.length >= 20 && // Minimum reasonable length
               address.all { it.isLetterOrDigit() } // Basic alphanumeric check
    }
    
    /**
     * Constants for common formatting parameters
     */
    object Constants {
        const val DEFAULT_PREFIX_LENGTH = 6
        const val DEFAULT_SUFFIX_LENGTH = 4
        const val DEFAULT_MIN_LENGTH = 12
        const val COMPACT_PREFIX_LENGTH = 4
        const val COMPACT_SUFFIX_LENGTH = 3
    }
}