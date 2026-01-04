package com.mangala.wallet.features.addressbook.domain.usecase.contact.email

import com.mangala.wallet.features.addressbook.data.model.contact.SocialProfileEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class InsertSocialProfilesBatchUseCase(private val contactRepository: ContactRepository) {
    /**
     * Insert multiple social profiles in a single transaction
     * @param profiles List of social profiles to insert
     * @return Map of original entities to their inserted IDs
     */
    suspend operator fun invoke(profiles: List<SocialProfileEntity>): Map<SocialProfileEntity, String> {
        return contactRepository.insertSocialProfilesBatch(profiles)
    }
}