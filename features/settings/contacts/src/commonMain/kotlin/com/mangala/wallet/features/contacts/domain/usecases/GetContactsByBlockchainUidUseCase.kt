package com.mangala.wallet.features.contacts.domain.usecases

import com.mangala.wallet.features.contacts.domain.repository.ContactRepository
import com.mangala.wallet.model.contact.ContactEntity

class GetContactsByBlockchainUidUseCase(private val contactRepository: ContactRepository) {

    suspend operator fun invoke(blockchainUid: String): List<ContactEntity>{
        return contactRepository.getAllContactsByBlockchainUid(blockchainUid)
    }

    fun invokeFlow(blockchainUid: String) = contactRepository.getAllContactsByBlockchainUidFlow(blockchainUid)
}