package com.mangala.wallet.features.addressbook.presentation.group

import com.mangala.wallet.features.addressbook.data.model.group.GroupModel

/**
 * Utility functions for GroupsContent
 */
object GroupsContentUtils {
    /**
     * Filter groups based on search query and filter option
     */
    fun filterGroups(
        groups: List<GroupModel>,
        searchQuery: String,
        selectedFilter: GroupFilterOption
    ): List<GroupModel> {
        return groups.filter { group ->
            val matchesSearch = if (searchQuery.isBlank()) true else
                group.name.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                GroupFilterOption.ALL -> true
                // Implement other filter options when the data model supports them
                // GroupFilterOption.FAVORITES -> group.isFavorite
                // GroupFilterOption.MOST_USED -> group.usageCount > 0
            }

            matchesSearch && matchesFilter
        }
    }
}