package com.mangala.wallet.features.addressbook.data.local.contact

import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

class DeleteSocialProfilesByContactIdUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contactId: String): Boolean {
        return contactRepository.deleteSocialProfilesByContactId(contactId)
    }
}