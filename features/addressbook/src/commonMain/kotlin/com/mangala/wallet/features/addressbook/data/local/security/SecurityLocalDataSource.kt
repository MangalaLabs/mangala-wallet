package com.mangala.wallet.features.addressbook.data.local.security

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.security.SecurityAuditLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface định nghĩa các phương thức truy cập dữ liệu liên quan đến bảo mật từ local database
 */
interface SecurityLocalDataSource {
    /**
     * Lấy một audit log theo ID
     * @param id ID của audit log cần lấy
     * @return SecurityAuditLogEntity hoặc null nếu không tìm thấy
     */
    suspend fun getAuditLogById(id: String): SecurityAuditLogEntity?

    /**
     * Lấy danh sách audit logs của một contact
     * @param contactId ID của contact
     * @param limit Số lượng records tối đa
     * @param offset Vị trí bắt đầu
     * @return Flow danh sách audit logs
     */
    fun getAuditLogsByContactId(contactId: String, limit: Int = 50, offset: Int = 0): Flow<List<SecurityAuditLogEntity>>

    /**
     * Lấy danh sách tất cả audit logs
     * @param limit Số lượng records tối đa
     * @param offset Vị trí bắt đầu
     * @return Flow danh sách audit logs
     */
    fun getAllAuditLogs(limit: Int = 100, offset: Int = 0): Flow<List<SecurityAuditLogEntity>>

    /**
     * Lấy danh sách audit logs theo loại hành động
     * @param action Loại hành động
     * @param limit Số lượng records tối đa
     * @param offset Vị trí bắt đầu
     * @return Flow danh sách audit logs
     */
    fun getAuditLogsByAction(action: String, limit: Int = 50, offset: Int = 0): Flow<List<SecurityAuditLogEntity>>

    /**
     * Lưu một audit log mới
     * @param securityAuditLog SecurityAuditLogEntity cần lưu
     * @return ID của audit log sau khi lưu
     */
    suspend fun insertAuditLog(securityAuditLog: SecurityAuditLogEntity): String

    /**
     * Xóa một audit log
     * @param id ID của audit log cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteAuditLog(id: String): Boolean

    /**
     * Xóa tất cả audit logs cũ hơn một thời điểm nhất định
     * @param timestamp Thời điểm cuối (Unix timestamp)
     * @return Số lượng records đã xóa
     */
    suspend fun purgeOldAuditLogs(timestamp: Long): Int

    /**
     * Ghi log hành động tạo mới
     * @param contactId ID của contact liên quan
     * @param details Chi tiết hành động
     * @return ID của audit log sau khi lưu
     */
    suspend fun logCreate(contactId: String, details: String? = null): String

    /**
     * Ghi log hành động xem
     * @param contactId ID của contact liên quan
     * @param details Chi tiết hành động
     * @return ID của audit log sau khi lưu
     */
    suspend fun logView(contactId: String, details: String? = null): String

    /**
     * Ghi log hành động chỉnh sửa
     * @param contactId ID của contact liên quan
     * @param details Chi tiết hành động
     * @return ID của audit log sau khi lưu
     */
    suspend fun logEdit(contactId: String, details: String? = null): String

    /**
     * Ghi log hành động xóa
     * @param contactId ID của contact liên quan
     * @param details Chi tiết hành động
     * @return ID của audit log sau khi lưu
     */
    suspend fun logDelete(contactId: String, details: String? = null): String

    /**
     * Ghi log hành động xuất
     * @param contactId ID của contact liên quan (có thể null nếu xuất tất cả)
     * @param details Chi tiết hành động
     * @return ID của audit log sau khi lưu
     */
    suspend fun logExport(contactId: String? = null, details: String? = null): String

    /**
     * Ghi log hành động xem thông tin nhạy cảm
     * @param contactId ID của contact liên quan
     * @param details Chi tiết hành động
     * @return ID của audit log sau khi lưu
     */
    suspend fun logViewSensitive(contactId: String, details: String? = null): String

    /**
     * Ghi log hành động thay đổi chế độ riêng tư
     * @param enabled Trạng thái mới (true = bật, false = tắt)
     * @param details Chi tiết hành động
     * @return ID của audit log sau khi lưu
     */
    suspend fun logPrivacyChange(enabled: Boolean, details: String? = null): String

    /**
     * Ghi log hành động xác thực thất bại
     * @param contactId ID của contact liên quan (có thể null)
     * @param details Chi tiết hành động
     * @return ID của audit log sau khi lưu
     */
    suspend fun logAuthFailure(contactId: String? = null, details: String? = null): String

    /**
     * Lấy danh sách contacts nhạy cảm
     * @return Danh sách contacts có đánh dấu là nhạy cảm
     */
    suspend fun getSensitiveContacts(): List<ContactEntity>

    /**
     * Mã hóa dữ liệu
     * @param data Dữ liệu cần mã hóa
     * @return Dữ liệu đã mã hóa
     */
    suspend fun encryptData(data: String): String

    /**
     * Giải mã dữ liệu
     * @param encryptedData Dữ liệu đã mã hóa
     * @return Dữ liệu đã giải mã
     */
    suspend fun decryptData(encryptedData: String): String
}