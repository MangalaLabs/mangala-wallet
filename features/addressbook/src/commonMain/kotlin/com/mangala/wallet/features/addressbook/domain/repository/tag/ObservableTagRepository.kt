package com.mangala.wallet.features.addressbook.domain.repository.tag

import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import kotlinx.coroutines.flow.Flow

/**
 * Extension of TagRepository with observable Flow support for real-time updates
 */
interface ObservableTagRepository : TagRepository {
    /**
     * Observe contacts assigned to a specific tag
     * @param tagId The ID of the tag to observe
     * @return Flow emitting list of contact IDs whenever they change
     */
    fun observeContactIdsWithTag(tagId: String): Flow<List<String>>
    
    /**
     * Observe all active tags with their contact counts
     * @return Flow emitting list of tags with updated counts
     */
    fun observeActiveTagsWithContactCount(): Flow<List<Pair<TagEntity, Int>>>
    
    /**
     * Observe tags assigned to a specific contact
     * @param contactId The ID of the contact to observe
     * @return Flow emitting list of tags whenever they change
     */
    fun observeTagsForContact(contactId: String): Flow<List<TagEntity>>
    
    /**
     * Force emit updates for a specific tag (for debugging)
     * @param tagId The ID of the tag to force update
     */
    suspend fun forceUpdateTag(tagId: String)
}