package com.mangala.wallet.features.addressbook.data.local.contact

import app.cash.paging.PagingSource
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ImportantDateEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.data.model.contact.SocialProfileEntity
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.database.SelectRecentContactsWithSearch
import kotlinx.coroutines.flow.Flow

/**
 * Interface định nghĩa các phương thức truy cập dữ liệu liên quan đến Contact từ local database
 */
interface ContactLocalDataSource {
    /**
     * Lấy một contact theo ID
     * @param id ID của contact cần lấy
     * @return ContactEntity hoặc null nếu không tìm thấy
     */
    suspend fun getContactById(id: String): ContactEntity?

    /**
     * Lấy danh sách tất cả contacts, hỗ trợ phân trang và sắp xếp
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


    suspend fun markAddressAsPrimary(
        contactId: String,
        addressId: String,
    ): Boolean

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

    suspend fun insertRelatedNamesBatch(relatedNames: List<RelatedNameEntity>): Map<RelatedNameEntity, String>

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
     * Lấy danh sách social profiles của contact
     * @param contactId ID của contact
     * @return Danh sách social profiles
     */
    suspend fun getSocialProfilesByContactId(contactId: String): List<SocialProfileEntity>

    /**
     * Lưu social profile mới
     * @param socialProfile Social profile cần lưu
     * @return ID của social profile sau khi lưu
     */
    suspend fun insertSocialProfile(socialProfile: SocialProfileEntity): String

    /**
     * Cập nhật social profile
     * @param socialProfile Social profile cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateSocialProfile(socialProfile: SocialProfileEntity): Boolean

    /**
     * Xóa social profile
     * @param id ID của social profile cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteSocialProfile(id: String): Boolean

    /**
     * Xóa tất cả social profiles của một contact
     * @param contactId ID của contact
     * @return true nếu xóa thành công
     */
    suspend fun deleteSocialProfilesByContactId(contactId: String): Boolean

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
    suspend fun getRecentContacts(limit: Int = 10): List<ContactEntity>

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

    suspend fun insertSocialProfilesBatch(socialProfiles: List<SocialProfileEntity>): Map<SocialProfileEntity, String>

    suspend fun insertImportantDatesBatch(importantDates: List<ImportantDateEntity>): Map<ImportantDateEntity, String>

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

    suspend fun getContactsByGroupIdIn(groupIds: List<String>): List<ContactEntity>

    fun getContactRecentTransactionPagingSource(
        searchQuery: String?,
        statuses: List<String>,
    ): PagingSource<Int, SelectRecentContactsWithSearch>

    fun getContactsPagingSource(
        searchQuery: String?,
        tagIds: List<String>?,
        checkTagId: String?,
        isFavoriteOnly: Boolean
    ): PagingSource<Int, ContactModel>

    suspend fun getFavoriteContactsFlow(
        limit: Int,
        offset: Int,
    ): Flow<List<ContactModel>>

    /**
     * Quan sát một contact theo ID, cập nhật theo Flow
     * @param id ID của contact cần quan sát
     * @return Flow Contact hoặc null nếu không tìm thấy
     */
    fun observeContactById(id: String): Flow<ContactEntity?>

    fun getTagsByContactId(contactId: String): List<TagEntity>

    /**
     * Clear all contacts data for wallet reset
     * This will delete all contacts and CASCADE will handle related data
     * @return true if clearing was successful
     */
    suspend fun clearAllContacts(): Boolean
}