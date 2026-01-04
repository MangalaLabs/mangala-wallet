package com.mangala.wallet.features.addressbook.domain.usecase.contact.favorite

import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class AddFavoriteUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contactId: String): Boolean {
        return contactRepository.addFavorite(contactId)
    }
}