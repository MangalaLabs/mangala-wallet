package com.mangala.wallet.features.addressbook.utils

/**
 * Constants used throughout the addressbook feature
 */
object Constants {
    
    /**
     * Pagination constants
     */
    object Pagination {
        const val DEFAULT_PAGE_SIZE = 20
        const val LARGE_PAGE_SIZE = 30
        const val SMALL_PAGE_SIZE = 10
    }
    
    /**
     * Search and debouncing constants
     */
    object Search {
        const val DEBOUNCE_DELAY_MS = 300L
        const val MIN_SEARCH_LENGTH = 1
        const val MAX_SEARCH_LENGTH = 100
    }
    
    /**
     * UI timing constants
     */
    object Timing {
        const val TOAST_DURATION_MS = 2000L
        const val ANIMATION_DURATION_MS = 300L
        const val FEEDBACK_DURATION_MS = 1000L
    }
    
    /**
     * Group management constants
     */
    object Group {
        const val MAX_CONTACTS_PER_GROUP = 50
        const val MIN_GROUP_NAME_LENGTH = 1
        const val MAX_DESCRIPTION_LENGTH = 200
    }
    
    /**
     * Contact constants
     */
    object Contact {
        const val MIN_CONTACT_NAME_LENGTH = 1
        const val MAX_NOTES_LENGTH = 500
    }
    
    /**
     * Validation patterns
     */
    object Patterns {
        const val HEX_COLOR_PATTERN = "^[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?$"
        const val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
        const val PHONE_PATTERN = "^[+]?[0-9\\s\\-\\(\\)]{7,20}$"
    }
    
    /**
     * Error messages
     */
    object ErrorMessages {
        const val NETWORK_ERROR = "Network connection error. Please try again."
        const val GENERIC_ERROR = "An unexpected error occurred. Please try again."
        const val VALIDATION_ERROR = "Please check your input and try again."
        const val PERMISSION_ERROR = "Permission denied. Please check your settings."
        const val TIMEOUT_ERROR = "Request timed out. Please try again."
    }
}