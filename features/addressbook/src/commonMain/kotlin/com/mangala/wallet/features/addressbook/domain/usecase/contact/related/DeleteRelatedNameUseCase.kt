package com.mangala.wallet.features.addressbook.domain.usecase.contact.related

import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class DeleteRelatedNameUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return contactRepository.deleteRelatedName(id)
    }
}