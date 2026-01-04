package com.mangala.wallet.features.send_base.sendcontact

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.presentation.components.ContactItemWithPrivacy
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel
import com.mangala.wallet.features.addressbook.presentation.contact.recent.EmptyContactState
import com.mangala.wallet.features.addressbook.presentation.contact.recent.NoContactSearchResultsState
import com.mangala.wallet.ui.MangalaPullToRefreshBox
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.utils.isNotNullOrBlank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendContactsContent(
    contactsPaging: LazyPagingItems<ContactGroupedByAlphabetUiModel>,
    searchQuery: String?,
    onSearchQueryChange: (String) -> Unit,
    onContactClick: (ContactModel) -> Unit,
    privacyModeEnabled: Boolean = false,
) {
    // Placeholder object for skeleton loading
    val placeholderContactObject = ContactWithMultipleBlockchainsModel(
        contactId = "",
        contactName = "",
        primaryWalletAddress = "",
        primaryWalletAddressId = "",
        primaryWalletAlias = "",
        primaryWalletSensitive = false,
        primaryBlockchainName = "",
        primaryBlockchainSymbol = "",
        primaryBlockchainIcon = "",
        primaryBlockChainColor = "",
        isFavorite = false,
        addedTime = 0L,
        isSensitive = false,
        avatar = null
    )

    MangalaPullToRefreshBox(
        isRefreshing = contactsPaging.loadState.refresh is LoadState.Loading,
        onRefresh = contactsPaging::refresh
    ) {
        MaxSizeColumn(
            modifier = Modifier
                .padding(
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.half,
                )
        ) {
            // Search bar
            SearchBar(
                query = searchQuery ?: "",
                onQueryChange = onSearchQueryChange,
                placeholder = "Search contacts to send"
            )

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            // Contacts list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
            ) {
                when {
                    // No search results
                    searchQuery.isNotNullOrBlank() &&
                            contactsPaging.itemCount == 0 &&
                            contactsPaging.loadState.refresh is LoadState.NotLoading -> item {
                        NoContactSearchResultsState()
                    }

                    // Empty contacts (no search)
                    searchQuery.isNullOrBlank() &&
                            contactsPaging.itemCount == 0 &&
                            contactsPaging.loadState.refresh is LoadState.NotLoading -> item {
                        EmptyContactState(onAddContact = { /* No add contact in send flow */ })
                    }

                    // Show skeleton loading
                    contactsPaging.loadState.refresh is LoadState.Loading && contactsPaging.itemCount == 0 ->
                        items(10) {
                            ContactItemWithPrivacy(
                                contact = placeholderContactObject,
                                isLoading = true,
                                privacyModeEnabled = privacyModeEnabled,
                                onContactClick = {},
                                onQrCodeClick = {},
                                onStarClick = {}
                            )
                        }

                    else ->
                        for (index in 0 until contactsPaging.itemCount) {
                            contactsPaging.peek(index)?.let { item ->
                                when (item) {
                                    is ContactGroupedByAlphabetUiModel.AlphabetHeader ->
                                        stickyHeader(key = item.alphabet) {
                                            Text(
                                                text = item.alphabet,
                                                style = MangalaTypography.Size14Medium(),
                                                color = MaterialTheme.mangalaColors.textSecondary,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = Dimensions.Padding.half)
                                            )
                                        }

                                    is ContactGroupedByAlphabetUiModel.ContactItem ->
                                        item(key = item.contact.contactId) {
                                            (contactsPaging[index] as? ContactGroupedByAlphabetUiModel.ContactItem)?.contact?.let { contact ->
                                                ContactItemWithPrivacy(
                                                    contact = contact,
                                                    privacyModeEnabled = privacyModeEnabled,
                                                    onContactClick = onContactClick,
                                                )
                                            }
                                        }
                                }
                            }
                        }
                }

                // Load more indicator
                if (contactsPaging.loadState.append is LoadState.Loading) {
                    item(key = "contacts_load_more") {
                        MaxWidthBox(
                            contentAlignment = androidx.compose.ui.Alignment.Center,
                        ) {
                            MangalaCircularProgressIndicator(
                                color = MaterialTheme.mangalaColors.iconPrimary,
                                size = 24.dp,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }
        }
    }
}