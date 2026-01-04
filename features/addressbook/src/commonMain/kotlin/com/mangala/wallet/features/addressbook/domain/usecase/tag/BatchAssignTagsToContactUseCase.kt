package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

/**
 * Use case to batch assign multiple tags to a contact
 */
class BatchAssignTagsToContactUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(contactId: String, tagIds: List<String>): Result<Unit> {
        return try {
            if (tagIds.isEmpty()) {
                return Result.success(Unit)
            }

            // Verify all tags exist and are not deleted
            for (tagId in tagIds) {
                val tag = tagRepository.getTagById(tagId)
                    ?: return Result.failure(IllegalArgumentException("Tag with ID $tagId not found"))

                if (tag.isDeleted) {
                    return Result.failure(IllegalArgumentException("Cannot assign deleted tag with ID $tagId"))
                }
            }

            tagRepository.batchAssignTagsToContact(contactId, tagIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}