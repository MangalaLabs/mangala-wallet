package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class IsContactFavoriteUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contactId: String): Boolean {
        return contactRepository.isContactFavorite(contactId)
    }
}