package com.mangala.wallet.features.addressbook.data.model.contact

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho danh sách yêu thích
 * Tương ứng với bảng 'FavoriteEntitys' trong database
 */
data class FavoriteEntity(
    val id: String, // UUID
    val contactId: String,
    val displayOrder: Int, // Thứ tự hiển thị
    val createdAt: Instant,
    val updatedAt: Instant?
) {
    companion object {
        /**
         * Tạo một đối tượng FavoriteEntity mới
         * @param id UUID mới
         * @param contactId ID của contact
         * @param displayOrder Thứ tự hiển thị (số càng nhỏ thì hiển thị càng trước)
         * @return Đối tượng FavoriteEntity mới
         */
        fun create(
            id: String = uuid4().toString(),
            contactId: String,
            displayOrder: Int
        ): FavoriteEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return FavoriteEntity(
                id = id,
                contactId = contactId,
                displayOrder = displayOrder,
                createdAt = now,
                updatedAt = null
            )
        }
    }
}
