package com.mangala.wallet.features.addressbook.domain.usecase.facade

import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ImportantDateEntity
import com.mangala.wallet.features.addressbook.domain.model.CalendarType
import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.data.model.contact.SocialProfileEntity
import com.mangala.wallet.features.addressbook.data.local.contact.DeleteSocialProfilesByContactIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.DeleteImportantDateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateImportantDateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdatePhoneNumberUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdatePhysicalAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateRelatedNameUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.AddEmailAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.DeleteEmailAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.InsertImportantDatesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.InsertSocialProfilesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.UpdateEmailAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.phone.AddPhoneNumberUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.phone.DeletePhoneNumberUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address.DeletePhysicalAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address.InsertPhysicalAddressesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.related.AddRelatedNameUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.related.DeleteRelatedNameUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.related.InsertRelatedNamesBatchUseCase
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Facade for communication-related operations - groups email, phone, address, social profiles
 * Reduces dependencies from 12+ use cases to 1 facade in ContactEditViewModel
 */
class CommunicationUseCase(
    // Email use cases
    private val addEmailAddressUseCase: AddEmailAddressUseCase,
    private val updateEmailAddressUseCase: UpdateEmailAddressUseCase,
    private val deleteEmailAddressUseCase: DeleteEmailAddressUseCase,
    
    // Phone use cases  
    private val addPhoneNumberUseCase: AddPhoneNumberUseCase,
    private val updatePhoneNumberUseCase: UpdatePhoneNumberUseCase,
    private val deletePhoneNumberUseCase: DeletePhoneNumberUseCase,
    
    // Physical address use cases
    private val insertPhysicalAddressesBatchUseCase: InsertPhysicalAddressesBatchUseCase,
    private val deletePhysicalAddressUseCase: DeletePhysicalAddressUseCase,
    private val updatePhysicalAddressUseCase: UpdatePhysicalAddressUseCase,
    
    // Social profile use cases
    private val insertSocialProfilesBatchUseCase: InsertSocialProfilesBatchUseCase,
    private val deleteSocialProfilesByContactIdUseCase: DeleteSocialProfilesByContactIdUseCase,
    
    // Important dates use cases
    private val insertImportantDatesBatchUseCase: InsertImportantDatesBatchUseCase,
    private val deleteImportantDateUseCase: DeleteImportantDateUseCase,
    private val updateImportantDateUseCase: UpdateImportantDateUseCase,

    // Related names (nicknames) use cases
    private val insertRelatedNamesBatchUseCase: InsertRelatedNamesBatchUseCase,
    private val addRelatedNameUseCase: AddRelatedNameUseCase,
    private val updateRelatedNameUseCase: UpdateRelatedNameUseCase,
    private val deleteRelatedNameUseCase: DeleteRelatedNameUseCase
) {
    
    // Email operations
    suspend fun addEmailAddress(email: EmailAddressEntity): Result<Unit> {
        return try {
            addEmailAddressUseCase(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateEmailAddress(email: EmailAddressEntity): Result<Unit> {
        return try {
            updateEmailAddressUseCase(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteEmailAddress(emailId: String): Result<Unit> {
        return try {
            deleteEmailAddressUseCase(emailId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Phone operations
    suspend fun addPhoneNumber(phone: PhoneNumberEntity): Result<Unit> {
        return try {
            addPhoneNumberUseCase(phone)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updatePhoneNumber(phone: PhoneNumberEntity): Result<Unit> {
        return try {
            updatePhoneNumberUseCase(phone)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deletePhoneNumber(phoneId: String): Result<Unit> {
        return try {
            deletePhoneNumberUseCase(phoneId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Physical address operations
    suspend fun savePhysicalAddresses(contactId: String, addresses: List<PhysicalAddressEntity>): Result<Unit> {
        return try {
            insertPhysicalAddressesBatchUseCase(addresses)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deletePhysicalAddress(addressId: String): Result<Unit> {
        return try {
            deletePhysicalAddressUseCase(addressId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updatePhysicalAddress(address: PhysicalAddressEntity): Result<Unit> {
        return try {
            updatePhysicalAddressUseCase(address)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Social profile operations
    suspend fun saveSocialProfiles(contactId: String, socialProfiles: List<SocialProfileEntity>): Result<Unit> {
        return try {
            insertSocialProfilesBatchUseCase(socialProfiles)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteSocialProfilesByContactId(contactId: String): Result<Unit> {
        return try {
            deleteSocialProfilesByContactIdUseCase(contactId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Important dates operations
    suspend fun saveImportantDates(contactId: String, dates: List<ImportantDateEntity>): Result<Unit> {
        return try {
            if (dates.isNotEmpty()) {
                insertImportantDatesBatchUseCase(dates)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteImportantDate(dateId: String): Result<Unit> {
        return try {
            deleteImportantDateUseCase(dateId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateImportantDate(dateId: String, date: Instant, description: String, calendarType: CalendarType = CalendarType.SOLAR): Result<Unit> {
        return try {
            // Note: This is a simplified implementation
            // In a real scenario, we would need to fetch the existing important date to get the contactId
            // For now, the repository should handle finding the correct important date by ID
            val importantDate = ImportantDateEntity(
                id = dateId,
                contactId = "", // Repository will need to handle this
                date = date,
                description = description, // ✅ FIX: Use full description with lunar data
                calendarType = calendarType, // ✅ ADD: Include calendar type
                createdAt = Clock.System.now(), // Will be ignored by update
                updatedAt = Clock.System.now()
            )
            val success = updateImportantDateUseCase(importantDate)
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update important date"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Related names (nicknames) operations
    suspend fun saveRelatedNames(contactId: String, relatedNames: List<RelatedNameEntity>): Result<Unit> {
        return try {
            if (relatedNames.isNotEmpty()) {
                insertRelatedNamesBatchUseCase(relatedNames)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addRelatedName(relatedName: RelatedNameEntity): Result<Unit> {
        return try {
            addRelatedNameUseCase(relatedName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRelatedName(relatedName: RelatedNameEntity): Result<Unit> {
        return try {
            updateRelatedNameUseCase(relatedName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRelatedName(relatedNameId: String): Result<Unit> {
        return try {
            deleteRelatedNameUseCase(relatedNameId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}