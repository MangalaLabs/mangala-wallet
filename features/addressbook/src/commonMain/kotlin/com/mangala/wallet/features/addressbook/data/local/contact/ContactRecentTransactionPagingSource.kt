package com.mangala.wallet.features.addressbook.data.local.contact

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.sqldelight.Query
import com.mangala.wallet.features.addressbook.database.AddressBookDatabaseQueries
import com.mangala.wallet.features.addressbook.database.SelectRecentContactsWithSearch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class ContactRecentTransactionPagingSource(
    private val dbQuery: AddressBookDatabaseQueries,
    private val searchQuery: String?,
    private val statuses: List<String>,
    private val ioDispatcher: CoroutineDispatcher
) : PagingSource<Int, SelectRecentContactsWithSearch>(), Query.Listener {

    private var currentQuery: Query<Long>? by Delegates.observable(null) { _, old, new ->
        old?.removeListener(this)
        new?.addListener(this)
    }

    init {
        registerInvalidatedCallback {
            currentQuery?.removeListener(this)
            currentQuery = null
        }
    }

    override suspend fun load(
        params: PagingSourceLoadParams<Int>
    ): PagingSourceLoadResult<Int, SelectRecentContactsWithSearch> {
        currentQuery = dbQuery.countTransactions()
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize

            withContext(ioDispatcher) {
                val result = dbQuery
                    .selectRecentContactsWithSearch(
                        statuses = statuses,
                        searchQuery = searchQuery,
                        limit = pageSize.toLong(),
                        offset = offset.toLong()
                    ).executeAsList()

                val nextKey = if (result.size < pageSize) null else page + 1

                PagingSourceLoadResultPage(
                    data = result,
                    prevKey = null,
                    nextKey = nextKey
                ) as PagingSourceLoadResult<Int, SelectRecentContactsWithSearch>
            }
        } catch (e: Exception) {
            PagingSourceLoadResultError<Int, SelectRecentContactsWithSearch>(e)
        }
    }

    override fun getRefreshKey(state: app.cash.paging.PagingState<Int, SelectRecentContactsWithSearch>): Int? =
        null

    override fun queryResultsChanged() {
        invalidate()
    }
}