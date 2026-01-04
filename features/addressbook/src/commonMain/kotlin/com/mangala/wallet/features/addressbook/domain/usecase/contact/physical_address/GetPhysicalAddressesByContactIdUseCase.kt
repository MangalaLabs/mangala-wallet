package com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address

import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class GetPhysicalAddressesByContactIdUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contactId: String): List<PhysicalAddressEntity> {
        return contactRepository.getPhysicalAddressesByContactId(contactId)
    }
}
