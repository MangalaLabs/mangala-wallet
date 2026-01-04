package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository

class InsertGroupUseCase(private val groupRepository: GroupRepository) {
    /**
     * Lưu một group mới
     * @param group Group cần lưu
     * @return ID của group sau khi lưu
     */
    suspend operator fun invoke(group: GroupEntity): String {
        return groupRepository.insertGroup(group)
    }
}