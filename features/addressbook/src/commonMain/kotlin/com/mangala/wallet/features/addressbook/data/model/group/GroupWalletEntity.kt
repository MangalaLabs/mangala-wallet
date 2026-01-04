package com.mangala.wallet.features.addressbook.data.model.group

import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho liên kết giữa Group và Wallet
 * Tương ứng với bảng 'group_wallet' trong database
 */
data class GroupWalletEntity(
    val groupId: String,
    val walletAddressId: String,
    val contactId: String, // Chủ của ví
    val createdAt: Instant
) {
    companion object {
        /**
         * Tạo một đối tượng GroupWallet mới
         */
        fun create(
            groupId: String,
            walletAddressId: String,
            contactId: String
        ): GroupWalletEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return GroupWalletEntity(
                groupId = groupId,
                walletAddressId = walletAddressId,
                contactId = contactId,
                createdAt = now
            )
        }
    }
}