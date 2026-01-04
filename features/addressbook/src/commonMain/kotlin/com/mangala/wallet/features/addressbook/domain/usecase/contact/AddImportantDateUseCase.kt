package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.ImportantDateEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class AddImportantDateUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(importantDate: ImportantDateEntity): String {
        return contactRepository.insertImportantDate(importantDate)
    }
}