package com.mangala.wallet.features.addressbook.data.model.blockchain

import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Entity cho địa chỉ ví
 * Tương ứng với bảng 'wallet_addresses' trong database
 */
data class WalletAddressEntity(
    val id: String, // UUID
    val contactId: String,
    val blockchainTypeId: String,
    val address: String,
    val alias: String?, // Biệt danh cho địa chỉ ví
    val walletType: String?, // Ví dụ: "hot", "cold"
    val isSensitive: Boolean,
    val isPrimary: Boolean,
    val isVerified: Boolean,
    val verifiedDate: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Trả về địa chỉ được format theo cách dễ đọc
     * @param privacyMode Có đang bật chế độ riêng tư hay không
     * @return Địa chỉ được format
     */
    fun getFormattedAddress(privacyMode: Boolean): String {
        if (!privacyMode) return address

        // Trong chế độ riêng tư, chỉ hiện 6 ký tự đầu và 4 ký tự cuối
        return if (address.length > 10) {
            val prefix = address.take(6)
            val suffix = address.takeLast(4)
            "$prefix...$suffix"
        } else {
            "****${address.takeLast(4)}"
        }
    }

    /**
     * Trả về định dạng ngắn của địa chỉ để hiển thị trong UI
     * @return Địa chỉ rút gọn
     */
    fun getShortAddress(): String {
        return if (address.length > 10) {
            val prefix = address.take(6)
            val suffix = address.takeLast(4)
            "$prefix...$suffix"
        } else {
            address
        }
    }

    fun displayWallet(): String {
        val maskedAddress = if (isSensitive) {
            "********"
        } else {
            maskAddress(address)
        }

        return maskedAddress
    }

    private fun maskAddress(address: String): String {
        // Use standard contact formatting for compact view
        return com.mangala.wallet.features.addressbook.utils.ContactAddressFormatter.forCompactView(address)
    }

    /**
     * Kiểm tra địa chỉ có hợp lệ không
     * (Lưu ý: Ở đây chỉ kiểm tra cơ bản, việc kiểm tra đầy đủ nên được thực hiện
     * thông qua BlockchainType.validateAddress)
     * @return true nếu địa chỉ có định dạng cơ bản hợp lệ
     */
    fun validate(): Boolean {
        // Kiểm tra cơ bản: không rỗng và đủ độ dài tối thiểu
        return address.isNotBlank() && address.length >= 26
    }

    companion object {
        // Các loại ví
        const val TYPE_HOT = "hot"       // Hot wallet (online)
        const val TYPE_COLD = "cold"     // Cold wallet (offline storage)
        const val TYPE_HARDWARE = "hardware" // Hardware wallet
        const val TYPE_PAPER = "paper"   // Paper wallet
        const val TYPE_EXCHANGE = "exchange" // Exchange wallet

        /**
         * Lấy danh sách các loại ví phổ biến
         * @return Danh sách các loại ví
         */
        fun getWalletTypes(): List<String> {
            return listOf(TYPE_HOT, TYPE_COLD, TYPE_HARDWARE, TYPE_PAPER, TYPE_EXCHANGE)
        }

        /**
         * Tạo một đối tượng WalletAddressEntity mới
         */
        fun create(
            id: String, // UUID được tạo từ repository
            contactId: String,
            blockchainTypeId: String,
            address: String,
            alias: String? = null,
            walletType: String? = TYPE_HOT,
            isSensitive: Boolean = false,
            isPrimary: Boolean = false,
            isVerified: Boolean = false,
            verifiedDate: LocalDate? = null
        ): WalletAddressEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return WalletAddressEntity(
                id = id,
                contactId = contactId,
                blockchainTypeId = blockchainTypeId,
                address = address,
                alias = alias,
                walletType = walletType, // Sửa lỗi: sử dụng đúng tham số walletType thay vì alias
                isSensitive = isSensitive,
                isPrimary = isPrimary,
                isVerified = isVerified,
                verifiedDate = verifiedDate,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}