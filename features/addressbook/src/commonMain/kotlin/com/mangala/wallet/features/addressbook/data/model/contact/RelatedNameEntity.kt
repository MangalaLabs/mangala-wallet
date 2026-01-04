package com.mangala.wallet.features.addressbook.data.model.contact

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho tên người liên quan của một contact
 * Tương ứng với bảng 'related_names' trong database
 */
data class RelatedNameEntity(
    val id: String, // UUID
    val contactId: String,
    val name: String,
    val relationship: String, // Ví dụ: "Father", "Mother", "Spouse"
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        // Các mối quan hệ phổ biến
        const val RELATIONSHIP_FATHER = "Father"
        const val RELATIONSHIP_MOTHER = "Mother"
        const val RELATIONSHIP_SPOUSE = "Spouse"
        const val RELATIONSHIP_CHILD = "Child"
        const val RELATIONSHIP_SIBLING = "Sibling"
        const val RELATIONSHIP_FRIEND = "Friend"
        const val RELATIONSHIP_COLLEAGUE = "Colleague"

        /**
         * Lấy danh sách các mối quan hệ phổ biến
         * @return Danh sách các mối quan hệ
         */
        fun getCommonRelationships(): List<String> {
            return listOf(
                RELATIONSHIP_FATHER,
                RELATIONSHIP_MOTHER,
                RELATIONSHIP_SPOUSE,
                RELATIONSHIP_CHILD,
                RELATIONSHIP_SIBLING,
                RELATIONSHIP_FRIEND,
                RELATIONSHIP_COLLEAGUE
            )
        }

        /**
         * Tạo một đối tượng RelatedNameEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            contactId: String,
            name: String,
            relationship: String
        ): RelatedNameEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return RelatedNameEntity(
                id = id,
                contactId = contactId,
                name = name,
                relationship = relationship,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}