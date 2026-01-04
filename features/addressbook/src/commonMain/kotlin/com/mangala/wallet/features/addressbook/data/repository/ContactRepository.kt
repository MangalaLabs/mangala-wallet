package com.mangala.wallet.features.addressbook.data.repository

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository để quản lý dữ liệu của Contact
 */
interface ContactRepository {
    /**
     * Tạo mới một contact
     * @param contact Contact cần tạo
     * @return ID của contact đã tạo
     */
    suspend fun createContact(contact: ContactEntity): String
    
    /**
     * Cập nhật đường dẫn avatar cho contact
     * @param contactId ID của contact
     * @param avatarPath Đường dẫn đến file avatar
     */
    suspend fun updateContactAvatar(contactId: String, avatarPath: String)
    
    /**
     * Lấy đường dẫn avatar của contact
     * @param contactId ID của contact
     * @return Đường dẫn avatar hoặc null nếu không có
     */
    suspend fun getContactAvatar(contactId: String): String?
    
    /**
     * Lấy thông tin một contact
     * @param contactId ID của contact
     * @return Thông tin contact
     */
    suspend fun getContact(contactId: String): ContactEntity?
    
    /**
     * Lấy danh sách tất cả contact
     * @return Flow chứa danh sách contact
     */
    fun getAllContacts(): Flow<List<ContactEntity>>
    
    /**
     * Lấy danh sách contact yêu thích
     * @return Flow chứa danh sách contact yêu thích
     */
    fun getFavoriteContacts(): Flow<List<ContactEntity>>
    
    /**
     * Cập nhật toàn bộ thông tin contact
     * @param contact Thông tin contact mới
     */
    suspend fun updateContact(contact: ContactEntity)
    
    /**
     * Xóa một contact
     * @param contactId ID của contact cần xóa
     */
    suspend fun deleteContact(contactId: String)
}
