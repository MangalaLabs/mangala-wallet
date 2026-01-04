package com.mangala.wallet.features.contacts.domain.usecases

import com.mangala.wallet.features.contacts.domain.repository.ContactRepository
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.model.token.TokenType.Native.id

class UpdateContactUseCase(private val contactRepository: ContactRepository) {

    operator fun invoke(contactEntity: ContactEntity){
        return contactRepository.updateContact(contactEntity)
    }

}