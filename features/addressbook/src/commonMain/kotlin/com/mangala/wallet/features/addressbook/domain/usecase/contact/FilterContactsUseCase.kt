package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class FilterContactsUseCase(
    private val contactRepository: ContactRepository,
    private val tagRepository: TagRepository,
    private val groupRepository: GroupRepository
) {
    /**
     * Filter contacts based on multiple criteria (one-time fetch)
     * @param filterCriteria Filter criteria including tags, groups, blockchains, favorites
     * @param page Page number (0-based)
     * @param pageSize Number of items per page
     * @return Result with list of matching contacts
     */
    suspend operator fun invoke(
        filterCriteria: FilterCriteria,
        page: Int,
        pageSize: Int
    ): Result<List<ContactModel>> {
        return try {
            val contacts = contactRepository.filterContacts(
                query = filterCriteria.searchQuery,
                tagIds = filterCriteria.tagIds,
                groupIds = filterCriteria.groupIds,
                blockchainIds = filterCriteria.blockchainIds,
                onlyFavorites = filterCriteria.onlyFavorites,
                sortOrder = filterCriteria.sortOrder,
                limit = pageSize,
                offset = page * pageSize
            )
            Result.success(contacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Observe contacts with filtering (real-time updates)
     * @param filterCriteria Filter criteria including tags, groups, blockchains, favorites
     * @return Flow emitting filtered contacts whenever the underlying data changes
     */
    fun observeFilteredContacts(
        filterCriteria: FilterCriteria,
        pageSize: Int = 50
    ): Flow<Result<List<ContactModel>>> = contactRepository.observeFilteredContacts(
        query = filterCriteria.searchQuery,
        tagIds = filterCriteria.tagIds,
        groupIds = filterCriteria.groupIds,
        blockchainIds = filterCriteria.blockchainIds,
        onlyFavorites = filterCriteria.onlyFavorites,
        sortOrder = filterCriteria.sortOrder,
        limit = pageSize,
        offset = 0 // Luôn bắt đầu từ trang đầu tiên cho cập nhật real-time
    )
}

data class FilterCriteria(
    val searchQuery: String = "",
    val tagIds: List<String> = emptyList(),
    val groupIds: List<String> = emptyList(),
    val blockchainIds: List<String> = emptyList(),
    val onlyFavorites: Boolean = false,
    val sortOrder: String = "name_asc"
)