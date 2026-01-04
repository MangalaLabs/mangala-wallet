package com.mangala.wallet.features.addressbook.presentation.contact.list

import com.mangala.wallet.features.addressbook.presentation.common.AlphabetListKeyGenerator
import com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel

/**
 * Generates stable, unique keys for LazyColumn items in contact list.
 * This ensures no duplicate keys even when data changes or paging occurs.
 * 
 * Uses the generic AlphabetListKeyGenerator internally for consistency
 * across all alphabet-grouped lists in the address book.
 */
object ContactListKeyGenerator {
    
    // Delegate to the generic implementation
    private val keyGenerator = AlphabetListKeyGenerator.contactsKeyGenerator
    
    /**
     * Generates a unique, stable key for a contact list item.
     * 
     * @param item The item to generate a key for
     * @param index The current index in the list
     * @param previousItemId The ID of the previous item (for context-based stability)
     * @return A unique, stable key
     */
    fun generateKey(
        item: ContactGroupedByAlphabetUiModel,
        index: Int,
        previousItemId: String? = null
    ): String {
        return when (item) {
            is ContactGroupedByAlphabetUiModel.AlphabetHeader -> {
                keyGenerator.generateHeaderKey(item.alphabet, previousItemId)
            }
            is ContactGroupedByAlphabetUiModel.ContactItem -> {
                keyGenerator.generateItemKey(item.contact.contactId)
            }
        }
    }
    
    /**
     * Clears the tracking data. Should be called when the list is refreshed
     * or when navigating away from the screen.
     */
    fun clear() {
        keyGenerator.clear()
    }
    
    /**
     * Resets tracking for a specific alphabet.
     * Useful when data is partially updated.
     */
    fun resetAlphabet(alphabet: String) {
        keyGenerator.resetAlphabet(alphabet)
    }
}