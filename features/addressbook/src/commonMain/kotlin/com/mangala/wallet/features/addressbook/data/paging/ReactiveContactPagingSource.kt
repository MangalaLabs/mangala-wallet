package com.mangala.wallet.features.addressbook.data.paging

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.database.AddressBookDatabaseQueries
import com.mangala.wallet.features.addressbook.domain.model.ContactChangeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * A PagingSource that automatically invalidates when contact data changes
 */
@OptIn(FlowPreview::class)
class ReactiveContactPagingSource(
    private val dbQuery: AddressBookDatabaseQueries,
    private val searchQuery: String?,
    private val tagIds: List<String>? = null,
    private val isFavoriteOnly: Boolean = false,
    private val checkTagId: String? = null,
    contactChanges: Flow<ContactChangeEvent>
) : PagingSource<Int, ContactModel>() {
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    init {
        // Listen to contact changes and invalidate when they occur
        contactChanges
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
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContactModel> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize
            
            val result = dbQuery.filterContacts(
                query = searchQuery ?: "",
                onlyFavorites = if (isFavoriteOnly) 1L else 0L,
                hasTagFilters = if (tagIds.isNullOrEmpty()) 0L else 1L,
                tagIds = tagIds ?: emptyList(),
                hasGroupFilters = 0L,
                groupIds = emptyList(),
                hasBlockchainFilters = 0L,
                blockchainIds = emptyList(),
                sortOrder = "name_asc",
                limit = pageSize.toLong(),
                offset = offset.toLong(),
                checkTagId = checkTagId
            ).executeAsList()
            
            val contactModels = result.map { contact ->
                ContactModel(
                    contactId = contact.contact_id,
                    contactName = contact.contact_name,
                    walletAddress = contact.wallet_address?.toString() ?: "",
                    walletAlias = contact.wallet_alias?.toString() ?: "",
                    walletAddressId = contact.wallet_address_id?.toString() ?: "",
                    blockchainName = contact.blockchain_name?.toString() ?: "",
                    blockchainSymbol = contact.blockchain_symbol?.toString() ?: "",
                    blockchainIcon = contact.blockchain_icon?.toString() ?: "",
                    walletSensitive = contact.is_sensitive,
                    isFavorite = contact.is_favorite == 1L,
                    blockChainColor = contact.blockchain_color?.toString() ?: "",
                    isSensitive = contact.is_sensitive == true,
                    avatar = contact.avatar,
                    tagId = if (contact.is_in_checked_tag == 1L && checkTagId != null) checkTagId else null,
                    privacyDisplayMode = DisplayMode.fromString(contact.privacy_display_mode)
                )
            }
            
            LoadResult.Page(
                data = contactModels,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (contactModels.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, ContactModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
    
}