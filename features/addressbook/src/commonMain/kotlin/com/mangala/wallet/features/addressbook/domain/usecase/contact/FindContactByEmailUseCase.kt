package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class FindContactByEmailUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(email: String): ContactEntity? {
        return contactRepository.findContactByEmail(email)
    }
}