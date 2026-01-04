package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.data.model.GroupDetailModel
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository

class GetGroupDetailByIdUseCase(private val groupRepository: GroupRepository) {
    /**
     * Lấy thông tin chi tiết của một group
     * @param id ID của group cần lấy
     * @return GroupDetail hoặc null nếu không tìm thấy
     */
    suspend operator fun invoke(id: String): GroupDetailModel? {
        return groupRepository.getGroupDetailById(id)
    }
}