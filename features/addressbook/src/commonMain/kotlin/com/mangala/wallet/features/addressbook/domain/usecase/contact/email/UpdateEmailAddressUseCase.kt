package com.mangala.wallet.features.addressbook.domain.usecase.contact.email

import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class UpdateEmailAddressUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(emailAddress: EmailAddressEntity): Boolean {
        return contactRepository.updateEmailAddress(emailAddress)
    }
}