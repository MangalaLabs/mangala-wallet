package com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address

import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class DeletePhysicalAddressUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return contactRepository.deletePhysicalAddress(id)
    }
}