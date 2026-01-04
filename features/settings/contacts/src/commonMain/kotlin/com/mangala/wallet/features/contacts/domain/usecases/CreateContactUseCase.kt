package com.mangala.wallet.features.contacts.domain.usecases

import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.features.contacts.domain.repository.ContactRepository
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.model.token.TokenEntity

class CreateContactUseCase(private val contactRepository: ContactRepository) {

    operator fun invoke(contact: ContactEntity): Long {
        return contactRepository.insertContact(contact)
    }

}