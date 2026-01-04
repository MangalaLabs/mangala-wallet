package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

class UpdateTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(id: String, name: String, color: String, textColor: String? = null, icon: String? = null): Result<TagEntity> {
        return try {
            if (name.isBlank()) {
                return Result.failure(IllegalArgumentException("Tag name cannot be empty"))
            }
            if (name.length > 20) {
                return Result.failure(IllegalArgumentException("Tag name cannot exceed 20 characters"))
            }

            // Check if tag exists
            val existingTag = tagRepository.getTagById(id)
                ?: return Result.failure(IllegalArgumentException("Tag not found"))

            // Check if another tag with same name exists
            val tagWithSameName = tagRepository.findTagByName(name)
            if (tagWithSameName != null && tagWithSameName.id != id && !tagWithSameName.isDeleted) {
                return Result.failure(IllegalArgumentException("Another tag with this name already exists"))
            }

            val updatedTag = existingTag.copy(
                name = name,
                color = color,
                textColor = textColor,
                icon = icon,
                updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            )

            Result.success(tagRepository.updateTag(updatedTag))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}