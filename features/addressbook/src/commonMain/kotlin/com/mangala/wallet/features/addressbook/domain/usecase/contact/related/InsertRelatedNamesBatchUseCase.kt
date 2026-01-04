package com.mangala.wallet.features.addressbook.domain.usecase.contact.related

import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class InsertRelatedNamesBatchUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(relatedNames: List<RelatedNameEntity>): Map<RelatedNameEntity, String> {
        return contactRepository.insertRelatedNamesBatch(relatedNames)
    }
}