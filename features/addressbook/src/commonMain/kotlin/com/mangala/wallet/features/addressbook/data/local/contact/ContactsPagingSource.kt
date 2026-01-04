package com.mangala.wallet.features.addressbook.data.local.contact

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.database.AddressBookDatabaseQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ContactsPagingSource(
    private val dbQuery: AddressBookDatabaseQueries,
    private val searchQuery: String?,
    private val tagIds: List<String>? = null,
    private val checkTagId: String? = null,
    private val isFavoriteOnly: Boolean,
    private val ioDispatcher: CoroutineDispatcher,
) : PagingSource<Int, ContactModel>() {

    override suspend fun load(
        params: PagingSourceLoadParams<Int>,
    ): PagingSourceLoadResult<Int, ContactModel> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize

            withContext(ioDispatcher) {
                val result = dbQuery.filterContacts(
                    query = searchQuery ?: "",
                    onlyFavorites = if (isFavoriteOnly) 1L else 0L,
                    hasTagFilters = if (tagIds.isNullOrEmpty()) 0L else 1L,
                    tagIds = tagIds ?: emptyList(),
                    checkTagId = checkTagId,
                    hasGroupFilters = 0L,
                    groupIds = emptyList(),
                    hasBlockchainFilters = 0L,
                    blockchainIds = emptyList(),
                    sortOrder = "name_asc",
                    limit = pageSize.toLong(),
                    offset = offset.toLong()
                ).executeAsList()

                val contactModels = result.map { contact ->
                    ContactModel(
                        contactId = contact.contact_id,
                        contactName = contact.contact_name,
                        walletAddress = contact.wallet_address.toString(),
                        walletAlias = contact.wallet_alias.toString(),
                        walletAddressId = contact.wallet_address_id.toString(),
                        blockchainName = contact.blockchain_name.toString(),
                        blockchainSymbol = contact.blockchain_symbol.toString(),
                        blockchainIcon = contact.blockchain_icon.toString(),
                        walletSensitive = contact.is_sensitive,
                        isFavorite = contact.is_favorite == 1L,
                        blockChainColor = contact.blockchain_color.toString(),
                        isSensitive = contact.isSensitive == true,
                        avatar = contact.avatar,
                        tagId = if (contact.is_in_checked_tag == 1L && checkTagId != null) checkTagId else null,
                        privacyDisplayMode = DisplayMode.fromString(contact.privacy_display_mode)
                    )
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (result.size < pageSize) null else page + 1

                PagingSourceLoadResultPage(
                    data = contactModels,
                    prevKey = prevKey,
                    nextKey = nextKey
                ) as PagingSourceLoadResult<Int, ContactModel>
            }
        } catch (e: Exception) {
            PagingSourceLoadResultError<Int, ContactModel>(e)
        }
    }

    override fun getRefreshKey(state: app.cash.paging.PagingState<Int, ContactModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}