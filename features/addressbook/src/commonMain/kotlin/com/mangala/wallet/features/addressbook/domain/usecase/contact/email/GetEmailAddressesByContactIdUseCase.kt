package com.mangala.wallet.features.addressbook.domain.usecase.contact.email

import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class GetEmailAddressesByContactIdUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contactId: String): List<EmailAddressEntity> {
        return contactRepository.getEmailAddressesByContactId(contactId)
    }
}