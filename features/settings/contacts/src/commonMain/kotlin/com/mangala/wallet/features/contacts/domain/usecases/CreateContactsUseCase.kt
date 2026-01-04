package com.mangala.wallet.features.contacts.domain.usecases

import com.mangala.wallet.features.contacts.domain.repository.ContactRepository
import com.mangala.wallet.model.contact.ContactEntity

class CreateContactsUseCase(private val contactRepository: ContactRepository) {

    suspend operator fun invoke(contacts: List<ContactEntity>){
        return contactRepository.insertContacts(contacts)
    }

}