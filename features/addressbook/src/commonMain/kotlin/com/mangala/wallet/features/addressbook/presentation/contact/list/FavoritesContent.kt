package com.mangala.wallet.features.addressbook.presentation.contact.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.InfoCircle
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.UnfavoriteStar
import com.mangala.wallet.features.addressbook.presentation.components.ContactItemWithPrivacy
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.features.addressbook.presentation.contact.recent.EmptyFavoritesState
import com.mangala.wallet.features.addressbook.presentation.contact.recent.NoFavoriteContactSearchResultsState
import com.mangala.wallet.ui.MangalaPullToRefreshBox
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MangalaSwipeRevealContainer
import com.mangala.wallet.ui.component.MaxHeightColumn
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.rememberSwipeRevealState
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.utils.isNotNullOrBlank
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    favoritesPaging: LazyPagingItems<ContactWithMultipleBlockchainsModel>,
    searchQuery: String?,
    onSearchQueryChange: (String) -> Unit,
    onContactClick: (ContactModel) -> Unit,
    onConfirmRemoveFromFavorites: (ContactModel) -> Unit,
    onClickContactDetail: (ContactModel) -> Unit,
    onQrCodeClick: (ContactModel) -> Unit,
    onClickAddFavorite: () -> Unit,
    privacyModeEnabled: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullToRefreshState()
    // Track manual refresh state
    var isManualRefreshing by remember { mutableStateOf(false) }

    // Update manual refresh state when load state changes
    LaunchedEffect(favoritesPaging.loadState.refresh) {
        if (favoritesPaging.loadState.refresh !is LoadState.Loading) {
            isManualRefreshing = false
        }
    }
    
    // Global state to manage which item is currently revealed (only one at a time)
    var currentlyRevealedContactId by remember { mutableStateOf<String?>(null) }
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
        isFavorite = true,
        addedTime = 0L,
        isSensitive = false,
        avatar = null
    )
    val isRefreshing = remember {
        derivedStateOf {
            isManualRefreshing && favoritesPaging.loadState.refresh == LoadState.Loading
        }
    }

    MangalaPullToRefreshBox(
        isRefreshing = isRefreshing.value,
        state = pullRefreshState,
        onRefresh = {
            favoritesPaging.refresh()
            isManualRefreshing = true
            scope.launch {
                delay(1000)
                if (isActive && isManualRefreshing && favoritesPaging.loadState.refresh != LoadState.Loading) {
                    isManualRefreshing = false
                    pullRefreshState.animateToHidden()
                }
            }
        }
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
            // Search bar
            SearchBar(
                query = searchQuery ?: "",
                onQueryChange = onSearchQueryChange,
                placeholder = "Search your contacts"
            )

            Spacer(modifier = Modifier.height(Spacing.BASE))

            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
            ) {
                when {
                    // No search results
                    searchQuery.isNotNullOrBlank() &&
                            favoritesPaging.itemCount == 0 &&
                            favoritesPaging.loadState.refresh is LoadState.NotLoading -> item {
                        NoFavoriteContactSearchResultsState()
                    }

                    // Empty favorites (no search)
                    searchQuery.isNullOrBlank() &&
                            favoritesPaging.itemCount == 0 &&
                            favoritesPaging.loadState.refresh is LoadState.NotLoading -> item {
                        EmptyFavoritesState(onAddFavorite = onClickAddFavorite)
                    }

                    // Show skeleton loading
                    favoritesPaging.loadState.refresh is LoadState.Loading && favoritesPaging.itemCount == 0 ->
                        items(10) {
                            ContactItemWithPrivacy(
                                contact = placeholderContactObject,
                                isLoading = true,
                                privacyModeEnabled = privacyModeEnabled,
                                onContactClick = {},
                                onQrCodeClick = {}
                            )
                        }

                    else ->
                        items(
                            count = favoritesPaging.itemCount,
                            key = { index -> 
                                // Use index-based key to prevent duplicates when same contact appears multiple times
                                favoritesPaging[index]?.let { contact ->
                                    "${contact.contactId}_idx$index"
                                } ?: "favorite_placeholder_$index"
                            }
                        ) { index ->
                            favoritesPaging[index]?.let { contact ->
                                val contactModel = remember { contact.toContactModel() }

                                val scope = rememberCoroutineScope()
                                var revealedWidth by remember { mutableStateOf(Float.MAX_VALUE) }
                                val swipeRevealState = rememberSwipeRevealState(revealedWidth = revealedWidth)

                                // Monitor this item's reveal state and update global state
                                LaunchedEffect(swipeRevealState.offset) {
                                    val isRevealed = swipeRevealState.offset > 0f
                                    if (isRevealed && currentlyRevealedContactId != contact.contactId) {
                                        // This item is now revealed, update global state
                                        currentlyRevealedContactId = contact.contactId
                                    } else if (!isRevealed && currentlyRevealedContactId == contact.contactId) {
                                        // This item is now closed, clear global state
                                        currentlyRevealedContactId = null
                                    }
                                }

                                // Close this item if another item becomes revealed
                                LaunchedEffect(currentlyRevealedContactId) {
                                    if (currentlyRevealedContactId != null &&
                                        currentlyRevealedContactId != contact.contactId &&
                                        swipeRevealState.offset > 0f
                                    ) {
                                        // Another item is revealed and this one is open, close this one
                                        swipeRevealState.snapTo(0)
                                    }
                                }

                                val onClickUnfavorite: () -> Unit = remember(contactModel) {
                                    {
                                        scope.launch {
                                            // First, animate the swipe back to closed state
                                            swipeRevealState.animateTo(0)
                                            currentlyRevealedContactId = null
                                        }
                                        onConfirmRemoveFromFavorites(contactModel)
                                    }
                                }

                                val onClickDetailAction = remember(contactModel) {
                                    {
                                        scope.launch {
                                            swipeRevealState.animateTo(0)
                                            currentlyRevealedContactId = null
                                        }
                                        onClickContactDetail(contactModel)
                                    }
                                }

                                MangalaSwipeRevealContainer(
                                    state = swipeRevealState,
                                    revealedWidth = revealedWidth,
                                    revealContent = {
                                        Row(
                                            modifier = Modifier
                                                .width(IntrinsicSize.Max)
                                                .graphicsLayer {
                                                    revealedWidth = size.width
                                                }
                                                .padding(start = Dimensions.Padding.default),
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Unfavorite action button (icon above label)
                                            MaxHeightColumn(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(
                                                        shape = RoundedCornerShape(
                                                            topStart = CornerRadius.Small,
                                                            bottomStart = CornerRadius.Small
                                                        )
                                                    )
                                                    .clickable(onClick = onClickUnfavorite)
                                                    .background(color = MaterialTheme.mangalaColors.bgSwipeAction)
                                                    .padding(horizontal = Dimensions.Padding.default),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(Spacing.XTINY, Alignment.CenterVertically)
                                            ) {
                                                Icon(
                                                    imageVector = ContactIcon.UnfavoriteStar,
                                                    contentDescription = "Remove from favorites",
                                                    tint = MaterialTheme.mangalaColors.iconPrimary,
                                                    modifier = Modifier.size(Dimensions.IconSize)
                                                )

                                                Text(
                                                    text = "Unfavorite",
                                                    style = MangalaTypography.Size10Medium(),
                                                    color = MaterialTheme.mangalaColors.textPrimary
                                                )
                                            }

                                            // Contact detail action button (icon above label)
                                            MaxHeightColumn(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(
                                                        shape = RoundedCornerShape(
                                                            topEnd = CornerRadius.Small,
                                                            bottomEnd = CornerRadius.Small
                                                        )
                                                    )
                                                    .clickable(onClick = onClickDetailAction)
                                                    .background(color = MaterialTheme.mangalaColors.bgSwipeAction)
                                                    .padding(horizontal = Dimensions.Padding.default),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(Spacing.XTINY, Alignment.CenterVertically)
                                            ) {
                                                Icon(
                                                    imageVector = MangalaWalletPack.InfoCircle,
                                                    contentDescription = "Contact Details",
                                                    tint = MaterialTheme.mangalaColors.iconPrimary,
                                                    modifier = Modifier.size(Dimensions.IconSize)
                                                )
                                                Text(
                                                    text = "Details",
                                                    style = MangalaTypography.Size10Medium(),
                                                    color = MaterialTheme.mangalaColors.textPrimary
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    ContactItemWithPrivacy(
                                        contact = contact,
                                        privacyModeEnabled = privacyModeEnabled,
                                        onContactClick = onContactClick,
                                        onQrCodeClick = onQrCodeClick,
                                    )
                                }
                            }
                        }
                }

                // Load more indicator
                if (favoritesPaging.loadState.append is LoadState.Loading) {
                    item(key = "favorites_load_more") {
                        MaxWidthBox(
                            contentAlignment = Alignment.Center,
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
