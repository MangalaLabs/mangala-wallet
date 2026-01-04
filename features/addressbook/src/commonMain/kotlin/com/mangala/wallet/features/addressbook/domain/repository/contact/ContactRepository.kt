package com.mangala.wallet.features.addressbook.domain.repository.contact

import app.cash.paging.PagingData
import com.mangala.wallet.features.addressbook.data.model.ContactDetailModel
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactRecentTransactionModel
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ImportantDateEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.data.model.contact.SocialProfileEntity
import com.mangala.wallet.features.addressbook.domain.model.ContactChangeEvent
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface cho việc quản lý Contact và các thông tin liên quan
 */
interface ContactRepository {
    /**
     * Flow that emits events when contact data changes
     */
    val contactChanges: Flow<ContactChangeEvent>
    
    /**
     * Lấy một contact theo ID
     * @param id ID của contact cần lấy
     * @return Contact hoặc null nếu không tìm thấy
     */
    suspend fun getContactById(id: String): ContactEntity?

    /**
     * Lấy thông tin chi tiết đầy đủ của một contact
     * @param id ID của contact cần lấy
     * @return ContactDetail hoặc null nếu không tìm thấy
     */
    suspend fun getContactDetailById(id: String): ContactDetailModel?

    /**
     * Lấy danh sách tất cả contacts, hỗ trợ phân trang
     * @param limit Số lượng records tối đa
     * @param offset Vị trí bắt đầu
     * @param sortOrder Thứ tự sắp xếp (A_Z, Z_A, NEWEST, OLDEST, BLOCKCHAIN)
     * @return Flow danh sách contacts
     */
    fun getAllContacts(
        limit: Int = 50,
        offset: Int = 0,
        sortOrder: String = "A_Z",
    ): Flow<List<ContactEntity>>

    /**
     * Tìm kiếm contacts theo tên
     * @param query Chuỗi tìm kiếm
     * @param limit Số lượng records tối đa
     * @param offset Vị trí bắt đầu
     * @return Flow danh sách contacts thỏa mãn
     */
    fun searchContacts(query: String, limit: Int = 50, offset: Int = 0): Flow<List<ContactEntity>>

    /**
     * Lưu một contact mới
     * @param contact Contact cần lưu
     * @return ID của contact sau khi lưu
     */
    suspend fun insertContact(contact: ContactEntity): String

    /**
     * Cập nhật một contact hiện có
     * @param contact Contact cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateContact(contact: ContactEntity): Boolean

    /**
     * Xóa một contact
     * @param id ID của contact cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteContact(id: String): Boolean

    /**
     * Cập nhật thời gian xem gần nhất của contact
     * @param id ID của contact
     * @return true nếu cập nhật thành công
     */
    suspend fun updateLastViewedAt(id: String): Boolean

    /**
     * Lấy danh sách các số điện thoại của một contact
     * @param contactId ID của contact
     * @return Danh sách các số điện thoại
     */
    suspend fun getPhoneNumbersByContactId(contactId: String): List<PhoneNumberEntity>

    /**
     * Lưu một số điện thoại mới
     * @param phoneNumber Số điện thoại cần lưu
     * @return ID của số điện thoại sau khi lưu
     */
    suspend fun insertPhoneNumber(phoneNumber: PhoneNumberEntity): String

    /**
     * Cập nhật số điện thoại
     * @param phoneNumber Số điện thoại cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updatePhoneNumber(phoneNumber: PhoneNumberEntity): Boolean

    /**
     * Xóa số điện thoại
     * @param id ID của số điện thoại cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deletePhoneNumber(id: String): Boolean

    /**
     * Lấy danh sách các địa chỉ email của một contact
     * @param contactId ID của contact
     * @return Danh sách các địa chỉ email
     */
    suspend fun getEmailAddressesByContactId(contactId: String): List<EmailAddressEntity>

    /**
     * Lưu một địa chỉ email mới
     * @param emailAddress Địa chỉ email cần lưu
     * @return ID của địa chỉ email sau khi lưu
     */
    suspend fun insertEmailAddress(emailAddress: EmailAddressEntity): String

    /**
     * Cập nhật địa chỉ email
     * @param emailAddress Địa chỉ email cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateEmailAddress(emailAddress: EmailAddressEntity): Boolean

    /**
     * Xóa địa chỉ email
     * @param id ID của địa chỉ email cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteEmailAddress(id: String): Boolean

    /**
     * Lấy danh sách các địa chỉ vật lý của một contact
     * @param contactId ID của contact
     * @return Danh sách các địa chỉ vật lý
     */
    suspend fun getPhysicalAddressesByContactId(contactId: String): List<PhysicalAddressEntity>

    /**
     * Lưu một địa chỉ vật lý mới
     * @param physicalAddress Địa chỉ vật lý cần lưu
     * @return ID của địa chỉ vật lý sau khi lưu
     */
    suspend fun insertPhysicalAddress(physicalAddress: PhysicalAddressEntity): String

    /**
     * Cập nhật địa chỉ vật lý
     * @param physicalAddress Địa chỉ vật lý cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updatePhysicalAddress(physicalAddress: PhysicalAddressEntity): Boolean

    /**
     * Xóa địa chỉ vật lý
     * @param id ID của địa chỉ vật lý cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deletePhysicalAddress(id: String): Boolean

    /**
     * Lấy danh sách các tên người liên quan của một contact
     * @param contactId ID của contact
     * @return Danh sách các tên người liên quan
     */
    suspend fun getRelatedNamesByContactId(contactId: String): List<RelatedNameEntity>

    /**
     * Lưu một tên người liên quan mới
     * @param relatedName Tên người liên quan cần lưu
     * @return ID của tên người liên quan sau khi lưu
     */
    suspend fun insertRelatedName(relatedName: RelatedNameEntity): String

    /**
     * Cập nhật tên người liên quan
     * @param relatedName Tên người liên quan cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateRelatedName(relatedName: RelatedNameEntity): Boolean

    /**
     * Xóa tên người liên quan
     * @param id ID của tên người liên quan cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteRelatedName(id: String): Boolean

    /**
     * Lấy danh sách các ngày quan trọng của một contact
     * @param contactId ID của contact
     * @return Danh sách các ngày quan trọng
     */
    suspend fun getImportantDatesByContactId(contactId: String): List<ImportantDateEntity>

    /**
     * Lưu một ngày quan trọng mới
     * @param importantDate Ngày quan trọng cần lưu
     * @return ID của ngày quan trọng sau khi lưu
     */
    suspend fun insertImportantDate(importantDate: ImportantDateEntity): String

    /**
     * Cập nhật ngày quan trọng
     * @param importantDate Ngày quan trọng cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateImportantDate(importantDate: ImportantDateEntity): Boolean

    /**
     * Xóa ngày quan trọng
     * @param id ID của ngày quan trọng cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteImportantDate(id: String): Boolean

    /**
     * Kiểm tra một contact có phải là favorite không
     * @param contactId ID của contact cần kiểm tra
     * @return true nếu contact là favorite
     */
    suspend fun isContactFavorite(contactId: String): Boolean

    /**
     * Thêm một contact vào danh sách yêu thích
     * @param contactId ID của contact cần thêm
     * @param displayOrder Thứ tự hiển thị
     * @return true nếu thêm thành công
     */
    suspend fun addFavorite(contactId: String): Boolean

    /**
     * Xóa một contact khỏi danh sách yêu thích
     * @param contactId ID của contact cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun removeFavorite(contactId: String): Boolean

    /**
     * Lấy danh sách contacts đã xem gần đây
     * @param limit Số lượng records tối đa
     * @return Danh sách contacts
     */
//    suspend fun getRecentContacts(limit: Int = 10): List<ContactEntity>

    /**
     * Đếm tổng số contacts
     * @return Số lượng contacts
     */
    suspend fun countAllContacts(): Int

    /**
     * Tìm contact bằng địa chỉ email
     * @param email Địa chỉ email cần tìm
     * @return Contact hoặc null nếu không tìm thấy
     */
    suspend fun findContactByEmail(email: String): ContactEntity?

    /**
     * Tìm contact bằng số điện thoại
     * @param phoneNumber Số điện thoại cần tìm
     * @return Contact hoặc null nếu không tìm thấy
     */
    suspend fun findContactByPhoneNumber(phoneNumber: String): ContactEntity?
    
    /**
     * Tìm contact bằng tên chính xác
     * @param name Tên contact cần tìm
     * @return Contact hoặc null nếu không tìm thấy
     */
    suspend fun findContactByName(name: String): ContactEntity?

    suspend fun insertEmailAddressesBatch(emailAddresses: List<EmailAddressEntity>): Map<EmailAddressEntity, String>

    suspend fun insertPhoneNumbersBatch(phoneNumbers: List<PhoneNumberEntity>): Map<PhoneNumberEntity, String>

    suspend fun insertPhysicalAddressesBatch(physicalAddresses: List<PhysicalAddressEntity>): Map<PhysicalAddressEntity, String>

    suspend fun insertRelatedNamesBatch(relatedNames: List<RelatedNameEntity>): Map<RelatedNameEntity, String>

    suspend fun insertImportantDatesBatch(importantDates: List<ImportantDateEntity>): Map<ImportantDateEntity, String>

    suspend fun insertSocialProfilesBatch(socialProfiles: List<SocialProfileEntity>): Map<SocialProfileEntity, String>

    /**
     * Xóa tất cả social profiles của một contact
     * @param contactId ID của contact
     * @return true nếu xóa thành công
     */
    suspend fun deleteSocialProfilesByContactId(contactId: String): Boolean

    suspend fun filterContacts(
        query: String,
        tagIds: List<String>,
        groupIds: List<String>,
        blockchainIds: List<String>,
        onlyFavorites: Boolean,
        sortOrder: String,
        limit: Int,
        offset: Int,
    ): List<ContactModel>

    /**
     * Quan sát danh sách liên hệ đã lọc với các điều kiện cụ thể
     * Khi có thay đổi, tự động phát ra kết quả mới
     * @return Flow emit kết quả mỗi khi dữ liệu thay đổi
     */
    fun observeFilteredContacts(
        query: String,
        tagIds: List<String>,
        groupIds: List<String>,
        blockchainIds: List<String>,
        onlyFavorites: Boolean,
        sortOrder: String,
        limit: Int,
        offset: Int,
    ): Flow<Result<List<ContactModel>>>


    suspend fun getContactByGroupIdIn(
        groupIds: List<String>,
        limit: Int,
        offset: Int,
    ): List<ContactEntity>

    fun getPaginatedContactRecentTransactions(
        searchQuery: String? = null,
        statuses: List<String>,
    ): Flow<PagingData<ContactRecentTransactionModel>>

    fun getPaginatedContacts(
        searchQuery: String? = null,
        tagIds: List<String>? = null,
        checkTagId: String? = null,
        isFavoriteOnly: Boolean,
    ): Flow<PagingData<ContactModel>>

    suspend fun getFavoriteContactsFlow(limit: Int, offset: Int): Flow<List<ContactModel>>

    suspend fun searchContacts(query: String): List<ContactEntity>

    /**
     * Observe all contacts and their changes
     * @return Flow emitting contact list whenever there's a change
     */
    fun observeContacts(): Flow<List<ContactEntity>>

    /**
     * Observe a specific contact by ID
     * @return Flow emitting contact or null whenever there's a change
     */
    fun observeContactById(id: String): Flow<ContactEntity?>

    /**
     * Notify observers that contacts data has changed, useful when repository doesn't naturally emit updates
     * @return True if notification was successful
     */
    suspend fun notifyContactsChanged(): Boolean

    /**
     * Clear all contacts data for wallet reset
     * This will delete all contacts and CASCADE will handle related data
     * @return true if clearing was successful
     */
    suspend fun clearAllContacts(): Boolean
}