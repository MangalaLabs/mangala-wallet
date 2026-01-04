package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class UpdatePhoneNumberUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(phoneNumber: PhoneNumberEntity): Boolean {
        return contactRepository.updatePhoneNumber(phoneNumber)
    }
}