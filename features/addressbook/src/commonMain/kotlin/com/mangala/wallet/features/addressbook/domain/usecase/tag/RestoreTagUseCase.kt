package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

/**
 * Use case to restore a previously deleted tag
 */
class RestoreTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(tagId: String, color: String): Result<TagEntity> {
        return try {
            // Get tag to ensure it exists and is deleted
            val tag = tagRepository.getTagById(tagId)
                ?: return Result.failure(IllegalArgumentException("Tag not found"))

            if (!tag.isDeleted) {
                return Result.failure(IllegalArgumentException("Tag is not deleted"))
            }

            val restoredTag = tagRepository.restoreTag(tagId, color)
            Result.success(restoredTag)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}