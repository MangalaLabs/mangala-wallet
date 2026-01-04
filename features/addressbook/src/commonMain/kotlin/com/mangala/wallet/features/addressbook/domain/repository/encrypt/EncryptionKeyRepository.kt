package com.mangala.wallet.features.addressbook.domain.repository.encrypt

import com.mangala.wallet.features.addressbook.data.model.encrypt.EncryptionKeyResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface để quản lý khóa mã hóa.
 * Triển khai cụ thể sẽ khác nhau giữa các nền tảng.
 */
interface EncryptionKeyRepository {
    /**
     * Tạo khóa mã hóa mới.
     * @param keyAlias Tên định danh cho khóa
     * @param userAuthRequired Có yêu cầu xác thực người dùng để dùng khóa
     * @return Flow<EncryptionKeyResult> Kết quả dạng Flow
     */
    fun generateKey(keyAlias: String, userAuthRequired: Boolean = false): Flow<EncryptionKeyResult>

    /**
     * Lấy khóa mã hóa đã tồn tại.
     * @param keyAlias Tên định danh của khóa cần lấy
     * @return Flow<EncryptionKeyResult> Kết quả dạng Flow
     */
    fun getKey(keyAlias: String): Flow<EncryptionKeyResult>

    /**
     * Kiểm tra khóa đã tồn tại chưa.
     * @param keyAlias Tên định danh của khóa
     * @return Flow<Boolean> true nếu khóa tồn tại
     */
    fun hasKey(keyAlias: String): Flow<Boolean>

    /**
     * Xóa khóa.
     * @param keyAlias Tên định danh của khóa cần xóa
     * @return Flow<Boolean> true nếu xóa thành công
     */
    fun deleteKey(keyAlias: String): Flow<Boolean>
}