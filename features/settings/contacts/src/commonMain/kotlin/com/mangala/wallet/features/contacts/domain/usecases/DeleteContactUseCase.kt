package com.mangala.wallet.features.contacts.domain.usecases

import com.mangala.wallet.features.contacts.domain.repository.ContactRepository

class DeleteContactUseCase(private val contactRepository: ContactRepository) {

    suspend operator fun invoke(id: Long){
        return contactRepository.deleteContactById(id)
    }

}