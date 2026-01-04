package com.mangala.wallet.features.addressbook.data.model.encrypt

import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis


/**
 * Class đại diện cho khóa mã hóa theo một cách cross-platform.
 * Khóa được lưu trữ dưới dạng mảng byte tương thích với tất cả các nền tảng.
 */
class EncryptionKey(
    val keyBytes: ByteArray,
    val algorithm: String = "AES",
    val creationTimestamp: Long = localDateTimeToMillis(localDateTimeNow())
) {
    // Hỗ trợ kiểm tra tính hợp lệ của khóa
    fun isValid(): Boolean {
        // AES-256 cần khóa 32 byte
        return algorithm == "AES" && keyBytes.size == 32
    }

    // Hỗ trợ caching - Khóa chỉ có hiệu lực trong khoảng thời gian nhất định
    fun isExpired(cacheTimeMs: Long): Boolean {
        val currentTime = localDateTimeToMillis(localDateTimeNow())
        return (currentTime - creationTimestamp) > cacheTimeMs
    }

    // Đảm bảo ByteArray được so sánh đúng
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EncryptionKey

        if (algorithm != other.algorithm) return false
        if (!keyBytes.contentEquals(other.keyBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = algorithm.hashCode()
        result = 31 * result + keyBytes.contentHashCode()
        return result
    }

    // Không in ra khóa trong toString để tránh rò rỉ thông tin
    override fun toString(): String {
        return "EncryptionKey(algorithm=$algorithm, keyLength=${keyBytes.size}, createdAt=$creationTimestamp)"
    }
}