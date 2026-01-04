package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import kotlinx.coroutines.flow.Flow

class SearchContactsUseCase(private val contactRepository: ContactRepository) {
    operator fun invoke(query: String, limit: Int = 50, offset: Int = 0): Flow<List<ContactEntity>> {
        return contactRepository.searchContacts(query, limit, offset)
    }
}