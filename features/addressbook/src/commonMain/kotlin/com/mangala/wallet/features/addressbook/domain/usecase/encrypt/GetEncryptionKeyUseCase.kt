package com.mangala.wallet.features.addressbook.domain.usecase.encrypt

import com.mangala.wallet.features.addressbook.data.model.encrypt.EncryptionKeyResult
import com.mangala.wallet.features.addressbook.data.model.enum.CryptoConstants
import com.mangala.wallet.features.addressbook.domain.repository.encrypt.EncryptionKeyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

/**
 * Use case để lấy khóa mã hóa.
 */
class GetEncryptionKeyUseCase(
    private val encryptionKeyRepository: EncryptionKeyRepository,
    private val defaultDispatcher: CoroutineDispatcher
) {
    /**
     * Lấy khóa mã hóa với alias mặc định.
     * @return Flow<EncryptionKeyResult>
     */
    operator fun invoke(): Flow<EncryptionKeyResult> =
        encryptionKeyRepository.getKey(CryptoConstants.DEFAULT_ENCRYPTION_KEY_ALIAS)
            .catch { e ->
                emit(EncryptionKeyResult.Error(Exception("Failed to retrieve encryption key", e)))
            }
            .flowOn(defaultDispatcher)

    /**
     * Lấy khóa mã hóa với alias tùy chỉnh.
     * @param keyAlias Tên định danh của khóa
     * @return Flow<EncryptionKeyResult>
     */
    operator fun invoke(keyAlias: String): Flow<EncryptionKeyResult> =
        encryptionKeyRepository.getKey(keyAlias)
            .catch { e ->
                emit(EncryptionKeyResult.Error(Exception("Failed to retrieve encryption key: $keyAlias", e)))
            }
            .flowOn(defaultDispatcher)
}