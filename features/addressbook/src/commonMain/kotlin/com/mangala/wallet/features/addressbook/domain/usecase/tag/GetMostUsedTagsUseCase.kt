package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

/**
 * Use case to get most frequently used tags (for suggestions)
 */
class GetMostUsedTagsUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(limit: Int = 10): Result<List<Pair<TagEntity, Int>>> {
        return try {
            val tags = tagRepository.getMostUsedTags(limit)
            Result.success(tags)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}