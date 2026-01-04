package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

/**
 * Use case to search for tags by name
 */
class SearchTagsUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(query: String): Result<List<TagEntity>> {
        return try {
            if (query.length < 2) {
                return Result.success(emptyList())
            }

            val tags = tagRepository.searchTags(query)
            Result.success(tags)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}