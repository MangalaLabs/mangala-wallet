package com.mangala.wallet.features.addressbook.presentation.group.model

/**
 * State for tracking local changes to groups before they are persisted to the database.
 * This allows for optimistic UI updates without constantly refreshing the paging data.
 */
data class GroupLocalChangesState(
    val deletedGroupIds: Set<String> = emptySet()
)