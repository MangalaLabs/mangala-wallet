package com.mangala.wallet.features.addressbook.data.model.transaction

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho liên kết giữa Contact và Transaction
 * Tương ứng với bảng 'contact_transactions' trong database
 */
data class ContactTransactionEntity(
    val id: String, // UUID
    val contactId: String,
    val transactionId: String,
    val walletAddressId: String,
    val isSender: Boolean, // true nếu contact là người gửi, false nếu là người nhận
    val createdAt: Instant
) {
    companion object {
        /**
         * Tạo một đối tượng ContactTransactionEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            contactId: String,
            transactionId: String,
            walletAddressId: String,
            isSender: Boolean
        ): ContactTransactionEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return ContactTransactionEntity(
                id = id,
                contactId = contactId,
                transactionId = transactionId,
                walletAddressId = walletAddressId,
                isSender = isSender,
                createdAt = now
            )
        }
    }
}