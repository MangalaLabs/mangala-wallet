package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository

class CountContactsByGroupIdUseCase(private val groupRepository: GroupRepository) {
    /**
     * Đếm số contacts trong một group
     * @param groupId ID của group
     * @return Số lượng contacts
     */
    suspend operator fun invoke(groupId: String): Int {
        return groupRepository.countContactsByGroupId(groupId)
    }
}