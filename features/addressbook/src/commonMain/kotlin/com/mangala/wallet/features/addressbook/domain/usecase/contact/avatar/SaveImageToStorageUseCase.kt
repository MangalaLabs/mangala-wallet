package com.mangala.wallet.features.addressbook.domain.usecase.contact.avatar

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.domain.repository.FileStorageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * UseCase để lưu ảnh vào bộ nhớ ứng dụng và trả về đường dẫn
 */
class SaveImageToStorageUseCase(
    private val fileStorageRepository: FileStorageRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(imageBytes: ByteArray, contactId: String): Result<String> = withContext(dispatcher) {
        try {
            // Tạo tên file duy nhất dựa trên contactId
            val fileName = "avatar_${contactId}_${uuid4()}.png"
            
            // Lưu file vào bộ nhớ và nhận đường dẫn
            val path = fileStorageRepository.saveImage(imageBytes, fileName)
            
            Result.success(path)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
