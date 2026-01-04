package com.mangala.wallet.features.addressbook.presentation.shared.loading

/**
 * Unified loading state for all Address Book features
 * Based on the successful Group module pattern
 */
enum class LoadingState {
    None,
    InitialLoad,
    Saving,
    Updating,
    Deleting,
    LoadingMore,
    LoadingContacts,
    LoadingTags,
    LoadingGroups,
    Authenticating;

    /**
     * Check if any loading operation is in progress
     */
    val isLoading: Boolean
        get() = this != None
        
    /**
     * Get appropriate loading message for each state
     */
    fun getMessage(): String = when (this) {
        None -> ""
        InitialLoad -> "Loading..."
        Saving -> "Saving..."
        Updating -> "Updating..."
        Deleting -> "Deleting..."
        LoadingMore -> "Loading more..."
        LoadingContacts -> "Loading contacts..."
        LoadingTags -> "Loading tags..."
        LoadingGroups -> "Loading groups..."
        Authenticating -> "Authenticating..."
    }
}

/**
 * Extension to combine multiple loading states
 * Useful when multiple async operations run in parallel
 */
fun List<LoadingState>.combineStates(): LoadingState {
    return firstOrNull { it != LoadingState.None } ?: LoadingState.None
}