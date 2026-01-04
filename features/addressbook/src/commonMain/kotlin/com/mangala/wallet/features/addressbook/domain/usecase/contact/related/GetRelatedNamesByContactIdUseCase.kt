package com.mangala.wallet.features.addressbook.domain.usecase.contact.related

import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class GetRelatedNamesByContactIdUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contactId: String): List<RelatedNameEntity> {
        return contactRepository.getRelatedNamesByContactId(contactId)
    }
}