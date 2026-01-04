package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository

class AddContactToGroupUseCase(private val groupRepository: GroupRepository) {
    /**
     * Thêm một contact vào group với wallet address chỉ định
     * @param contactId ID của contact
     * @param groupId ID của group
     * @param walletAddressId ID của wallet address sẽ sử dụng trong group
     * @return true nếu thêm thành công
     */
    suspend operator fun invoke(contactId: String, groupId: String, walletAddressId: String): Boolean {
        return groupRepository.addContactToGroup(contactId, groupId, walletAddressId)
    }
}