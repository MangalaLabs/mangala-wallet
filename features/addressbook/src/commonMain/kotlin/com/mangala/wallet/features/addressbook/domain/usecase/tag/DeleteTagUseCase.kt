package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

/**
 * Use case to delete a tag (soft delete)
 * With soft delete, we keep all contact-tag relationships
 */
class DeleteTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(tagId: String): Result<List<String>> {
        return try {
            // Get tag to ensure it exists
            val tag = tagRepository.getTagById(tagId)
                ?: return Result.failure(IllegalArgumentException("Tag not found"))

            // Perform soft delete and get affected contacts
            val affectedContactIds = tagRepository.softDeleteTag(tagId)

            Result.success(affectedContactIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Hard delete a tag permanently
     * @param tagId ID of the tag to delete
     * @return true if deletion was successful
     */
    suspend fun hardDeleteTag(tagId: String): Boolean {
        return tagRepository.hardDeleteTag(tagId)
    }
}