package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.data.model.contact.ContactWithAddressesModel
import com.mangala.wallet.features.addressbook.data.model.contact.PaginatedContactsResult
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase to get contacts with their addresses by group ID with pagination by contact
 */
class GetContactsWithAddressesByGroupIdUseCase(private val groupRepository: GroupRepository) {
    
    suspend operator fun invoke(
        groupId: String,
        limit: Int,
        offset: Int
    ): Flow<PaginatedContactsResult> {
        return groupRepository.getContactsWithAddressesByGroupId(
            groupId = groupId,
            limit = limit,
            offset = offset
        )
    }
}