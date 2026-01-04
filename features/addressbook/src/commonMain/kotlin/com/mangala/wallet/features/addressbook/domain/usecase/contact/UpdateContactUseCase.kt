package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.exceptions.DuplicateNameException
import com.mangala.wallet.features.addressbook.domain.exceptions.ValidationException
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants

class UpdateContactUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contact: ContactEntity): Boolean {
        // Validate contact name
        if (contact.name.isBlank()) {
            throw ValidationException("Contact name cannot be empty")
        }
        
        if (contact.name.length > ValidationConstants.MAX_CONTACT_NAME_LENGTH) {
            throw ValidationException("Contact name cannot exceed ${ValidationConstants.MAX_CONTACT_NAME_LENGTH} characters")
        }
        
        // Check if contact exists
        val existingContact = contactRepository.getContactById(contact.id)
            ?: throw ValidationException("Contact not found")
        
        // Check for duplicate name (excluding current contact)
        val contactWithSameName = contactRepository.findContactByName(contact.name)
        if (contactWithSameName != null && contactWithSameName.id != contact.id) {
            throw DuplicateNameException("Another contact with name '${contact.name}' already exists")
        }
        
        return contactRepository.updateContact(contact)
    }
}