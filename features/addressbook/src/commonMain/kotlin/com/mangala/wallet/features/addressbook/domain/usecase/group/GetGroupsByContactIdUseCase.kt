package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository

class GetGroupsByContactIdUseCase(private val groupRepository: GroupRepository) {
    /**
     * Lấy danh sách groups mà một contact thuộc về
     * @param contactId ID của contact
     * @return Danh sách các groups
     */
    suspend operator fun invoke(contactId: String): List<GroupEntity> {
        return groupRepository.getGroupsByContactId(contactId)
    }
}