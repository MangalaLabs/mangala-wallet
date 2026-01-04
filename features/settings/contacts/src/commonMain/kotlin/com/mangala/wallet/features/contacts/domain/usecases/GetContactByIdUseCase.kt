package com.mangala.wallet.features.contacts.domain.usecases

import com.mangala.wallet.features.contacts.domain.repository.ContactRepository
import com.mangala.wallet.model.contact.ContactEntity

class GetContactByIdUseCase(private val contactRepository: ContactRepository) {

    suspend operator fun invoke(id: Long): ContactEntity?{
        return contactRepository.getContactById(id)
    }

    fun invokeFlow(id: Long) = contactRepository.getContactByIdFlow(id)

}