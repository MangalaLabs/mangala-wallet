package com.mangala.wallet.features.addressbook.data.local.tag

import app.cash.paging.PagingSource
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import kotlinx.coroutines.async
import kotlinx.datetime.Instant

/**
 * Implementation of TagLocalDataSource that interacts with the SQLDelight database
 * Simplified version without tag caching in contact_tags table
 */
class TagLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TagLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    override suspend fun getActiveTags(): List<TagEntity> {
        return withContext(ioDispatcher) {
            dbQuery.getActiveTags()
                .executeAsList()
                .map {
                    TagEntity(
                        id = it.id,
                        name = it.name,
                        color = it.color,
                        textColor = it.text_color,
                        icon = it.icon,
                        isDeleted = it.is_deleted,
                        createdAt = Instant.fromEpochMilliseconds(it.created_at),
                        updatedAt = Instant.fromEpochMilliseconds(it.updated_at)
                    )
                }
        }
    }

    override suspend fun getTagById(id: String): TagEntity? {
        return withContext(ioDispatcher) {
            val contactCountDeferred = async {
                getContactCountWithTag(id)
            }
            val getTagDeferred = async {
                dbQuery.getTagById(id)
                    .executeAsOneOrNull()
            }
            getTagDeferred.await()
                ?.let {
                    TagEntity(
                        id = it.id,
                        name = it.name,
                        color = it.color,
                        textColor = it.text_color,
                        icon = it.icon,
                        isDeleted = it.is_deleted,
                        contactCount = contactCountDeferred.await(),
                        createdAt = Instant.fromEpochMilliseconds(it.created_at),
                        updatedAt = Instant.fromEpochMilliseconds(it.updated_at)
                    )
                }
        }
    }

    override suspend fun findTagByName(name: String): TagEntity? {
        return withContext(ioDispatcher) {
            dbQuery.findTagByName(name)
                .executeAsOneOrNull()
                ?.let {
                    TagEntity(
                        id = it.id,
                        name = it.name,
                        color = it.color,
                        textColor = it.text_color,
                        icon = it.icon,
                        isDeleted = it.is_deleted,
                        createdAt = Instant.fromEpochMilliseconds(it.created_at),
                        updatedAt = Instant.fromEpochMilliseconds(it.updated_at)
                    )
                }
        }
    }

    override suspend fun createTag(tag: TagEntity): TagEntity {
        withContext(ioDispatcher) {
            dbQuery.insertTag(
                id = tag.id,
                name = tag.name,
                color = tag.color,
                text_color = tag.textColor,
                icon = tag.icon,
                is_deleted = tag.isDeleted,
                created_at = tag.createdAt.toEpochMilliseconds(),
                updated_at = tag.updatedAt.toEpochMilliseconds()
            )
        }
        return tag
    }

    override suspend fun updateTag(tag: TagEntity): TagEntity {
        withContext(ioDispatcher) {
            dbQuery.updateTag(
                id = tag.id,
                name = tag.name,
                color = tag.color,
                text_color = tag.textColor,
                icon = tag.icon,
                updated_at = tag.updatedAt.toEpochMilliseconds()
            )
        }
        return tag
    }

    override suspend fun markTagAsDeleted(tagId: String) {
        withContext(ioDispatcher) {
            dbQuery.markTagAsDeleted(
                id = tagId,
                updated_at = localDateTimeToMillis(localDateTimeNow())
            )
        }
    }

    override suspend fun hardDeleteTag(tagId: String): Boolean = withContext(ioDispatcher) {
        // Thanks to ON DELETE CASCADE foreign key constraints,
        // deleting the tag will automatically delete all related entries
        // in contact_tags and address_tags tables
        dbQuery.deleteTag(tagId)
        return@withContext true
    }

    override suspend fun restoreTag(tagId: String, color: String, textColor: String?): TagEntity {
        withContext(ioDispatcher) {
            dbQuery.restoreTag(
                id = tagId,
                color = color,
                text_color = textColor, // Can be null, will be calculated on the fly if null
                updated_at = localDateTimeToMillis(localDateTimeNow())
            )
        }

        return getTagById(tagId)!!
    }

    override suspend fun assignTagToContact(tagId: String, contactId: String) {
        withContext(ioDispatcher) {
            try {
                
                // Kiểm tra contact có tồn tại không
                val contactExists = dbQuery.getContactById(contactId).executeAsOneOrNull() != null
                
                if (!contactExists) {
                    println("WARNING: Attempted to assign tag to non-existent contact: $contactId")
                    return@withContext
                }
                
                // Kiểm tra tag có tồn tại không
                val tagExists = dbQuery.getTagById(tagId).executeAsOneOrNull() != null
                
                if (!tagExists) {
                    println("WARNING: Attempted to assign non-existent tag: $tagId")
                    return@withContext
                }
                
                // Check if association already exists to avoid duplicates
                val exists = dbQuery.checkAssociationExists(
                    contact_id = contactId,
                    tag_id = tagId
                ).executeAsOneOrNull() != null

                if (!exists) {
                    try {
                        dbQuery.insertContactTag(
                            contact_id = contactId,
                            tag_id = tagId,
                            created_at = localDateTimeToMillis(localDateTimeNow())
                        )
                    } catch (e: Exception) {
                        println("ERROR: Database error while inserting contact tag: ${e.message}")
                        throw e
                    }
                } else {
                }
            } catch (e: Exception) {
                println("ERROR: Failed to assign tag to contact: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }

    override suspend fun removeTagFromContact(tagId: String, contactId: String) {
        withContext(ioDispatcher) {
            try {
                dbQuery.removeTagFromContact(
                    contact_id = contactId,
                    tag_id = tagId
                )
            } catch (e: Exception) {
                println("ERROR: Failed to remove tag from contact: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun getTagsForContact(contactId: String): List<TagEntity> {
        return withContext(ioDispatcher) {
            // Get all tags for the contact, including soft-deleted ones
            dbQuery.getTagsForContact(contactId)
                .executeAsList()
                .map {
                    TagEntity(
                        id = it.id,
                        name = it.name,
                        color = it.color,
                        textColor = it.text_color,
                        icon = it.icon,
                        isDeleted = it.is_deleted,
                        createdAt = Instant.fromEpochMilliseconds(it.created_at),
                        updatedAt = Instant.fromEpochMilliseconds(it.updated_at)
                    )
                }
        }
    }

    override suspend fun getActiveTagsForContact(contactId: String): List<TagEntity> {
        return withContext(ioDispatcher) {
            // Get only active (non-deleted) tags for the contact
            dbQuery.getActiveTagsForContact(contactId)
                .executeAsList()
                .map {
                    TagEntity(
                        id = it.id,
                        name = it.name,
                        color = it.color,
                        textColor = it.text_color,
                        icon = it.icon,
                        isDeleted = it.is_deleted,
                        createdAt = Instant.fromEpochMilliseconds(it.created_at),
                        updatedAt = Instant.fromEpochMilliseconds(it.updated_at)
                    )
                }
        }
    }

    override suspend fun getContactIdsWithTag(tagId: String): List<String> {
        return withContext(ioDispatcher) {
            try {
                val result = dbQuery.getContactIdsWithTag(tagId).executeAsList()
                result.map { it }
            } catch (e: Exception) {
                println("ERROR: Failed to get contacts with tag: ${e.message}")
                emptyList()
            }
        }
    }

    override suspend fun getContactIdsWithTag(tagId: String, limit: Int, offset: Int): List<String> {
        return withContext(ioDispatcher) {
            try {
                // Use fallback pagination since SQL functions don't exist yet
                val allResults = dbQuery.getContactIdsWithTag(tagId).executeAsList()
                val paginatedResults = allResults.drop(offset).take(limit)
                paginatedResults
            } catch (e: Exception) {
                println("ERROR: Failed to get paginated contacts with tag: ${e.message}")
                emptyList()
            }
        }
    }

    override suspend fun getContactCountWithTag(tagId: String): Int {
        return withContext(ioDispatcher) {
            try {
                dbQuery.countContactAssociatedWithTagByTagId(tagId).executeAsOneOrNull()?.toInt() ?: 0
            } catch (e: Exception) {
                println("ERROR: Failed to count contacts with tag: ${e.message}")
                0
            }
        }
    }

    override suspend fun batchAssignTagsToContact(contactId: String, tagIds: List<String>) {
        withContext(ioDispatcher) {
            try {
                // Kiểm tra contact có tồn tại không
                val contactExists = dbQuery.getContactById(contactId).executeAsOneOrNull() != null
                
                if (!contactExists) {
                    println("WARNING: Attempted to assign tags to non-existent contact: $contactId")
                    return@withContext
                }
                
                dbQuery.transaction {
                    val now = localDateTimeToMillis(localDateTimeNow())
    
                    for (tagId in tagIds) {
                        // Kiểm tra tag có tồn tại không
                        val tagExists = dbQuery.getTagById(tagId).executeAsOneOrNull() != null
                        
                        if (!tagExists) {
                            println("WARNING: Skipping non-existent tag: $tagId")
                            continue
                        }
                        
                        // Skip if association already exists
                        val exists = dbQuery.checkAssociationExists(
                            contact_id = contactId,
                            tag_id = tagId
                        ).executeAsOneOrNull() != null
    
                        if (!exists) {
                            try {
                                dbQuery.insertContactTag(
                                    contact_id = contactId,
                                    tag_id = tagId,
                                    created_at = now
                                )
                            } catch (e: Exception) {
                                println("ERROR: Database error while inserting contact tag: ${e.message}")
                                // Không throw exception trong transaction để tiếp tục với các tag khác
                            }
                        } else {
                        }
                    }
                }
            } catch (e: Exception) {
                println("ERROR: Failed to batch assign tags to contact: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }

    override suspend fun batchAssignTagToContacts(tagId: String, contactIds: List<String>) {
        withContext(ioDispatcher) {
            try {
                // Kiểm tra tag có tồn tại không
                val tagExists = dbQuery.getTagById(tagId).executeAsOneOrNull() != null
                
                if (!tagExists) {
                    println("WARNING: Attempted to assign non-existent tag $tagId to contacts")
                    return@withContext
                }
                
                dbQuery.transaction {
                    val now = localDateTimeToMillis(localDateTimeNow())
    
                    for (contactId in contactIds) {
                        // Kiểm tra contact có tồn tại không
                        val contactExists = dbQuery.getContactById(contactId).executeAsOneOrNull() != null
                        
                        if (!contactExists) {
                            println("WARNING: Skipping non-existent contact: $contactId")
                            continue
                        }
                        
                        // Skip if association already exists
                        val exists = dbQuery.checkAssociationExists(
                            contact_id = contactId,
                            tag_id = tagId
                        ).executeAsOneOrNull() != null
    
                        if (!exists) {
                            try {
                                dbQuery.insertContactTag(
                                    contact_id = contactId,
                                    tag_id = tagId,
                                    created_at = now
                                )
                            } catch (e: Exception) {
                                println("ERROR: Database error while inserting contact tag: ${e.message}")
                                // Không throw exception trong transaction để tiếp tục với các contact khác
                            }
                        } else {
                        }
                    }
                }
            } catch (e: Exception) {
                println("ERROR: Failed to batch assign tag to contacts: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }

    override suspend fun searchTags(query: String): List<TagEntity> {
        return withContext(ioDispatcher) {
            // Use % for partial matching on both sides
            val searchPattern = "%${query.trim()}%"

            dbQuery.searchActiveTags(searchPattern)
                .executeAsList()
                .map {
                    TagEntity(
                        id = it.id,
                        name = it.name,
                        color = it.color,
                        textColor = it.text_color,
                        icon = it.icon,
                        isDeleted = it.is_deleted,
                        createdAt = Instant.fromEpochMilliseconds(it.created_at),
                        updatedAt = Instant.fromEpochMilliseconds(it.updated_at)
                    )
                }
        }
    }

    override suspend fun getMostUsedTags(limit: Int): List<Pair<TagEntity, Int>> {
        return withContext(ioDispatcher) {
            dbQuery.getMostUsedTags(limit.toLong())
                .executeAsList()
                .map {
                    Pair(
                        TagEntity(
                            id = it.id,
                            name = it.name,
                            color = it.color,
                            textColor = it.text_color,
                            icon = it.icon,
                            isDeleted = it.is_deleted,
                            createdAt = Instant.fromEpochMilliseconds(it.created_at),
                            updatedAt = Instant.fromEpochMilliseconds(it.updated_at)
                        ),
                        it.usage_count.toInt()
                    )
                }
        }
    }

    override suspend fun getActiveTagsWithContactCount(): List<TagEntity> {
        return withContext(ioDispatcher) {
            dbQuery.getActiveTagsWithContactCount(
                query = "",
                limit = Long.MAX_VALUE,
                offset = 0L
            ).executeAsList()
                .map {
                    TagEntity(
                        id = it.id,
                        name = it.name,
                        color = it.color,
                        textColor = it.text_color,
                        icon = it.icon,
                        isDeleted = it.is_deleted,
                        createdAt = Instant.fromEpochMilliseconds(it.created_at),
                        updatedAt = Instant.fromEpochMilliseconds(it.updated_at),
                        contactCount = it.contact_count?.toInt() ?: 0
                    )
                }
        }
    }

    override fun getTagsPagingSource(searchQuery: String?): PagingSource<Int, TagEntity> {
        return TagsPagingSource(
            dbQuery = dbQuery,
            searchQuery = searchQuery,
            ioDispatcher = ioDispatcher
        )
    }

    /**
     * Assign a tag to a specific wallet address
     * Using the new address_tags table for direct address-level tagging
     */
    override suspend fun assignTagToAddress(addressId: String, tagId: String) {
        withContext(ioDispatcher) {
            
            // Check if the association already exists
            val exists = dbQuery.checkAddressTagAssociationExists(
                address_id = addressId,
                tag_id = tagId
            ).executeAsOneOrNull() != null
            
            if (exists) {
                return@withContext
            }
            
            try {
                val now = localDateTimeToMillis(localDateTimeNow())
                
                // Insert directly into address_tags table
                dbQuery.insertAddressTag(
                    address_id = addressId,
                    tag_id = tagId,
                    created_at = now
                )
                
                
                // Verify the assignment worked
                val hasTag = isTagAssignedToAddress(addressId, tagId)
            } catch (e: Exception) {
                println("ERROR: Failed to assign tag $tagId to address $addressId: ${e.message}")
                e.printStackTrace()
                // Re-throw the exception to propagate it to the caller
                throw e
            }
        }
    }

    /**
     * Remove a tag from a specific wallet address
     * Using the new address_tags table for direct address-level tagging
     */
    override suspend fun removeTagFromAddress(addressId: String, tagId: String) {
        withContext(ioDispatcher) {
            
            try {
                // Remove directly from address_tags table
                dbQuery.removeTagFromAddress(
                    address_id = addressId,
                    tag_id = tagId
                )
                
                
                // Verify the removal worked
                val stillHasTag = isTagAssignedToAddress(addressId, tagId)
            } catch (e: Exception) {
                println("ERROR: Failed to remove tag $tagId from address $addressId: ${e.message}")
                e.printStackTrace()
                // Re-throw the exception to propagate it to the caller
                throw e
            }
        }
    }

    /**
     * Get all address IDs that have a specific tag
     * Using the new address_tags table for direct address-level querying
     */
    override suspend fun getAddressIdsWithTag(tagId: String): List<String> {
        return withContext(ioDispatcher) {
            try {
                // Get address IDs directly from address_tags table
                val addressIds = dbQuery.getAddressIdsWithTag(tagId)
                    .executeAsList()
                
                return@withContext addressIds
            } catch (e: Exception) {
                println("ERROR: Failed to get addresses with tag $tagId: ${e.message}")
                e.printStackTrace()
                // Re-throw the exception to propagate it to the caller
                throw e
            }
        }
    }

    /**
     * Batch assign a single tag to multiple addresses (transaction)
     * Using the new address_tags table for direct address-level batch operations
     */
    override suspend fun batchAssignTagToAddresses(tagId: String, addressIds: List<String>) {
        withContext(ioDispatcher) {
            
            if (addressIds.isEmpty()) {
                return@withContext
            }
            
            try {
                val now = localDateTimeToMillis(localDateTimeNow())
                
                // Use transaction for better performance
                dbQuery.transaction {
                    for (addressId in addressIds) {
                        // Skip if association already exists
                        val exists = dbQuery.checkAddressTagAssociationExists(
                            address_id = addressId,
                            tag_id = tagId
                        ).executeAsOneOrNull() != null
                        
                        if (exists) {
                            continue
                        }
                        
                        // Insert directly into address_tags table
                        dbQuery.insertAddressTag(
                            address_id = addressId,
                            tag_id = tagId,
                            created_at = now
                        )
                    }
                }
                
                // Verify the number of addresses with this tag after assignment
                val taggedAddressIds = getAddressIdsWithTag(tagId)
                
            } catch (e: Exception) {
                println("ERROR: Failed to batch assign tag $tagId to addresses: ${e.message}")
                e.printStackTrace()
                // Re-throw the exception to propagate it to the caller
                throw e
            }
        }
    }

    /**
     * Get all tags for a specific wallet address
     * Using the new address_tags table for direct address-level querying
     */
    override suspend fun getTagsForAddress(addressId: String): List<TagEntity> {
        return withContext(ioDispatcher) {
            try {
                // Get tags directly from address_tags table joined with tags table
                val results = dbQuery.getTagsForAddress(addressId)
                    .executeAsList()
                    .map {
                        TagEntity(
                            id = it.id,
                            name = it.name,
                            color = it.color,
                            textColor = it.text_color,
                            isDeleted = it.is_deleted,
                            createdAt = Instant.fromEpochMilliseconds(it.created_at),
                            updatedAt = Instant.fromEpochMilliseconds(it.updated_at)
                        )
                    }
                
                return@withContext results
            } catch (e: Exception) {
                println("ERROR: Failed to get tags for address $addressId: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }

    /**
     * Get all active tags with their address count
     * Using the new address_tags table for accurate address counts
     */
    override suspend fun getActiveTagsWithAddressCount(): List<TagEntity> {
        return withContext(ioDispatcher) {
            try {
                // Get tags with address counts directly from joined tables
                val tags = dbQuery.getActiveTagsWithContactCount(
                    query = "",
                    limit = Long.MAX_VALUE,
                    offset = 0L
                )
                    .executeAsList()
                    .map { row ->
                        TagEntity(
                            id = row.id,
                            name = row.name,
                            color = row.color,
                            textColor = row.text_color,
                            isDeleted = row.is_deleted,
                            createdAt = Instant.fromEpochMilliseconds(row.created_at),
                            updatedAt = Instant.fromEpochMilliseconds(row.updated_at),
                            addressCount = row.contact_count?.toInt() ?: 0
                        )
                    }
                
                // Get address counts for each tag using the new address_tags table
                val result = tags.map { tag ->
                    // Count addresses with this tag
                    val addressCount = dbQuery.countAddressesByTagId(tag.id)
                        .executeAsOne()
                        .toInt()
                    
                    // Return the tag with accurate address count
                    tag.copy(
                        addressCount = addressCount
                    )
                }
                
                return@withContext result
            } catch (e: Exception) {
                println("ERROR: Failed to get active tags with address count: ${e.message}")
                e.printStackTrace()
                
                // Fallback to basic implementation if there's an error
                val tags = withContext(ioDispatcher) { 
                    dbQuery.getActiveTags()
                        .executeAsList()
                        .map { row ->
                            TagEntity(
                                id = row.id,
                                name = row.name,
                                color = row.color,
                                textColor = row.text_color,
                                isDeleted = row.is_deleted,
                                createdAt = Instant.fromEpochMilliseconds(row.created_at),
                                updatedAt = Instant.fromEpochMilliseconds(row.updated_at)
                            )
                        }
                }
                
                // For each tag, get its address count
                return@withContext tags.map { tag ->
                    val addressCount = dbQuery.countAddressesByTagId(tag.id)
                        .executeAsOne()
                        .toInt()
                    
                    // Return the tag with address count
                    tag.copy(
                        addressCount = addressCount
                    )
                }
            }
        }
    }
    
    /**
     * Check if a tag is assigned to a specific address
     * Uses the address_tags table for direct address-level querying
     */
    override suspend fun isTagAssignedToAddress(addressId: String, tagId: String): Boolean {
        return withContext(ioDispatcher) {
            try {
                val result = dbQuery.checkAddressTagAssociationExists(
                    address_id = addressId,
                    tag_id = tagId
                ).executeAsOneOrNull()
                
                val isAssigned = result != null
                return@withContext isAssigned
            } catch (e: Exception) {
                println("ERROR: Failed to check if tag $tagId is assigned to address $addressId: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }
    
    /**
     * Migrate existing contact tags to address tags
     * This is useful for ensuring backward compatibility
     */
    override suspend fun migrateContactTagsToAddressTags() {
        withContext(ioDispatcher) {
            try {
                
                // Get all contact-tag associations
                val contactTagPairs = dbQuery.getAllContactTagPairs()
                    .executeAsList()
                
                
                val now = localDateTimeToMillis(localDateTimeNow())
                
                // Process each contact-tag pair
                for (pair in contactTagPairs) {
                    val contactId = pair.contact_id
                    val tagId = pair.tag_id
                    
                    // Get all addresses for this contact
                    val addresses = dbQuery.getWalletAddressesForContact(contactId, 999, 0)
                        .executeAsList()
                    
                    
                    // Add tag to each address
                    for (address in addresses) {
                        try {
                            // Skip if association already exists
                            val exists = isTagAssignedToAddress(address.id, tagId)
                            
                            if (!exists) {
                                // Insert into address_tags table
                                dbQuery.insertAddressTag(
                                    address_id = address.id,
                                    tag_id = tagId,
                                    created_at = now
                                )
                            } else {
                            }
                        } catch (e: Exception) {
                            println("ERROR: Failed to migrate tag $tagId to address ${address.id}: ${e.message}")
                            e.printStackTrace()
                            // Continue with other addresses despite error
                        }
                    }
                }
                
            } catch (e: Exception) {
                println("ERROR: Failed to migrate contact tags to address tags: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }

    override suspend fun migrateAddressTagsToContactTags() {
        // Để trống vì chúng ta không thực hiện migration thực sự
        println("[MIGRATION] Skipping migration since we're using clean install approach")
    }

    override suspend fun clearUserTags(): Boolean = withContext(ioDispatcher) {
        return@withContext try {
            dbQuery.clearUserTags()
            true
        } catch (e: Exception) {
            false
        }
    }
}