package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.ImportantDateEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class UpdateImportantDateUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(importantDate: ImportantDateEntity): Boolean {
        return contactRepository.updateImportantDate(importantDate)
    }
}