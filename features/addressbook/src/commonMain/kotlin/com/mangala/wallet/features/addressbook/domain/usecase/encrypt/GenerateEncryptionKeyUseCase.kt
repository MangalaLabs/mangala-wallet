package com.mangala.wallet.features.addressbook.domain.usecase.encrypt

import com.mangala.wallet.features.addressbook.data.model.encrypt.EncryptionKeyResult
import com.mangala.wallet.features.addressbook.data.model.enum.CryptoConstants
import com.mangala.wallet.features.addressbook.domain.repository.encrypt.EncryptionKeyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Use case để tạo khóa mã hóa mới.
 */
class GenerateEncryptionKeyUseCase(
    private val encryptionKeyRepository: EncryptionKeyRepository,
    private val defaultDispatcher: CoroutineDispatcher
) {
    /**
     * Tạo khóa mã hóa với alias mặc định.
     * @param userAuthRequired True nếu yêu cầu xác thực người dùng để sử dụng khóa
     * @return Flow<EncryptionKeyResult>
     */
    operator fun invoke(userAuthRequired: Boolean = false): Flow<EncryptionKeyResult> =
        encryptionKeyRepository.generateKey(
            keyAlias = CryptoConstants.DEFAULT_ENCRYPTION_KEY_ALIAS,
            userAuthRequired = userAuthRequired
        )
            .catch { e ->
                emit(EncryptionKeyResult.Error(Exception("Failed to generate encryption key", e)))
            }
            .flowOn(defaultDispatcher)

    /**
     * Tạo khóa mã hóa với alias tùy chỉnh.
     * @param keyAlias Tên định danh cho khóa
     * @param userAuthRequired True nếu yêu cầu xác thực người dùng để sử dụng khóa
     * @return Flow<EncryptionKeyResult>
     */
    operator fun invoke(keyAlias: String, userAuthRequired: Boolean = false): Flow<EncryptionKeyResult> =
        encryptionKeyRepository.generateKey(keyAlias, userAuthRequired)
            .catch { e ->
                emit(EncryptionKeyResult.Error(Exception("Failed to generate encryption key: $keyAlias", e)))
            }
            .flowOn(defaultDispatcher)
}