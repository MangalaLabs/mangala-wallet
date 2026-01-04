package com.mangala.wallet.features.addressbook.domain.usecase.contact.phone

import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class GetPhoneNumbersByContactIdUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contactId: String): List<PhoneNumberEntity> {
        return contactRepository.getPhoneNumbersByContactId(contactId)
    }
}