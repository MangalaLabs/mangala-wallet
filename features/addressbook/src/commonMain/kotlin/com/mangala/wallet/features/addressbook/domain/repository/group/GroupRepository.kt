package com.mangala.wallet.features.addressbook.domain.repository.group

import app.cash.paging.PagingData
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.GroupDetailModel
import com.mangala.wallet.features.addressbook.data.model.contact.ContactWithAddressesModel
import com.mangala.wallet.features.addressbook.data.model.contact.PaginatedContactsResult
import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.domain.model.GroupChangeEvent
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface cho việc quản lý Group và liên kết với Contact
 */
interface GroupRepository {
    /**
     * Flow that emits events when group data changes
     */
    val groupChanges: Flow<GroupChangeEvent>
    /**
     * Lấy một group theo ID
     * @param id ID của group cần lấy
     * @return Group hoặc null nếu không tìm thấy
     */
    suspend fun getGroupById(id: String): GroupEntity?

    /**
     * Lấy một group model theo ID
     * @param id ID của group cần lấy
     * @return GroupModel hoặc null nếu không tìm thấy
     */
    suspend fun getGroupModelById(id: String): GroupModel?

    /**
     * Lấy thông tin chi tiết của một group
     * @param id ID của group cần lấy
     * @return GroupDetail hoặc null nếu không tìm thấy
     */
    suspend fun getGroupDetailById(id: String): GroupDetailModel?

    suspend fun getAllGroups(): List<GroupModel>

    /**
     * Get paginated groups with search functionality (replaces getAllGroups and searchGroups)
     * @param searchQuery Optional search query to filter groups (null/empty = get all)
     * @return Flow of PagingData for GroupModel
     */
    fun getPaginatedGroups(
        searchQuery: String? = null
    ): Flow<PagingData<GroupModel>>

    /**
     * Lấy danh sách groups mà một contact thuộc về
     * @param contactId ID của contact
     * @return Danh sách các groups
     */
    suspend fun getGroupsByContactId(contactId: String): List<GroupEntity>

    /**
     * Lấy danh sách contacts trong một group
     * @param groupId ID của group
     * @param limit Số lượng records tối đa
     * @param offset Vị trí bắt đầu
     * @return Flow danh sách contacts
     */
    fun getContactsByGroupId(groupId: String, limit: Int = 50, offset: Int = 0): Flow<List<ContactEntity>>

    /**
     * Lưu một group mới
     * @param group Group cần lưu
     * @return ID của group sau khi lưu
     */
    suspend fun insertGroup(group: GroupEntity): String

    /**
     * Cập nhật một group hiện có
     * @param group Group cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateGroup(group: GroupEntity): Boolean

    /**
     * Xóa một group
     * @param id ID của group cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteGroup(id: String): Boolean
    
    /**
     * Tìm group bằng tên chính xác
     * @param name Tên group cần tìm
     * @return Group hoặc null nếu không tìm thấy
     */
    suspend fun findGroupByName(name: String): GroupEntity?

    /**
     * Thêm một contact vào group với wallet address chỉ định
     * @param contactId ID của contact
     * @param groupId ID của group
     * @param walletAddressId ID của wallet address sẽ sử dụng trong group
     * @return true nếu thêm thành công
     */
    suspend fun addContactToGroup(contactId: String, groupId: String, walletAddressId: String): Boolean

    /**
     * Xóa một contact khỏi group
     * @param contactId ID của contact
     * @param groupId ID của group
     * @return true nếu xóa thành công
     */
    suspend fun removeContactFromGroup(contactId: String, groupId: String): Boolean

    /**
     * Kiểm tra một contact có thuộc group không
     * @param contactId ID của contact
     * @param groupId ID của group
     * @return true nếu contact thuộc group
     */
    suspend fun contactInGroup(contactId: String, groupId: String): Boolean

    /**
     * Đếm số contacts trong một group
     * @param groupId ID của group
     * @return Số lượng contacts
     */
    suspend fun countContactsByGroupId(groupId: String): Int


    suspend fun updateGroupMainBlockchain(
        groupId: String,
        blockchainId: String
    ): Boolean

    suspend fun getContactAddressByGroupId(
        groupId: String,
        limit: Int,
        offset: Int
    ): Flow<List<ContactModel>>
    
    /**
     * Get contacts with their addresses by group ID with pagination by contact
     * @param groupId ID of the group
     * @param limit Maximum number of contacts to retrieve
     * @param offset Starting point for pagination
     * @return Flow of PaginatedContactsResult containing contacts with their addresses
     */
    suspend fun getContactsWithAddressesByGroupId(
        groupId: String,
        limit: Int,
        offset: Int
    ): Flow<PaginatedContactsResult>

    suspend fun getGroupsByIds(ids: List<String>): List<GroupEntity>
    fun observeGroups(): Flow<List<GroupEntity>>
    fun observeGroupById(id: String): Flow<GroupEntity?>

    /**
     * Clear all groups data for wallet reset
     * This will delete all groups and CASCADE will handle related data
     * @return true if clearing was successful
     */
    suspend fun clearAllGroups(): Boolean
}