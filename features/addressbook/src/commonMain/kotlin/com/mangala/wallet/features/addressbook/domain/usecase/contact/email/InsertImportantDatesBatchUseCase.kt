package com.mangala.wallet.features.addressbook.domain.usecase.contact.email

import com.mangala.wallet.features.addressbook.data.model.contact.ImportantDateEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class InsertImportantDatesBatchUseCase(private val contactRepository: ContactRepository) {
    /**
     * Insert multiple important dates in a single transaction
     * @param importantDates List of important dates to insert
     * @return Map of original entities to their inserted IDs
     */
    suspend operator fun invoke(importantDates: List<ImportantDateEntity>): Map<ImportantDateEntity, String> {
        return contactRepository.insertImportantDatesBatch(importantDates)
    }
}