package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class CountAllContactsUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(): Int {
        return contactRepository.countAllContacts()
    }
}