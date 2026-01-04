package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import kotlinx.coroutines.flow.Flow

class GetContactAddressByGroupIdUseCase(private val groupRepository: GroupRepository) {

    suspend operator fun invoke(
        groupId: String,
        limit: Int,
        offset: Int
    ): Flow<List<ContactModel>> {
        return groupRepository.getContactAddressByGroupId(
            groupId = groupId,
            limit = limit,
            offset = offset
        )
    }
}