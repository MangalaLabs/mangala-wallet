package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository


class DeleteGroupUseCase(private val groupRepository: GroupRepository) {
    /**
     * Xóa một group
     * @param id ID của group cần xóa
     * @return true nếu xóa thành công
     */
    suspend operator fun invoke(id: String): Boolean {
        return groupRepository.deleteGroup(id)
    }
}