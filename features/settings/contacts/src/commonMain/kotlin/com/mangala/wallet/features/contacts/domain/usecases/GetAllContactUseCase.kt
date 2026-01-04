package com.mangala.wallet.features.contacts.domain.usecases

import com.mangala.wallet.features.contacts.domain.repository.ContactRepository
import com.mangala.wallet.model.contact.ContactEntity

class GetAllContactUseCase(
    private val contactRepository: ContactRepository
) {

    suspend operator fun invoke(): List<ContactEntity>{
        return contactRepository.getAllContact()
    }

    fun invokeFlow() = contactRepository.getAllContactFlow()
    fun invokeFlow(blockchainUid: String?) = contactRepository.getAllContactsByBlockchainUidFlow(blockchainUid)

}