package com.mangala.wallet.features.addressbook.domain.usecase.contact.phone

import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class DeletePhoneNumberUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return contactRepository.deletePhoneNumber(id)
    }
}
