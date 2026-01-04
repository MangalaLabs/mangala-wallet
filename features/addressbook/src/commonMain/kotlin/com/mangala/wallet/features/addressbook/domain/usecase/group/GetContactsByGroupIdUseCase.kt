package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import kotlinx.coroutines.flow.Flow

class GetContactsByGroupIdUseCase(private val groupRepository: GroupRepository) {
    /**
     * Lấy danh sách contacts trong một group
     * @param groupId ID của group
     * @param limit Số lượng records tối đa
     * @param offset Vị trí bắt đầu
     * @return Flow danh sách contacts
     */
    operator fun invoke(groupId: String, limit: Int = 50, offset: Int = 0): Flow<List<ContactEntity>> {
        return groupRepository.getContactsByGroupId(groupId, limit, offset)
    }
}