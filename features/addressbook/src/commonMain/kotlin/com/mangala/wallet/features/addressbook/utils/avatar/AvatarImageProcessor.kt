package com.mangala.wallet.features.addressbook.utils.avatar

/**
 * Interface định nghĩa các phương thức xử lý ảnh avatar.
 * Chịu trách nhiệm resize và nén ảnh trước khi upload.
 */
interface AvatarImageProcessor {
    /**
     * Xử lý ảnh từ đường dẫn (resize và nén)
     * @param path String đường dẫn đến ảnh cần xử lý
     * @return ByteArray chứa dữ liệu ảnh đã xử lý
     */
    suspend fun process(path: String): ByteArray

    /**
     * Xử lý ảnh từ ByteArray (resize và nén)
     * @param bytes ByteArray chứa dữ liệu ảnh cần xử lý
     * @param mimeType Định dạng ảnh, nếu biết (vd: "image/jpeg")
     * @return ByteArray chứa dữ liệu ảnh đã xử lý
     */
    suspend fun process(bytes: ByteArray, mimeType: String? = null): ByteArray

    /**
     * Lấy MIME type của ảnh sau khi xử lý
     * @return MIME type của ảnh (vd: "image/jpeg")
     */
    fun getProcessedMimeType(): String
}