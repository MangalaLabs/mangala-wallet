package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.exceptions.DuplicateNameException
import com.mangala.wallet.features.addressbook.domain.exceptions.ValidationException
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants

class CreateContactUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contact: ContactEntity): String {
        // Validate contact name
        if (contact.name.isBlank()) {
            throw ValidationException("Contact name cannot be empty")
        }
        
        if (contact.name.length > ValidationConstants.MAX_CONTACT_NAME_LENGTH) {
            throw ValidationException("Contact name cannot exceed ${ValidationConstants.MAX_CONTACT_NAME_LENGTH} characters")
        }
        
        // Check for duplicate name
        val existingContact = contactRepository.findContactByName(contact.name)
        if (existingContact != null) {
            throw DuplicateNameException("Contact with name '${contact.name}' already exists")
        }
        
        return contactRepository.insertContact(contact)
    }
}