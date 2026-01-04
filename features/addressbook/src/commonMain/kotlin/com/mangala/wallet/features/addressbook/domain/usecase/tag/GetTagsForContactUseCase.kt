package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

class GetTagsForContactUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(contactId: String): Result<List<TagEntity>> {
        return try {
            val tags = tagRepository.getTagsForContact(contactId)
            Result.success(tags)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}