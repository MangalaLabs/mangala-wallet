package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

class AssignTagToContactUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(contactId: String, tagId: String): Result<Unit> {
        return try {
            // Verify both contact and tag exist
            val tag = tagRepository.getTagById(tagId)
                ?: return Result.failure(IllegalArgumentException("Tag not found"))

            if (tag.isDeleted) {
                return Result.failure(IllegalArgumentException("Cannot assign a deleted tag"))
            }

            // Assign the tag
            tagRepository.assignTagToContact(tagId, contactId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}