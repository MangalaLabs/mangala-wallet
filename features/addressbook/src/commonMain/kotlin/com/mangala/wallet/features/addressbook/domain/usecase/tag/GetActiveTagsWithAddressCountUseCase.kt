package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

/**
 * Use case to get active tags with address counts
 * This helps display the correct count of addresses assigned to each tag
 */
class GetActiveTagsWithAddressCountUseCase(private val tagRepository: TagRepository) {
    /**
     * Get all active tags with address counts
     * 
     * @return Result containing list of tags with address counts or failure
     */
    suspend operator fun invoke(): Result<List<TagEntity>> {
        return try {
            val tags = tagRepository.getActiveTagsWithAddressCount()
            Result.success(tags)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}