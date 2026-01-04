package com.mangala.wallet.features.addressbook.domain.usecase.contact.related

import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class AddRelatedNameUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(relatedName: RelatedNameEntity): String {
        return contactRepository.insertRelatedName(relatedName)
    }
}