package com.mangala.wallet.features.addressbook.data.model.tag

import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho liên kết giữa Contact và Tag
 * Tương ứng với bảng 'contact_tags' trong database
 */
data class ContactTagEntity(
    val contactId: String,
    val tagId: String,
    val createdAt: Instant
) {
    companion object {
        /**
         * Tạo một đối tượng ContactTagEntity mới
         */
        fun create(
            contactId: String,
            tagId: String
        ): ContactTagEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return ContactTagEntity(
                contactId = contactId,
                tagId = tagId,
                createdAt = now
            )
        }
    }
}