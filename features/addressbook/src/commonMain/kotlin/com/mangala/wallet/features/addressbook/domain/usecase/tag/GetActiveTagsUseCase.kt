package com.mangala.wallet.features.addressbook.domain.usecase.tag

import app.cash.paging.PagingData
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository
import kotlinx.coroutines.flow.Flow

class GetActiveTagsUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(): Result<List<TagEntity>> {
        return try {
            // Sử dụng phương thức mới để lấy tags kèm số lượng contact
            val tagsWithCount = tagRepository.getActiveTagsWithContactCount()
            Result.success(tagsWithCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get paginated active tags with contact counts and search functionality
     * This replaces manual loading with proper Cash App Paging for better performance
     * 
     * @param searchQuery Optional search query to filter tags (null/empty = get all)
     * @return Flow of PagingData for TagEntity with contact counts
     */
    fun getPaginatedTags(searchQuery: String? = null): Flow<PagingData<TagEntity>> {
        return tagRepository.getPaginatedTags(searchQuery)
    }
}