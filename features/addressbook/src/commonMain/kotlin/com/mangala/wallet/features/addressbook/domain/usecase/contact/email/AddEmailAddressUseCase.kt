package com.mangala.wallet.features.addressbook.domain.usecase.contact.email

import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class AddEmailAddressUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(emailAddress: EmailAddressEntity): String {
        return contactRepository.insertEmailAddress(emailAddress)
    }
}