package com.mangala.wallet.features.addressbook.domain.usecase.contact.email

import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class DeleteEmailAddressUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return contactRepository.deleteEmailAddress(id)
    }
}