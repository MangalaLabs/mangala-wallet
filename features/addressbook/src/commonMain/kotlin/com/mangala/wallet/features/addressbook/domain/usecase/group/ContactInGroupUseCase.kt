package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository


class ContactInGroupUseCase(private val groupRepository: GroupRepository) {
    /**
     * Kiểm tra một contact có thuộc group không
     * @param contactId ID của contact
     * @param groupId ID của group
     * @return true nếu contact thuộc group
     */
    suspend operator fun invoke(contactId: String, groupId: String): Boolean {
        return groupRepository.contactInGroup(contactId, groupId)
    }
}