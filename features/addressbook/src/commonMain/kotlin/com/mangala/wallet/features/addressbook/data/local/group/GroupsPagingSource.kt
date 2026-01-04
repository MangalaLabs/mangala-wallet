package com.mangala.wallet.features.addressbook.data.local.group

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.database.AddressBookDatabaseQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GroupsPagingSource(
    private val dbQuery: AddressBookDatabaseQueries,
    private val searchQuery: String?,
    private val ioDispatcher: CoroutineDispatcher,
) : PagingSource<Int, GroupModel>() {

    override suspend fun load(
        params: PagingSourceLoadParams<Int>,
    ): PagingSourceLoadResult<Int, GroupModel> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize

            withContext(ioDispatcher) {
                val result = dbQuery.filterGroups(
                    query = searchQuery ?: "",
                    limit = pageSize.toLong(),
                    offset = offset.toLong()
                ).executeAsList()

                val groupModels = result.map { group ->
                    GroupModel(
                        id = group.group_id,
                        name = group.group_name,
                        description = group.group_description,
                        icon = group.group_icon,
                        color = group.group_color,
                        privacyLevel = group.privacy_level ?: "PUBLIC",
                        securityLevel = group.security_level ?: "NORMAL",
                        createdAt = group.created_at,
                        updatedAt = group.updated_at,
                        mainBlockchainId = group.main_blockchain_id,
                        mainBlockchainName = group.main_blockchain_name,
                        mainBlockchainSymbol = group.main_blockchain_symbol,
                        mainBlockchainIcon = group.main_blockchain_icon,
                        walletAddressCount = group.wallet_address_count.toInt()
                    )
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (result.size < pageSize) null else page + 1

                PagingSourceLoadResultPage(
                    data = groupModels,
                    prevKey = prevKey,
                    nextKey = nextKey
                ) as PagingSourceLoadResult<Int, GroupModel>
            }
        } catch (e: Exception) {
            PagingSourceLoadResultError<Int, GroupModel>(e)
        }
    }

    override fun getRefreshKey(state: app.cash.paging.PagingState<Int, GroupModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}