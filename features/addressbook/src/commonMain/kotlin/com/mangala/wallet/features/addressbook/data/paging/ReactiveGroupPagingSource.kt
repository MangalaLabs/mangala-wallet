package com.mangala.wallet.features.addressbook.data.paging

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.database.AddressBookDatabaseQueries
import com.mangala.wallet.features.addressbook.domain.model.GroupChangeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * A PagingSource that automatically invalidates when group data changes
 */
class ReactiveGroupPagingSource(
    private val dbQuery: AddressBookDatabaseQueries,
    private val searchQuery: String?,
    groupChanges: Flow<GroupChangeEvent>
) : PagingSource<Int, GroupModel>() {
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    init {
        // Listen to group changes and invalidate when they occur
        groupChanges
            .debounce(100) // Debounce rapid changes
            .onEach {
                invalidate()
            }
            .launchIn(scope)
        
        // Cancel the scope when the PagingSource is invalidated to prevent memory leaks
        registerInvalidatedCallback {
            scope.cancel()
        }
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GroupModel> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize
            
            val result = if (searchQuery.isNullOrEmpty()) {
                dbQuery.filterGroups(
                    query = "",
                    limit = pageSize.toLong(),
                    offset = offset.toLong()
                ).executeAsList()
            } else {
                dbQuery.filterGroups(
                    query = searchQuery,
                    limit = pageSize.toLong(),
                    offset = offset.toLong()
                ).executeAsList()
            }
            
            val groupModels = result.map { group ->
                GroupModel(
                    id = group.group_id,
                    name = group.group_name,
                    description = group.group_description,
                    icon = group.group_icon,
                    color = group.group_color,
                    privacyLevel = group.privacy_level ?: "public",
                    securityLevel = group.security_level ?: "standard",
                    createdAt = group.created_at,
                    updatedAt = group.updated_at,
                    mainBlockchainId = group.main_blockchain_id,
                    mainBlockchainName = group.main_blockchain_name,
                    mainBlockchainSymbol = group.main_blockchain_symbol,
                    mainBlockchainIcon = group.main_blockchain_icon,
                    walletAddressCount = group.wallet_address_count.toInt()
                )
            }
            
            LoadResult.Page(
                data = groupModels,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (groupModels.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, GroupModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
    
    // Clean up resources when the PagingSource is no longer needed
    fun cleanup() {
        scope.cancel()
    }
}