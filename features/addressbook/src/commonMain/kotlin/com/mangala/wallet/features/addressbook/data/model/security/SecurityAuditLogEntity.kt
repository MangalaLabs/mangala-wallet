package com.mangala.wallet.features.addressbook.data.model.security

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.format
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Entity cho log kiểm tra bảo mật
 * Tương ứng với bảng 'security_audit_logs' trong database
 */
data class SecurityAuditLogEntity(
    val id: String, // UUID
    val contactId: String?,
    val walletAddressId: String?,
    val action: String, // CREATE, VIEW, EDIT, DELETE, EXPORT, VIEW_SENSITIVE, PRIVACY_CHANGE, AUTH_FAILURE
    val details: String?,
    val deviceInfo: String?,
    val timestamp: Instant
) {
    /**
     * Trả về chuỗi thời gian được định dạng
     * @return Chuỗi thời gian dạng "yyyy-MM-dd HH:mm:ss"
     */
    fun getFormattedTimestamp(): String {
        val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
        return localDateTime.format(format = "yyyy-MM-dd HH:mm:ss")
    }

    /**
     * Kiểm tra xem log có phải là hành động nhạy cảm không
     * @return true nếu hành động là nhạy cảm
     */
    fun isSensitiveAction(): Boolean {
        return action in listOf(
            ACTION_VIEW_SENSITIVE,
            ACTION_EXPORT,
            ACTION_AUTH_FAILURE,
            ACTION_PRIVACY_CHANGE
        )
    }

    companion object {
        // Các loại hành động
        const val ACTION_CREATE = "CREATE"
        const val ACTION_VIEW = "VIEW"
        const val ACTION_EDIT = "EDIT"
        const val ACTION_DELETE = "DELETE"
        const val ACTION_EXPORT = "EXPORT"
        const val ACTION_VIEW_SENSITIVE = "VIEW_SENSITIVE"
        const val ACTION_PRIVACY_CHANGE = "PRIVACY_CHANGE"
        const val ACTION_AUTH_FAILURE = "AUTH_FAILURE"

        /**
         * Tạo một đối tượng SecurityAuditLogEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            action: String,
            contactId: String? = null,
            walletAddressId: String? = null,
            details: String? = null,
            deviceInfo: String? = null
        ): SecurityAuditLogEntity {
            return SecurityAuditLogEntity(
                id = id,
                contactId = contactId,
                walletAddressId = walletAddressId,
                action = action,
                details = details,
                deviceInfo = deviceInfo,
                timestamp = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            )
        }
    }
}