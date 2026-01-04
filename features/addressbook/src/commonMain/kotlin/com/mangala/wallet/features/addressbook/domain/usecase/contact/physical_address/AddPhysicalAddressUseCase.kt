package com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address

import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class AddPhysicalAddressUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(physicalAddress: PhysicalAddressEntity): String {
        return contactRepository.insertPhysicalAddress(physicalAddress)
    }
}