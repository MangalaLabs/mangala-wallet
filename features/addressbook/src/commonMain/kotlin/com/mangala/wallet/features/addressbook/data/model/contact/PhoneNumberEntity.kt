package com.mangala.wallet.features.addressbook.data.model.contact

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho số điện thoại của một contact
 * Tương ứng với bảng 'phone_numbers' trong database
 */
data class PhoneNumberEntity(
    val id: String, // UUID
    val contactId: String,
    val phoneNumber: String,
    val label: String?, // Ví dụ: "Home", "Work", "Mobile"
    val isPrimary: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Trả về số điện thoại được format theo định dạng phù hợp
     * Có thể mở rộng để hỗ trợ nhiều định dạng quốc gia khác nhau
     */
    fun getFormattedNumber(): String {
        // Đơn giản hóa, có thể triển khai logic phức tạp hơn tùy theo yêu cầu
        return phoneNumber
    }

    /**
     * Trả về số điện thoại được ẩn một phần để bảo vệ quyền riêng tư
     * @return Số điện thoại với chỉ 4 số cuối được hiển thị
     */
    fun getMaskedNumber(): String {
        return if (phoneNumber.length > 4) {
            val visiblePart = phoneNumber.takeLast(4)
            "*".repeat(phoneNumber.length - 4) + visiblePart
        } else {
            "*".repeat(phoneNumber.length)
        }
    }

    companion object {
        /**
         * Tạo một đối tượng PhoneNumberEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            contactId: String,
            PhoneNumberEntity: String,
            label: String? = null,
            isPrimary: Boolean = false
        ): PhoneNumberEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return PhoneNumberEntity(
                id = id,
                contactId = contactId,
                phoneNumber = PhoneNumberEntity,
                label = label,
                isPrimary = isPrimary,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}