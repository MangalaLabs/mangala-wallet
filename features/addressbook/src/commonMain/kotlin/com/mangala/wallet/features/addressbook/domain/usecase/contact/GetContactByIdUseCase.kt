package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class GetContactByIdUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(id: String): ContactEntity? {
        return contactRepository.getContactById(id)
    }
}