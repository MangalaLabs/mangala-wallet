package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository

/**
 * Use case for notifying that contact data has changed.
 * This is useful when we need to force a refresh of contact lists after operations
 * like adding/editing contacts or toggling favorites.
 */
class NotifyContactsChangedUseCase(private val contactRepository: ContactRepository) {
    
    /**
     * Notify that contacts data has changed and needs to be refreshed
     * @return True if notification was successful
     */
    suspend operator fun invoke(): Boolean {
        return contactRepository.notifyContactsChanged()
    }
}