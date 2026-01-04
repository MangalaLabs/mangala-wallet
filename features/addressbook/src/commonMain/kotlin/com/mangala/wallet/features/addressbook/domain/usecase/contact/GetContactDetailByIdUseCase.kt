package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.ContactDetailModel
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class GetContactDetailByIdUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(id: String): ContactDetailModel? {
        return contactRepository.getContactDetailById(id)
    }
}