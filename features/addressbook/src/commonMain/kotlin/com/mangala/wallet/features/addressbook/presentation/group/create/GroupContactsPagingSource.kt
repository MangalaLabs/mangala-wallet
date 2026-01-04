package com.mangala.wallet.features.addressbook.presentation.group.create

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactsWithWalletAddressPaginatedUseCase

/**
 * Custom PagingSource for loading contacts filtered by blockchain
 * This source:
 * - Loads GroupWallet data page by page
 * - Filters by blockchain ID
 * - Supports search functionality
 * - Excludes already selected wallet IDs
 */
class GroupContactsPagingSource(
    private val blockchainId: String,
    private val searchQuery: String?,
    private val getContactsWithWalletAddressPaginatedUseCase: GetContactsWithWalletAddressPaginatedUseCase,
    private val existingSelectedWalletIds: Set<String> = emptySet()
) : PagingSource<Int, GroupWallet>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GroupWallet> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            // Load data from use case
            val groupWallets = getContactsWithWalletAddressPaginatedUseCase.getPagedList(
                blockchainId = blockchainId,
                page = page,
                pageSize = pageSize,
                searchQuery = searchQuery ?: ""
            )

            // Filter out already selected wallets if needed
            val filteredWallets = if (existingSelectedWalletIds.isEmpty()) {
                groupWallets
            } else {
                groupWallets.filter { wallet ->
                    !existingSelectedWalletIds.contains(wallet.walletId)
                }
            }

            // Calculate next page
            val nextKey = if (groupWallets.size < pageSize) {
                null // No more pages
            } else {
                page + 1
            }

            LoadResult.Page(
                data = filteredWallets,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GroupWallet>): Int? {
        // Try to find the page that contains the anchor position
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}