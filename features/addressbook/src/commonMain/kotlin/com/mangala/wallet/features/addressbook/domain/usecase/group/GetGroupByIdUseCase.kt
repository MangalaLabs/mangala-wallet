package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository

class GetGroupByIdUseCase(private val groupRepository: GroupRepository) {
    /**
     * Lấy một group theo ID
     * @param id ID của group cần lấy
     * @return Group hoặc null nếu không tìm thấy
     */
    suspend operator fun invoke(id: String): GroupEntity? {
        return groupRepository.getGroupById(id)
    }
}