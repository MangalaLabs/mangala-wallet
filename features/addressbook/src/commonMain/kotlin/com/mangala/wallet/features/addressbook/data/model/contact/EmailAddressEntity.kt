package com.mangala.wallet.features.addressbook.data.model.contact

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho địa chỉ email của một contact
 * Tương ứng với bảng 'email_addresses' trong database
 */
data class EmailAddressEntity(
    val id: String, // UUID
    val contactId: String,
    val email: String,
    val label: String?, // Ví dụ: "Personal", "Work"
    val isPrimary: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Kiểm tra định dạng email có hợp lệ không
     * @return true nếu email hợp lệ
     */
    fun isValid(): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }

    /**
     * Trả về địa chỉ email được ẩn một phần để bảo vệ quyền riêng tư
     * @return Địa chỉ email với username được ẩn một phần
     */
    fun getMaskedEmail(): String {
        val parts = email.split("@")
        if (parts.size != 2) return "****@****.com"

        val username = parts[0]
        val domain = parts[1]

        val maskedUsername = if (username.length > 2) {
            "${username.take(1)}${"*".repeat(username.length - 2)}${username.last()}"
        } else {
            "*".repeat(username.length)
        }

        return "$maskedUsername@$domain"
    }

    companion object {
        /**
         * Tạo một đối tượng EmailAddressEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            contactId: String,
            email: String,
            label: String? = null,
            isPrimary: Boolean = false
        ): EmailAddressEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return EmailAddressEntity(
                id = id,
                contactId = contactId,
                email = email,
                label = label,
                isPrimary = isPrimary,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}