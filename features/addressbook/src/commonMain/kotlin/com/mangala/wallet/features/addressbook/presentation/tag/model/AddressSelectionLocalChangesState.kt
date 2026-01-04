package com.mangala.wallet.features.addressbook.presentation.tag.model

/**
 * Local changes state for optimistic updates in AddressSelectionViewModel
 * Tracks contacts that are added/removed locally before persisting to database
 */
data class AddressSelectionLocalChangesState(
    val newlySelectedContactIds: Set<String> = emptySet(), // Contacts added to tag locally
    val removedContactIds: Set<String> = emptySet() // Contacts removed from tag locally
)