package com.mangala.wallet.features.addressbook.domain.usecase.avatar

import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarRepository
import com.mangala.wallet.features.addressbook.utils.avatar.AvatarImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class AvatarUseCase(
    private val imageProcessor: AvatarImageProcessor,
    private val avatarRepository: AvatarRepository
) {
    sealed class Result {
        data class Success(val avatarSource: AvatarSource) : Result()
        data class Error(val message: String, val cause: Throwable? = null) : Result()
    }

    fun createEmojiAvatar(emoji: String): Result {
        if (emoji.isEmpty()) {
            return Result.Error("Emoji không hợp lệ")
        }
        return Result.Success(AvatarSource.Emoji(emoji))
    }

    suspend fun processAndSaveImage(
        entityId: String,
        imagePath: String
    ): Result = withContext(Dispatchers.IO) {
        try {
            val processedImage = try {
                imageProcessor.process(imagePath)
            } catch (e: Exception) {
                return@withContext Result.Error("Không thể xử lý ảnh: ${e.message}", e)
            }

            val localPath = try {
                avatarRepository.saveImage(entityId, processedImage)
            } catch (e: Exception) {
                return@withContext Result.Error("Không thể lưu ảnh: ${e.message}", e)
            }

            Result.Success(AvatarSource.ImageUrl(localPath))
        } catch (e: Exception) {
            Result.Error("Lỗi không xác định: ${e.message}", e)
        }
    }

    suspend fun deleteImage(filePath: String?): Boolean = withContext(Dispatchers.IO) {
        if (filePath == null || filePath.startsWith("emoji:")) return@withContext true
        return@withContext avatarRepository.deleteImage(filePath)
    }

    fun avatarSourceToString(avatarSource: AvatarSource): String? {
        return AvatarSource.toString(avatarSource)
    }

    fun createAvatarSource(iconString: String?): AvatarSource {
        return AvatarSource.fromString(iconString)
    }
}