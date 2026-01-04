package com.mangala.wallet.features.addressbook.data.model.tag

import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho liên kết giữa Address và Tag
 * Tương ứng với bảng 'address_tags' trong database
 */
data class AddressTagEntity(
    val addressId: String,
    val tagId: String,
    val createdAt: Instant
) {
    companion object {
        /**
         * Tạo một đối tượng AddressTagEntity mới
         */
        fun create(
            addressId: String,
            tagId: String
        ): AddressTagEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return AddressTagEntity(
                addressId = addressId,
                tagId = tagId,
                createdAt = now
            )
        }
    }
}