package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class FindContactByPhoneNumberUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(phoneNumber: String): ContactEntity? {
        return contactRepository.findContactByPhoneNumber(phoneNumber)
    }
}