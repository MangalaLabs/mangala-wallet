package com.mangala.wallet.features.addressbook.domain.usecase.contact.email

import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class InsertEmailAddressesBatchUseCase(private val contactRepository: ContactRepository) {
    /**
     * Insert multiple email addresses in a single transaction
     * @param emailAddresses List of email addresses to insert
     * @return Map of original entities to their inserted IDs
     */
    suspend operator fun invoke(emailAddresses: List<EmailAddressEntity>): Map<EmailAddressEntity, String> {
        return contactRepository.insertEmailAddressesBatch(emailAddresses)
    }
}