package com.mangala.wallet.features.addressbook.domain.usecase.facade

import com.mangala.wallet.features.addressbook.data.model.ContactDetailModel
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.domain.usecase.contact.CreateContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactDetailByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.NotifyContactsChangedUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.favorite.AddFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.RemoveFavoriteUseCase

/**
 * Facade for core contact operations - groups contact, favorite, and avatar management
 * Reduces dependencies from 8+ use cases to 1 facade in ContactEditViewModel
 */
class ContactCoreUseCase(
    private val createContactUseCase: CreateContactUseCase,
    private val getContactDetailByIdUseCase: GetContactDetailByIdUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val notifyContactsChangedUseCase: NotifyContactsChangedUseCase
) {
    
    suspend fun createContact(contact: ContactEntity): Result<String> {
        return try {
            val contactId = createContactUseCase(contact)
            notifyContactsChangedUseCase()
            Result.success(contactId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getContactDetail(contactId: String): Result<ContactDetailModel?> {
        return try {
            val contactDetail = getContactDetailByIdUseCase(contactId)
            Result.success(contactDetail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateContact(contact: ContactEntity): Result<Unit> {
        return try {
            updateContactUseCase(contact)
            notifyContactsChangedUseCase()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateBasicInfo(
        contactId: String,
        name: String,
        note: String,
        avatarSource: AvatarSource?,
        securityLevel: SecurityLevel,
        displayMode: DisplayMode
    ): Result<Unit> {
        return try {
            // Get the current contact first
            val currentContact = getContactDetailByIdUseCase(contactId)
            if (currentContact?.contact == null) {
                return Result.failure(Exception("Contact not found"))
            }
            
            // Update the contact entity with new values
            val avatarString = avatarSource?.let { AvatarSource.toString(it) }

            val updatedContact = currentContact.contact.copy(
                name = name,
                notes = note,
                avatar = avatarString,
                securityLevel = securityLevel,
                privacyDisplayMode = displayMode,
                updatedAt = kotlinx.datetime.Clock.System.now()
            )
            
            // Save the updated contact
            updateContactUseCase(updatedContact)
            notifyContactsChangedUseCase()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun toggleFavorite(contactId: String, isFavorite: Boolean): Result<Unit> {
        return try {
            if (isFavorite) {
                addFavoriteUseCase(contactId)
            } else {
                removeFavoriteUseCase(contactId)
            }
            notifyContactsChangedUseCase()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateAvatar(contactId: String, avatarSource: AvatarSource?): Result<Unit> {
        return try {
            // For now, avatar update is part of contact update
            // We could expand this if there are specific avatar use cases
            notifyContactsChangedUseCase()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}