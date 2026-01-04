package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class UpdateRelatedNameUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(relatedName: RelatedNameEntity): Boolean {
        return contactRepository.updateRelatedName(relatedName)
    }
}