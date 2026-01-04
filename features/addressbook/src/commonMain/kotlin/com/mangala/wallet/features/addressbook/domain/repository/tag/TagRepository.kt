package com.mangala.wallet.features.addressbook.domain.repository.tag

import app.cash.paging.PagingData
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.model.TagChangeEvent
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface cho việc quản lý Tag và liên kết với Contact và Address
 */
interface TagRepository {
    /**
     * Flow that emits events when tag data changes
     */
    val tagChanges: Flow<TagChangeEvent>
    // Original methods that work with contacts
    suspend fun getActiveTags(): List<TagEntity>
    suspend fun getTagById(id: String): TagEntity?
    suspend fun findTagByName(name: String): TagEntity?
    suspend fun createTag(tag: TagEntity): TagEntity
    suspend fun updateTag(tag: TagEntity): TagEntity
    suspend fun softDeleteTag(tagId: String): List<String> // Returns affected contact IDs
    suspend fun hardDeleteTag(tagId: String): Boolean // Permanently delete tag and all relationships
    suspend fun restoreTag(tagId: String, color: String, textColor: String? = null): TagEntity
    suspend fun assignTagToContact(tagId: String, contactId: String)
    suspend fun removeTagFromContact(tagId: String, contactId: String)
    suspend fun getTagsForContact(contactId: String): List<TagEntity>
    suspend fun getActiveTagsForContact(contactId: String): List<TagEntity>
    suspend fun getContactIdsWithTag(tagId: String): List<String>
    suspend fun getMostUsedTags(limit: Int): List<Pair<TagEntity, Int>>
    suspend fun searchTags(query: String): List<TagEntity>
    suspend fun batchAssignTagsToContact(contactId: String, tagIds: List<String>)
    suspend fun batchAssignTagToContacts(tagId: String, contactIds: List<String>)
    suspend fun getActiveTagsWithContactCount(): List<TagEntity>
    
    /**
     * Get paginated tags with search functionality (replaces getActiveTagsWithContactCount for UI)
     * @param searchQuery Optional search query to filter tags (null/empty = get all)
     * @return Flow of PagingData for TagEntity with contact counts
     */
    fun getPaginatedTags(
        searchQuery: String? = null
    ): Flow<PagingData<TagEntity>>
    
    // NEW: Pagination methods for mobile optimization
    suspend fun getContactIdsWithTag(tagId: String, limit: Int, offset: Int): List<String>

    // New methods that work with addresses directly
    suspend fun assignTagToAddress(addressId: String, tagId: String)
    suspend fun removeTagFromAddress(addressId: String, tagId: String)
    suspend fun getAddressIdsWithTag(tagId: String): List<String>
    suspend fun batchAssignTagToAddresses(tagId: String, addressIds: List<String>)
    suspend fun getTagsForAddress(addressId: String): List<TagEntity>
    suspend fun getActiveTagsWithAddressCount(): List<TagEntity>
    
    /**
     * Migrate existing contact tags to address tags
     * This is useful for ensuring backward compatibility
     */
    suspend fun migrateContactTagsToAddressTags()
    
    /**
     * Check if a tag is assigned to a specific address
     */
    suspend fun isTagAssignedToAddress(addressId: String, tagId: String): Boolean
    
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