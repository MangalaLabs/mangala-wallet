package com.mangala.wallet.features.addressbook.data.repository.tag

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.mangala.wallet.features.addressbook.data.local.tag.TagLocalDataSource
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.repository.tag.ObservableTagRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.domain.model.TagChangeEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.paging.ReactiveTagPagingSource
import com.mangala.wallet.features.addressbook.util.ChangeEventEmitter
import com.mangala.wallet.features.addressbook.util.DebouncedChangeEventEmitter
import kotlinx.coroutines.launch

/**
 * Implementation of ObservableTagRepository with real-time Flow support
 * Uses only contact_tags table for consistency between screens
 */
class TagRepositoryImpl(
    private val localDataSource: TagLocalDataSource,
    private val walletAddressRepository: WalletAddressRepository,
    private val databaseWrapper: AddressBookDatabaseWrapper
) : ObservableTagRepository {
    
    companion object {
        private const val TAGS_PAGE_SIZE = 20
    }
    
    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Thêm cache cho các truy vấn thường xuyên
    private val contactIdCache = mutableMapOf<String, String>()
    private val addressesForContactCache = mutableMapOf<String, List<String>>()
    
    // Shared Flows for real-time updates
    // Using replay=1 to ensure late subscribers get the latest value
    // Using extraBufferCapacity to handle rapid updates without suspending
    private val _tagContactsFlow = MutableSharedFlow<Pair<String, List<String>>>(
        replay = 1,
        extraBufferCapacity = 10
    )
    private val _contactTagsFlow = MutableSharedFlow<Pair<String, List<TagEntity>>>(
        replay = 1,
        extraBufferCapacity = 10
    )
    private val _activeTagsFlow = MutableSharedFlow<List<Pair<TagEntity, Int>>>(
        replay = 1,
        extraBufferCapacity = 10
    )
    
    // Tag change events flow
    private val _tagChangesFlow = MutableSharedFlow<TagChangeEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    
    // Expose as public read-only flow
    override val tagChanges: Flow<TagChangeEvent> = _tagChangesFlow.asSharedFlow()
    
    private val tagEventEmitter = ChangeEventEmitter(_tagChangesFlow, "TagRepository")
    private val debouncedTagEventEmitter = DebouncedChangeEventEmitter(_tagChangesFlow, repositoryScope, "TagRepository")
    
    // Helper function to emit tag updates
    private suspend fun emitTagContactsUpdate(tagId: String) {
        val contactIds = localDataSource.getContactIdsWithTag(tagId)
        // Check subscription count BEFORE emission
        val subCountBefore = _tagContactsFlow.subscriptionCount.value
        // Get replay cache before emission
        val replayCacheBefore = _tagContactsFlow.replayCache
        // Try to emit and check if there are any active collectors
        val emitted = _tagContactsFlow.tryEmit(tagId to contactIds)
        if (emitted) {
        } else {
            _tagContactsFlow.emit(tagId to contactIds)
        }
        
        // Check subscription count AFTER emission
        val subCountAfter = _tagContactsFlow.subscriptionCount.value
        // Get replay cache after emission
        val replayCacheAfter = _tagContactsFlow.replayCache
        if (subCountAfter == 0) {
        }
    }
    
    // Helper function to emit contact tags update
    private suspend fun emitContactTagsUpdate(contactId: String) {
        val tags = localDataSource.getActiveTagsForContact(contactId)
        _contactTagsFlow.emit(contactId to tags)
    }
    
    // Helper function to emit active tags update
    private suspend fun emitActiveTagsUpdate() {
        val tagsWithCount = localDataSource.getActiveTagsWithContactCount()
            .map { tag -> tag to localDataSource.getContactIdsWithTag(tag.id).size }
        _activeTagsFlow.emit(tagsWithCount)
    }
    
    // Hàm lấy contactId từ addressId
    private suspend fun getContactIdFromAddressId(addressId: String): String? {
        // Kiểm tra cache trước
        contactIdCache[addressId]?.let { return it }
        
        // Nếu không có trong cache, truy vấn database
        val address = walletAddressRepository.getWalletAddressById(addressId)
        val contactId = address.contactId
        
        // Lưu vào cache nếu có kết quả
        if (contactId.isNotEmpty()) {
            contactIdCache[addressId] = contactId
        }
        
        return contactId
    }

    // Hàm lấy danh sách contactIds từ addressIds (không trùng lặp)
    private suspend fun getUniqueContactIdsFromAddressIds(addressIds: List<String>): List<String> {
        val contactIds = mutableSetOf<String>()
        for (addressId in addressIds) {
            val contactId = getContactIdFromAddressId(addressId)
            if (contactId != null) {
                contactIds.add(contactId)
            }
        }
        return contactIds.toList()
    }

    // Hàm lấy tất cả addressIds của một contactId
    private suspend fun getAllAddressIdsFromContactId(contactId: String): List<String> {
        // Kiểm tra cache trước
        addressesForContactCache[contactId]?.let { return it }
        
        // Nếu không có trong cache, truy vấn database
        val addresses = walletAddressRepository.getWalletAddressesForContact(contactId)
        val addressIds = addresses.map { it.id }
        
        // Lưu vào cache nếu có kết quả
        if (addressIds.isNotEmpty()) {
            addressesForContactCache[contactId] = addressIds
        }
        
        return addressIds
    }
    
    // Hàm xóa cache khi có thay đổi
    private fun clearCaches() {
        contactIdCache.clear()
        addressesForContactCache.clear()
    }

    override suspend fun getActiveTags(): List<TagEntity> {
        return localDataSource.getActiveTags()
    }

    override suspend fun getTagById(id: String): TagEntity? {
        return localDataSource.getTagById(id)
    }

    override suspend fun findTagByName(name: String): TagEntity? {
        return localDataSource.findTagByName(name)
    }

    override suspend fun createTag(tag: TagEntity): TagEntity {
        val createdTag = localDataSource.createTag(tag)
        // Emit update for active tags
        emitActiveTagsUpdate()
        // Emit tag change event
        tagEventEmitter.emit(
            TagChangeEvent.Created,
            "Notified tags changed after creating tag: ${createdTag.id}"
        )
        return createdTag
    }

    override suspend fun updateTag(tag: TagEntity): TagEntity {
        val updatedTag = localDataSource.updateTag(tag)
        // Emit updates
        emitTagContactsUpdate(tag.id)
        emitActiveTagsUpdate()
        // Emit tag change event
        tagEventEmitter.emit(
            TagChangeEvent.Updated,
            "Notified tags changed after updating tag: ${tag.id}"
        )
        return updatedTag
    }

    override suspend fun softDeleteTag(tagId: String): List<String> {
        // Get affected contact IDs for UI refresh before deletion
        val affectedContactIds = localDataSource.getContactIdsWithTag(tagId)

        // Mark tag as deleted - no need to cache info as we're keeping the relationship
        localDataSource.markTagAsDeleted(tagId)
        
        // Emit updates
        emitTagContactsUpdate(tagId)
        emitActiveTagsUpdate()
        for (contactId in affectedContactIds) {
            emitContactTagsUpdate(contactId)
        }
        
        // Emit tag change event
        tagEventEmitter.emit(
            TagChangeEvent.Deleted,
            "Notified tags changed after soft deleting tag: $tagId"
        )

        return affectedContactIds
    }

    override suspend fun hardDeleteTag(tagId: String): Boolean {
        val result = localDataSource.hardDeleteTag(tagId)
        
        if (result) {
            // Emit tag change event
            tagEventEmitter.emit(
                TagChangeEvent.Deleted,
                "Notified tags changed after hard deleting tag: $tagId"
            )
        }
        
        return result
    }

    override suspend fun restoreTag(tagId: String, color: String, textColor: String?): TagEntity {
        return localDataSource.restoreTag(tagId, color, textColor)
    }

    override suspend fun assignTagToContact(tagId: String, contactId: String) {
        localDataSource.assignTagToContact(tagId, contactId)

        clearCaches() // Xóa cache khi có thay đổi

        // Emit real-time updates
        emitTagContactsUpdate(tagId)
        emitContactTagsUpdate(contactId)
        emitActiveTagsUpdate()

        // Emit tag change event to trigger list refresh with debouncing
        debouncedTagEventEmitter.emitDebounced(
            TagChangeEvent.Updated,
            "Notified tags changed after assigning contact $contactId to tag $tagId"
        )
    }

    override suspend fun removeTagFromContact(tagId: String, contactId: String) {
        localDataSource.removeTagFromContact(tagId, contactId)

        clearCaches() // Xóa cache khi có thay đổi

        // Emit real-time updates
        emitTagContactsUpdate(tagId)
        emitContactTagsUpdate(contactId)
        emitActiveTagsUpdate()

        debouncedTagEventEmitter.emitDebounced(
            TagChangeEvent.Updated,
            "Notified tags changed after removing contact $contactId from tag $tagId"
        )
    }

    override suspend fun getTagsForContact(contactId: String): List<TagEntity> {
        return localDataSource.getTagsForContact(contactId)
    }

    override suspend fun getActiveTagsForContact(contactId: String): List<TagEntity> {
        return localDataSource.getActiveTagsForContact(contactId)
    }

    override suspend fun getContactIdsWithTag(tagId: String): List<String> {
        return localDataSource.getContactIdsWithTag(tagId)
    }

    override suspend fun getContactIdsWithTag(tagId: String, limit: Int, offset: Int): List<String> {
        return localDataSource.getContactIdsWithTag(tagId, limit, offset)
    }

    override suspend fun getMostUsedTags(limit: Int): List<Pair<TagEntity, Int>> {
        return localDataSource.getMostUsedTags(limit)
    }

    override suspend fun searchTags(query: String): List<TagEntity> {
        return localDataSource.searchTags(query)
    }

    override suspend fun batchAssignTagsToContact(contactId: String, tagIds: List<String>) {
        localDataSource.batchAssignTagsToContact(contactId, tagIds)
        clearCaches() // Xóa cache khi có thay đổi
        
        // Emit updates for all affected tags
        for (tagId in tagIds) {
            emitTagContactsUpdate(tagId)
        }
        emitContactTagsUpdate(contactId)
        emitActiveTagsUpdate()
        
        // Emit tag change event to trigger list refresh
        if (tagIds.isNotEmpty()) {
            // Use immediate emit for batch operations to ensure UI updates
            repositoryScope.launch {
                tagEventEmitter.emit(
                    TagChangeEvent.Updated,
                    "Notified tags changed after batch assigning ${tagIds.size} tags to contact $contactId"
                )
            }
        }
    }

    override suspend fun batchAssignTagToContacts(tagId: String, contactIds: List<String>) {
        localDataSource.batchAssignTagToContacts(tagId, contactIds)
        clearCaches() // Xóa cache khi có thay đổi
        
        // Emit updates
        emitTagContactsUpdate(tagId)
        for (contactId in contactIds) {
            emitContactTagsUpdate(contactId)
        }
        emitActiveTagsUpdate()
        
        // Emit tag change event to trigger list refresh
        if (contactIds.isNotEmpty()) {
            // Use immediate emit for batch operations to ensure UI updates
            repositoryScope.launch {
                tagEventEmitter.emit(
                    TagChangeEvent.Updated,
                    "Notified tags changed after batch assigning tag $tagId to ${contactIds.size} contacts"
                )
            }
        }
    }

    override suspend fun getActiveTagsWithContactCount(): List<TagEntity> {
        return localDataSource.getActiveTagsWithContactCount()
    }

    override fun getPaginatedTags(
        searchQuery: String?
    ): Flow<PagingData<TagEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = TAGS_PAGE_SIZE,
                prefetchDistance = TAGS_PAGE_SIZE / 2,
                initialLoadSize = TAGS_PAGE_SIZE,
            ),
            pagingSourceFactory = {
                // Use ReactiveTagPagingSource that listens to changes
                ReactiveTagPagingSource(
                    dbQuery = databaseWrapper.database.addressBookDatabaseQueries,
                    searchQuery = searchQuery,
                    tagChanges = tagChanges
                )
            }
        ).flow
    }

    override suspend fun assignTagToAddress(addressId: String, tagId: String) {
        localDataSource.assignTagToAddress(addressId, tagId)
        clearCaches() // Xóa cache khi có thay đổi
    }
    
    override suspend fun removeTagFromAddress(addressId: String, tagId: String) {
        localDataSource.removeTagFromAddress(addressId, tagId)
        clearCaches() // Xóa cache khi có thay đổi
    }
    
    override suspend fun getAddressIdsWithTag(tagId: String): List<String> {
        return localDataSource.getAddressIdsWithTag(tagId)
    }
    
    override suspend fun batchAssignTagToAddresses(tagId: String, addressIds: List<String>) {
        localDataSource.batchAssignTagToAddresses(tagId, addressIds)
        clearCaches() // Xóa cache khi có thay đổi
    }
    
    override suspend fun getTagsForAddress(addressId: String): List<TagEntity> {
        return localDataSource.getTagsForAddress(addressId)
    }
    
    override suspend fun getActiveTagsWithAddressCount(): List<TagEntity> {
        return localDataSource.getActiveTagsWithAddressCount()
    }
    
    override suspend fun migrateContactTagsToAddressTags() {
        localDataSource.migrateContactTagsToAddressTags()
        clearCaches() // Xóa cache khi có thay đổi
    }
    
    override suspend fun isTagAssignedToAddress(addressId: String, tagId: String): Boolean {
        return localDataSource.isTagAssignedToAddress(addressId, tagId)
    }

    override suspend fun migrateAddressTagsToContactTags() {
        localDataSource.migrateAddressTagsToContactTags()
        clearCaches() // Xóa cache khi có thay đổi
    }
    
    // Observable interface methods
    override fun observeContactIdsWithTag(tagId: String): Flow<List<String>> {
        return _tagContactsFlow
            .asSharedFlow()
            .onStart {
                // Emit initial value when someone starts collecting
                emitTagContactsUpdate(tagId)
            }
            .filter { (id, _) -> 
                val matches = id == tagId
                if (matches) {
                } else if (id != null) {
                }
                matches
            }
            .map { (_, contacts) -> 
                contacts
            }
            // Use custom equality check for lists
            .distinctUntilChanged { old, new ->
                val oldSet = old.toSet()
                val newSet = new.toSet()
                val areEqual = oldSet == newSet
                
                if (!areEqual) {
                } else {
                }
                
                areEqual
            }
    }
    
    override fun observeActiveTagsWithContactCount(): Flow<List<Pair<TagEntity, Int>>> {
        return _activeTagsFlow.asSharedFlow()
    }
    
    override fun observeTagsForContact(contactId: String): Flow<List<TagEntity>> {
        return _contactTagsFlow
            .asSharedFlow()
            .map { (id, tags) -> if (id == contactId) tags else emptyList() }
            .distinctUntilChanged()
    }
    
    override suspend fun forceUpdateTag(tagId: String) {
        emitTagContactsUpdate(tagId)
        emitActiveTagsUpdate()
    }

    override suspend fun clearUserTags(): Boolean {
        return localDataSource.clearUserTags()
    }
}