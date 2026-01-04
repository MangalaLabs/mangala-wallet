package com.mangala.wallet.features.addressbook.presentation.common

import java.util.concurrent.ConcurrentHashMap

/**
 * Generic key generator for alphabet-grouped lists in address book.
 * Provides stable, unique keys for LazyColumn items to prevent duplicate key crashes.
 * 
 * This is used by ContactsContent, GroupsContent, and TagTabContent to ensure
 * proper list rendering without key conflicts.
 */
class AlphabetListKeyGenerator(private val prefix: String) {
    
    // Track header occurrences to ensure uniqueness
    private val headerOccurrences = ConcurrentHashMap<String, Int>()
    
    /**
     * Generates a unique key for alphabet headers.
     * Uses a combination of alphabet, context, and occurrence count.
     */
    fun generateHeaderKey(alphabet: String, previousItemId: String? = null): String {
        val contextKey = "${alphabet}_after_${previousItemId ?: "start"}"
        
        val occurrence = headerOccurrences.compute(contextKey) { _, count ->
            (count ?: 0) + 1
        } ?: 1
        
        return "${prefix}_header_${alphabet}_ctx_${previousItemId?.take(8) ?: "start"}_occ_$occurrence"
    }
    
    /**
     * Generates a unique key for list items.
     * Uses the item's unique ID with appropriate prefix.
     */
    fun generateItemKey(itemId: String): String {
        require(itemId.isNotEmpty()) { "Item ID cannot be empty" }
        return "${prefix}_item_$itemId"
    }
    
    /**
     * Clears all tracking data.
     * Should be called when the list is refreshed or screen is disposed.
     */
    fun clear() {
        headerOccurrences.clear()
    }
    
    /**
     * Resets tracking for a specific alphabet.
     */
    fun resetAlphabet(alphabet: String) {
        headerOccurrences.keys.removeIf { it.startsWith("${alphabet}_") }
    }
    
    companion object {
        /**
         * Singleton instances for each list type to maintain state across recompositions
         */
        val contactsKeyGenerator = AlphabetListKeyGenerator("contact")
        val groupsKeyGenerator = AlphabetListKeyGenerator("group")
        val tagsKeyGenerator = AlphabetListKeyGenerator("tag")
    }
}