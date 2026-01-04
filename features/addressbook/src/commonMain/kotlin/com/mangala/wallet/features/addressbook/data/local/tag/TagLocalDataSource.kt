package com.mangala.wallet.features.addressbook.data.local.tag

import app.cash.paging.PagingSource
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity

/**
 * Interface for Tag local data source operations
 * Updated to support address-specific tagging with the new address_tags table
 */
interface TagLocalDataSource {
    /**
     * Get all active (non-deleted) tags
     */
    suspend fun getActiveTags(): List<TagEntity>

    /**
     * Get a tag by its ID
     */
    suspend fun getTagById(id: String): TagEntity?

    /**
     * Find a tag by name
     */
    suspend fun findTagByName(name: String): TagEntity?

    /**
     * Create a new tag
     */
    suspend fun createTag(tag: TagEntity): TagEntity

    /**
     * Update an existing tag
     */
    suspend fun updateTag(tag: TagEntity): TagEntity

    /**
     * Mark a tag as deleted (soft delete)
     */
    suspend fun markTagAsDeleted(tagId: String)

    /**
     * Permanently delete a tag and all its relationships (hard delete)
     * This removes the tag from the database and all contact/address associations
     */
    suspend fun hardDeleteTag(tagId: String): Boolean

    /**
     * Restore a previously deleted tag
     */
    suspend fun restoreTag(tagId: String, color: String, textColor: String? = null): TagEntity

    /**
     * Assign a tag to a contact
     * @param tagId The ID of the tag to assign
     * @param contactId The ID of the contact to tag
     */
    suspend fun assignTagToContact(tagId: String, contactId: String)

    /**
     * Remove a tag from a contact
     * @param tagId The ID of the tag to remove
     * @param contactId The ID of the contact to untag
     */
    suspend fun removeTagFromContact(tagId: String, contactId: String)

    /**
     * Get all tags for a contact (including soft-deleted)
     */
    suspend fun getTagsForContact(contactId: String): List<TagEntity>

    /**
     * Get only active (non-deleted) tags for a contact
     */
    suspend fun getActiveTagsForContact(contactId: String): List<TagEntity>

    /**
     * Get all contact IDs that have a specific tag
     */
    suspend fun getContactIdsWithTag(tagId: String): List<String>
    
    /**
     * Get contact IDs with pagination for mobile optimization
     */
    suspend fun getContactIdsWithTag(tagId: String, limit: Int, offset: Int): List<String>
    
    /**
     * Get contact count for a specific tag
     */
    suspend fun getContactCountWithTag(tagId: String): Int

    /**
     * Batch assign multiple tags to a single contact (transaction)
     */
    suspend fun batchAssignTagsToContact(contactId: String, tagIds: List<String>)

    /**
     * Batch assign a single tag to multiple contacts (transaction)
     */
    suspend fun batchAssignTagToContacts(tagId: String, contactIds: List<String>)

    /**
     * Search for active tags matching the query
     */
    suspend fun searchTags(query: String): List<TagEntity>

    /**
     * Get most frequently used tags with their usage count
     */
    suspend fun getMostUsedTags(limit: Int): List<Pair<TagEntity, Int>>

    /**
     * Get all active tags with their contact count
     */
    suspend fun getActiveTagsWithContactCount(): List<TagEntity>

    /**
     * Get tags paging source for efficient loading with search support
     * Replaces manual loading with proper Cash App Paging
     */
    fun getTagsPagingSource(searchQuery: String? = null): PagingSource<Int, TagEntity>

    /**
     * Assign a tag to a specific wallet address
     * Uses the new address_tags table for direct address-level tagging
     */
    suspend fun assignTagToAddress(addressId: String, tagId: String)

    /**
     * Remove a tag from a specific wallet address
     * Uses the new address_tags table for direct address-level tagging
     */
    suspend fun removeTagFromAddress(addressId: String, tagId: String)

    /**
     * Get all address IDs that have a specific tag
     * Uses the new address_tags table for direct address-level querying
     */
    suspend fun getAddressIdsWithTag(tagId: String): List<String>

    /**
     * Batch assign a single tag to multiple addresses (transaction)
     * Uses the new address_tags table for direct address-level batch operations
     */
    suspend fun batchAssignTagToAddresses(tagId: String, addressIds: List<String>)

    /**
     * Get all tags for a specific wallet address
     * Uses the new address_tags table for direct address-level querying
     */
    suspend fun getTagsForAddress(addressId: String): List<TagEntity>

    /**
     * Get all active tags with their address count
     * Uses the new address_tags table for accurate address counts
     */
    suspend fun getActiveTagsWithAddressCount(): List<TagEntity>
    
    /**
     * Check if a tag is assigned to a specific address
     * Uses the address_tags table for direct address-level querying
     */
    suspend fun isTagAssignedToAddress(addressId: String, tagId: String): Boolean
    
    /**
     * Migrate existing contact tags to address tags
     * This is useful for ensuring backward compatibility
     */
    suspend fun migrateContactTagsToAddressTags()

    /**
     * Migrate existing address tags to contact tags
     * This is useful for switching from address-based to contact-based tagging
     */
    suspend fun migrateAddressTagsToContactTags()

    /**
     * Clear user-created tags for wallet reset
     * This will preserve default tags but remove user-created ones
     * @return true if clearing was successful
     */
    suspend fun clearUserTags(): Boolean
}