package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository


class RemoveContactFromGroupUseCase(private val groupRepository: GroupRepository) {
    /**
     * Xóa một contact khỏi group
     * @param contactId ID của contact
     * @param groupId ID của group
     * @return true nếu xóa thành công
     */
    suspend operator fun invoke(contactId: String, groupId: String): Boolean {
        return groupRepository.removeContactFromGroup(contactId, groupId)
    }
}