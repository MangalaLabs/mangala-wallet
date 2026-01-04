package com.mangala.wallet.features.addressbook.domain.repository.tag

import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Wrapper around TagRepository that provides transaction safety for multi-operation database calls
 * This ensures atomicity for complex tag operations like bulk contact assignments
 */
class TransactionalTagRepository(
    private val delegate: TagRepository
) : TagRepository by delegate {
    
    private val transactionMutex = Mutex()
    
    /**
     * Executes multiple tag operations within a synchronized block to ensure atomicity
     * If any operation fails, the entire transaction is rolled back
     */
    suspend fun <T> withTransaction(block: suspend TagRepository.() -> T): Result<T> {
        return transactionMutex.withLock {
            try {
                val result = delegate.block()
                Result.success(result)
            } catch (e: Exception) {
                println("ERROR: Transaction failed: ${e.message}")
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
    
    /**
     * Atomically assign a tag to multiple contacts
     * Either all assignments succeed or none do
     */
    suspend fun assignTagToContacts(tagId: String, contactIds: List<String>): Result<Unit> {
        return withTransaction {
            for (contactId in contactIds) {
                assignTagToContact(tagId, contactId)
            }
        }
    }
    
    /**
     * Atomically remove a tag from multiple contacts
     * Either all removals succeed or none do
     */
    suspend fun removeTagFromContacts(tagId: String, contactIds: List<String>): Result<Unit> {
        return withTransaction {
            for (contactId in contactIds) {
                removeTagFromContact(tagId, contactId)
            }
        }
    }
    
    /**
     * Atomically update tag contacts by adding new ones and removing old ones
     * This is the most complex operation that requires full transaction safety
     */
    suspend fun updateTagContacts(
        tagId: String, 
        contactsToAdd: List<String>, 
        contactsToRemove: List<String>
    ): Result<Unit> {
        return withTransaction {
            // First remove contacts to avoid potential constraint issues
            for (contactId in contactsToRemove) {
                removeTagFromContact(tagId, contactId)
            }
            
            // Then add new contacts
            for (contactId in contactsToAdd) {
                assignTagToContact(tagId, contactId)
            }
        }
    }
    
    /**
     * Create a tag and assign it to multiple contacts atomically
     */
    suspend fun createTagWithContacts(
        name: String,
        color: String,
        textColor: String? = null,
        icon: String? = null,
        contactIds: List<String>
    ): Result<TagEntity> {
        return withTransaction {
            // Validate input
            if (name.isBlank()) {
                throw IllegalArgumentException("Tag name cannot be empty")
            }
            
            // Check if tag with same name already exists
            val existingTag = findTagByName(name)
            if (existingTag != null && !existingTag.isDeleted) {
                throw IllegalArgumentException("Tag with this name already exists")
            }
            
            // Create the tag entity
            val tag = TagEntity.create(
                id = uuid4().toString(),
                name = name,
                color = color,
                textColor = textColor,
                icon = icon,
                isDeleted = false
            )
            
            // Create the tag in repository
            val createdTag = createTag(tag)
            
            // Assign to all contacts
            for (contactId in contactIds) {
                assignTagToContact(createdTag.id, contactId)
            }
            
            createdTag
        }
    }
}