package com.mangala.wallet.features.addressbook.data.repository.group

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.group.GroupLocalDataSource
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.GroupDetailModel
import com.mangala.wallet.features.addressbook.data.model.contact.PaginatedContactsResult
import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import com.mangala.wallet.features.addressbook.domain.model.GroupChangeEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.datetime.Clock
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.paging.ReactiveGroupPagingSource
import com.mangala.wallet.features.addressbook.util.ChangeEventEmitter
import com.mangala.wallet.features.addressbook.util.DebouncedChangeEventEmitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class GroupRepositoryImpl(
    private val localDataSource: GroupLocalDataSource,
    private val databaseWrapper: AddressBookDatabaseWrapper
) : GroupRepository {
    
    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Tạo một MutableSharedFlow để phát các thông báo khi group thay đổi
    private val _groupChangesFlow = MutableSharedFlow<GroupChangeEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    
    // Expose as public read-only flow
    override val groupChanges: Flow<GroupChangeEvent> = _groupChangesFlow.asSharedFlow()
    
    private val groupEventEmitter = ChangeEventEmitter(_groupChangesFlow, "GroupRepository")
    private val debouncedGroupEventEmitter = DebouncedChangeEventEmitter(_groupChangesFlow, repositoryScope, "GroupRepository")

    companion object {
        private const val GROUPS_PAGE_SIZE = 20
    }
    override suspend fun getGroupById(id: String): GroupEntity? {
        return localDataSource.getGroupById(id)
    }

    override suspend fun getGroupDetailById(id: String): GroupDetailModel? {
        val group = localDataSource.getGroupById(id) ?: return null
        return GroupDetailModel(
            group = group,
        )
    }

    override suspend fun getGroupModelById(id: String): GroupModel? {
        return localDataSource.getGroupModelById(id)
    }

    override suspend fun getAllGroups(): List<GroupModel> = localDataSource.getAllGroups()

    override fun getPaginatedGroups(
        searchQuery: String?
    ): Flow<PagingData<GroupModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = GROUPS_PAGE_SIZE,
                prefetchDistance = GROUPS_PAGE_SIZE / 2,
                initialLoadSize = GROUPS_PAGE_SIZE,
            ),
            pagingSourceFactory = {
                // Use ReactiveGroupPagingSource that listens to changes
                ReactiveGroupPagingSource(
                    dbQuery = databaseWrapper.database.addressBookDatabaseQueries,
                    searchQuery = searchQuery,
                    groupChanges = groupChanges
                )
            }
        ).flow
    }

    override suspend fun getGroupsByContactId(contactId: String): List<GroupEntity> {
        return localDataSource.getGroupsByContactId(contactId)
    }

    override fun getContactsByGroupId(
        groupId: String,
        limit: Int,
        offset: Int
    ): Flow<List<ContactEntity>> {
        return localDataSource.getContactsByGroupId(groupId, limit, offset)
    }

    override suspend fun insertGroup(group: GroupEntity): String {
        val now = Clock.System.now()
        val groupId = group.id.ifBlank { uuid4().toString() }

        val newGroup = group.copy(
            id = groupId,
            createdAt = now,
            updatedAt = now
        )

        val result = localDataSource.insertGroup(newGroup)

        groupEventEmitter.emit(
            GroupChangeEvent.Created,
            "Notified groups changed after inserting group: $result"
        )

        return result
    }

    override suspend fun updateGroup(group: GroupEntity): Boolean {
        val now = Clock.System.now()
        val updatedGroup = group.copy(updatedAt = now)

        val result = localDataSource.updateGroup(updatedGroup)
        
        // Thông báo sự thay đổi nếu cập nhật thành công
        if (result) {
            groupEventEmitter.emit(
                GroupChangeEvent.Updated,
                "Notified groups changed after updating group: ${group.id}"
            )
        }
        
        return result
    }

    override suspend fun deleteGroup(id: String): Boolean {
        val result = localDataSource.deleteGroup(id)
        
        // Thông báo sự thay đổi nếu xóa thành công
        if (result) {
            groupEventEmitter.emit(
                GroupChangeEvent.Deleted,
                "Notified groups changed after deleting group: $id"
            )
        }
        
        return result
    }
    
    override suspend fun findGroupByName(name: String): GroupEntity? {
        return localDataSource.findGroupByName(name)
    }

    override suspend fun addContactToGroup(
        contactId: String,
        groupId: String,
        walletAddressId: String
    ): Boolean {
        return try {
            val result = localDataSource.addContactToGroup(contactId, groupId, walletAddressId)

            // Emit group change event to trigger list refresh if addition was successful
            if (result) {
                debouncedGroupEventEmitter.emitDebounced(
                    GroupChangeEvent.Updated,
                    "Notified groups changed after adding contact $contactId to group $groupId"
                )
            }

            result
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun removeContactFromGroup(contactId: String, groupId: String): Boolean {
        return try {
            val result = localDataSource.removeContactFromGroup(contactId, groupId)
            // Emit group change event to trigger list refresh if removal was successful
            if (result) {
                debouncedGroupEventEmitter.emitDebounced(
                    GroupChangeEvent.Updated,
                    "Notified groups changed after removing contact $contactId from group $groupId"
                )
            }

            result
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun contactInGroup(contactId: String, groupId: String): Boolean {
        return localDataSource.contactInGroup(contactId, groupId)
    }

    override suspend fun countContactsByGroupId(groupId: String): Int {
        return localDataSource.countContactsByGroupId(groupId)
    }


    override suspend fun updateGroupMainBlockchain(
        groupId: String,
        blockchainId: String
    ): Boolean {
        return localDataSource.updateGroupMainBlockchain(groupId, blockchainId)
    }

    override suspend fun getContactAddressByGroupId(
        groupId: String,
        limit: Int,
        offset: Int
    ): Flow<List<ContactModel>> {
        return localDataSource.getContactAddressByGroupId(groupId, limit, offset)
    }
    
    override suspend fun getContactsWithAddressesByGroupId(
        groupId: String,
        limit: Int,
        offset: Int
    ): Flow<PaginatedContactsResult> {
        return localDataSource.getContactsWithAddressesByGroupId(groupId, limit, offset)
    }

    override suspend fun getGroupsByIds(ids: List<String>): List<GroupEntity> {
        TODO("Not yet implemented")
    }

    override fun observeGroups(): Flow<List<GroupEntity>> {
        TODO("Not yet implemented")
    }

    override fun observeGroupById(id: String): Flow<GroupEntity?> {
        TODO("Not yet implemented")
    }

    override suspend fun clearAllGroups(): Boolean {
        return localDataSource.clearAllGroups()
    }
}