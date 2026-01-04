package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class UpdateLastViewedAtUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return contactRepository.updateLastViewedAt(id)
    }
}