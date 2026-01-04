package com.mangala.wallet.features.addressbook.domain.usecase.tag

import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

class RemoveTagFromContactUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(contactId: String, tagId: String): Result<Unit> {
        return try {
            tagRepository.removeTagFromContact(tagId, contactId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}