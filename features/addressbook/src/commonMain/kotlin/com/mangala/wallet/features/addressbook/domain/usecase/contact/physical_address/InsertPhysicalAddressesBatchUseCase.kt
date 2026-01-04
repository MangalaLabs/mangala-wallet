package com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address

import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class InsertPhysicalAddressesBatchUseCase (private val contactRepository: ContactRepository) {
    suspend operator fun invoke(physicalAddresses: List<PhysicalAddressEntity>): Map<PhysicalAddressEntity, String> {
        return contactRepository.insertPhysicalAddressesBatch(physicalAddresses)
    }
}