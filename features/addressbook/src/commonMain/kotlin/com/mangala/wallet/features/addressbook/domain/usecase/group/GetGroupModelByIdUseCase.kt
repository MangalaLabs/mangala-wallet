package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository

class GetGroupModelByIdUseCase(private val groupRepository: GroupRepository) {
    /**
     * Lấy một group model theo ID
     * @param id ID của group cần lấy
     * @return GroupModel hoặc null nếu không tìm thấy
     */
    suspend operator fun invoke(id: String): GroupModel? {
        return groupRepository.getGroupModelById(id)
    }
}