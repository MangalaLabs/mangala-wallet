package com.mangala.wallet.features.addressbook.domain.usecase.contact.phone

import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class AddPhoneNumberUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(phoneNumber: PhoneNumberEntity): String {
        return contactRepository.insertPhoneNumber(phoneNumber)
    }
}