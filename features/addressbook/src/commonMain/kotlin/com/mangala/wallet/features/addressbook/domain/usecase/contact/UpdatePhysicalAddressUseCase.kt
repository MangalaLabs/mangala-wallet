package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class UpdatePhysicalAddressUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(physicalAddress: PhysicalAddressEntity): Boolean {
        return contactRepository.updatePhysicalAddress(physicalAddress)
    }
}