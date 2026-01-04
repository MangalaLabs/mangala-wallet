package com.mangala.wallet.features.addressbook.domain.usecase.contact.phone

import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class InsertPhoneNumbersBatchUseCase(private val contactRepository: ContactRepository) {
    /**
     * Insert multiple phone numbers in a single transaction
     * @param phoneNumbers List of phone numbers to insert
     * @return Map of original entities to their inserted IDs
     */
    suspend operator fun invoke(phoneNumbers: List<PhoneNumberEntity>): Map<PhoneNumberEntity, String> {
        return contactRepository.insertPhoneNumbersBatch(phoneNumbers)
    }
}