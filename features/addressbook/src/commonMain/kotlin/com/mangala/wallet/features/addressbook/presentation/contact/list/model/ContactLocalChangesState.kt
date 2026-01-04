package com.mangala.wallet.features.addressbook.presentation.contact.list.model

/**
 * State for tracking local changes to contacts before they are persisted to the database.
 * This allows for optimistic UI updates without constantly refreshing the paging data.
 */
data class ContactLocalChangesState(
    val deletedContactIds: Set<String> = emptySet(),
    val favoriteChanges: Map<String, Boolean> = emptyMap()
)