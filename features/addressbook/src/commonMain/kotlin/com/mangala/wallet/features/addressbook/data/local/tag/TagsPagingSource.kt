package com.mangala.wallet.features.addressbook.data.local.tag

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.database.AddressBookDatabaseQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

class TagsPagingSource(
    private val dbQuery: AddressBookDatabaseQueries,
    private val searchQuery: String?,
    private val ioDispatcher: CoroutineDispatcher,
) : PagingSource<Int, TagEntity>() {

    override suspend fun load(
        params: PagingSourceLoadParams<Int>,
    ): PagingSourceLoadResult<Int, TagEntity> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize

            withContext(ioDispatcher) {
                val result = dbQuery.getActiveTagsWithContactCount(
                    query = searchQuery ?: "",
                    limit = pageSize.toLong(),
                    offset = offset.toLong()
                ).executeAsList()

                val tagEntities = result.map { tag ->
                    TagEntity(
                        id = tag.id,
                        name = tag.name,
                        color = tag.color,
                        textColor = tag.text_color,
                        icon = tag.icon,
                        isDeleted = tag.is_deleted,
                        createdAt = Instant.fromEpochMilliseconds(tag.created_at),
                        updatedAt = Instant.fromEpochMilliseconds(tag.updated_at),
                        contactCount = tag.contact_count.toInt(),
                        addressCount = 0 // We're using contact count for now, address count can be added later if needed
                    )
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (result.size < pageSize) null else page + 1

                PagingSourceLoadResultPage(
                    data = tagEntities,
                    prevKey = prevKey,
                    nextKey = nextKey
                ) as PagingSourceLoadResult<Int, TagEntity>
            }
        } catch (e: Exception) {
            PagingSourceLoadResultError<Int, TagEntity>(e)
        }
    }

    override fun getRefreshKey(state: app.cash.paging.PagingState<Int, TagEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}