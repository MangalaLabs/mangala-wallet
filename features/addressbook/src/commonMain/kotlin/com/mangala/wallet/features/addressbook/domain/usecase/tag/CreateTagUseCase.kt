package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

class CreateTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(name: String, color: String, textColor: String? = null, icon: String? = null): Result<TagEntity> {
        return try {
            if (name.isBlank()) {
                return Result.failure(IllegalArgumentException("Tag name cannot be empty"))
            }
            if (name.length > 20) {
                return Result.failure(IllegalArgumentException("Tag name cannot exceed 20 characters"))
            }

            // Check if tag with same name already exists
            val existingTag = tagRepository.findTagByName(name)
            if (existingTag != null && !existingTag.isDeleted) {
                return Result.failure(IllegalArgumentException("Tag with this name already exists"))
            }

            // If a soft-deleted tag exists with this name, restore it
            if (existingTag != null && existingTag.isDeleted) {
                val restoredTag = tagRepository.restoreTag(existingTag.id, color, textColor)
                return Result.success(restoredTag)
            }

            // Create a new tag
            val tag = TagEntity.create(
                id = uuid4().toString(),
                name = name,
                color = color,
                textColor = textColor,
                icon = icon,
                isDeleted = false
            )

            Result.success(tagRepository.createTag(tag))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}