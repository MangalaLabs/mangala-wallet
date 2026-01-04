package com.mangala.wallet.features.addressbook.domain.usecase.group

import app.cash.paging.PagingData
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import kotlinx.coroutines.flow.Flow

class GetAllGroupsUseCase(private val groupRepository: GroupRepository) {
    /**
     * Get paginated groups with optional search functionality
     * @param searchQuery Optional search query to filter groups (null/empty = get all)
     * @return Flow of PagingData for GroupModel
     */
    fun getPaginatedGroups(searchQuery: String? = null): Flow<PagingData<GroupModel>> {
        return groupRepository.getPaginatedGroups(searchQuery)
    }

    suspend operator fun invoke(): List<GroupModel> = groupRepository.getAllGroups()
}