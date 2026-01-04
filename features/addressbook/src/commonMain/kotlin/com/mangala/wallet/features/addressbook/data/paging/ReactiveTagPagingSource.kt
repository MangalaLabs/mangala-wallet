package com.mangala.wallet.features.addressbook.data.paging

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.database.AddressBookDatabaseQueries
import com.mangala.wallet.features.addressbook.domain.model.TagChangeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant

/**
 * A PagingSource that automatically invalidates when tag data changes
 */
class ReactiveTagPagingSource(
    private val dbQuery: AddressBookDatabaseQueries,
    private val searchQuery: String?,
    tagChanges: Flow<TagChangeEvent>
) : PagingSource<Int, TagEntity>() {
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    init {
        // Listen to tag changes and invalidate when they occur
        tagChanges
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
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TagEntity> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize
            
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
                    createdAt = kotlinx.datetime.Instant.fromEpochMilliseconds(tag.created_at),
                    updatedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(tag.updated_at),
                    contactCount = tag.contact_count?.toInt() ?: 0,
                    addressCount = null // Not provided by this query
                )
            }
            
            LoadResult.Page(
                data = tagEntities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (tagEntities.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, TagEntity>): Int? {
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