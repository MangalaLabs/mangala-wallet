package com.mangala.wallet.features.addressbook.presentation.tag.model

/**
 * State for tracking local changes to tags before they are persisted to the database.
 * This allows for optimistic UI updates without constantly refreshing the paging data.
 */
data class TagLocalChangesState(
    val deletedTagIds: Set<String> = emptySet()
)