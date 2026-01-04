package com.mangala.wallet.features.addressbook.presentation.contact.recent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.compose.LazyPagingItems
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactRecentTransactionModel
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.ui.MangalaPullToRefreshBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.utils.isNotNullOrBlank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun recentTransactionContent(
    favoriteContacts: List<ContactModel>?,
    recentTransactionsPaging: LazyPagingItems<ContactRecentTransactionModel>,
    searchQuery: String?,
    onSearchQueryChange: (String) -> Unit,
    onClickSendToken: () -> Unit,
    onContactClick: (ContactModel) -> Unit,
    onQrCodeClick: (ContactRecentTransactionModel) -> Unit,
    onTransactionClick: (ContactRecentTransactionModel) -> Unit,
    navigateToTransactionDetail: (ContactRecentTransactionModel) -> Unit,
    navigateToFavoriteContacts: () -> Unit,
    navigateToImportAccount: () -> Unit,
    navigateToCreateAccount: () -> Unit,
    hasAnyImportedAccount: Boolean,
    privacyModeEnabled: Boolean = false,
) {
    var isShowFavorite by remember { mutableStateOf(true) }
    val (currentlyRevealedTransactionId, onRevealedTransactionChange) = remember { mutableStateOf<String?>(null) }
    
    // Track manual refresh state
    var isManualRefreshing by remember { mutableStateOf(false) }

    // Update manual refresh state when load state changes
    LaunchedEffect(Unit) {
        snapshotFlow {
            recentTransactionsPaging.loadState.refresh
        }.collect { refreshState ->
            if (refreshState != LoadStateLoading) {
                isManualRefreshing = false
            }
        }
    }

    MangalaPullToRefreshBox(
        isRefreshing = isManualRefreshing && recentTransactionsPaging.loadState.refresh == LoadStateLoading,
        onRefresh = {
            isManualRefreshing = true
            recentTransactionsPaging.refresh()
        },
    ) {
        MaxSizeColumn(
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .padding(
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.half,
                )
        ) {
            SearchBar(
                query = searchQuery ?: "",
                onQueryChange = onSearchQueryChange,
                placeholder = "Search your transaction"
            )

            Spacer(modifier = Modifier.height(Spacing.BASE))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.TINY),
            ) {
                favoritesSection(
                    favorites = favoriteContacts,
                    onToggleShowHideClick = {
                        isShowFavorite = !isShowFavorite
                    },
                    isShowFavorite = isShowFavorite,
                    onContactClick = onContactClick,
                    navigateToFavoriteContacts = navigateToFavoriteContacts,
                )

                recentTransactionsSection(
                    transactionsPaging = recentTransactionsPaging,
                    isSearching = searchQuery.isNotNullOrBlank(),
                    onTransactionClick = onTransactionClick,
                    onClickSendToken = onClickSendToken,
                    navigateToTransactionDetail = navigateToTransactionDetail,
                    onClickQrCode = onQrCodeClick,
                    privacyModeEnabled = privacyModeEnabled,
                    currentlyRevealedTransactionId = currentlyRevealedTransactionId,
                    onRevealedTransactionChange = onRevealedTransactionChange,
                    navigateToCreateAccount = navigateToCreateAccount,
                    navigateToImportAccount = navigateToImportAccount,
                    hasAnyImportedAccount = hasAnyImportedAccount,
                )


                if (searchQuery.isNotNullOrBlank() && recentTransactionsPaging.itemCount == 0 && recentTransactionsPaging.loadState.refresh is LoadStateNotLoading)
                    item {
                        NoTransactionSearchResultsState()
                    }
            }
        }
    }
}