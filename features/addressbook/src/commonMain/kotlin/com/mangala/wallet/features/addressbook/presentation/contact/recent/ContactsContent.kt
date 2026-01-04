package com.mangala.wallet.features.addressbook.presentation.contact.recent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Send
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Trash
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.presentation.components.ContactItemWithPrivacy
import com.mangala.wallet.features.addressbook.presentation.components.DeleteConfirmationDialog
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel
import com.mangala.wallet.features.addressbook.presentation.contact.list.ContactListKeyGenerator
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthProvider
import com.mangala.wallet.features.addressbook.presentation.security.SecureActionId
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthPolicyProvider
import com.mangala.wallet.features.addressbook.presentation.security.ContextAwareSecureAuthPolicyProvider
import com.mangala.wallet.features.addressbook.presentation.common.contactsWithAlphabetHeaders
import org.koin.compose.koinInject
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
fun ContactsContent(
    contactsPaging: LazyPagingItems<ContactGroupedByAlphabetUiModel>,
    searchQuery: String?,
    onSearchQueryChange: (String) -> Unit,
    onAddContact: () -> Unit,
    onContactClick: (ContactModel) -> Unit,
    toggleFavorite: (ContactModel) -> Unit,
    onConfirmDeleteContact: (String) -> Unit,
    clearLocalChanges: () -> Unit,
    onQrCodeClick: (ContactModel) -> Unit,
    onClickSend: (ContactModel) -> Unit,
    privacyModeEnabled: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullToRefreshState()
    // Track manual refresh state
    var isManualRefreshing by remember { mutableStateOf(false) }
    
    // Clean up key generator when composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            ContactListKeyGenerator.clear()
        }
    }
    
    // Update manual refresh state when load state changes
    LaunchedEffect(contactsPaging.loadState) {
        if (contactsPaging.loadState.refresh !is LoadState.Loading) {
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

    MangalaPullToRefreshBox(
        isRefreshing = isManualRefreshing && contactsPaging.loadState.refresh is LoadState.Loading,
        state = pullRefreshState,
        onRefresh = {
            isManualRefreshing = true
            clearLocalChanges()
            contactsPaging.refresh()
            scope.launch {
                delay(1000)
                if (isActive && isManualRefreshing && contactsPaging.loadState.refresh != LoadState.Loading) {
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

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            // Clear key generator when list refreshes
            LaunchedEffect(contactsPaging.loadState.refresh) {
                if (contactsPaging.loadState.refresh is LoadState.Loading) {
                    ContactListKeyGenerator.clear()
                }
            }

            // Profile header
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
                        EmptyContactState(onAddContact = onAddContact)
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

                    else -> {
                        contactsWithAlphabetHeaders(
                            items = contactsPaging,
                            headerContent = { alphabet ->
                                Text(
                                    text = alphabet,
                                    style = MangalaTypography.Size14Medium(),
                                    color = MaterialTheme.mangalaColors.textSecondary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.mangalaColors.bg)
                                        .padding(top = Dimensions.Padding.half)
                                )
                            },
                            itemContent = { contact ->
                                val contactModel = remember(contact) { contact.toContactModel() }
                                val scope = rememberCoroutineScope()
                                var revealedWidth by remember { mutableStateOf(Float.MAX_VALUE) }
                                val swipeRevealState = rememberSwipeRevealState(revealedWidth = revealedWidth)
                                var showDeleteDialog by remember { mutableStateOf(false) }

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

                                        val onClickDelete: () -> Unit = remember(contact) {
                                            {
                                                scope.launch {
                                                    // First, animate the swipe back to closed state
                                                    swipeRevealState.animateTo(0)
                                                    currentlyRevealedContactId = null
                                                }
                                                showDeleteDialog = true
                                            }
                                        }

                                        val onClickSendAction = remember(contactModel) {
                                            {
                                                scope.launch {
                                                    swipeRevealState.animateTo(0)
                                                    currentlyRevealedContactId = null
                                                }
                                                onClickSend(contactModel) // Navigate to send/add transaction
                                            }
                                        }

                                        // Delete confirmation dialog
                                        if (showDeleteDialog) {
                                            val secureActionHandler = SecureAuthProvider.current
                                            val policyProvider = koinInject<SecureAuthPolicyProvider>() as? ContextAwareSecureAuthPolicyProvider
                                            
                                            // Preload contact security level when dialog is shown
                                            LaunchedEffect(contact.contactId) {
                                                policyProvider?.preloadContactSecurity(contact.contactId)
                                            }
                                            
                                            DeleteConfirmationDialog(
                                                title = "Delete contact?",
                                                message = "Are you sure you want to delete ${contact.contactName}? This action cannot be undone.",
                                                onConfirm = {
                                                    showDeleteDialog = false
                                                    // Set contact context before running secure action
                                                    policyProvider?.setContactContext(contact.contactId)
                                                    secureActionHandler.runSecureActionForId(
                                                        actionId = SecureActionId.DeleteContact,
                                                        onSuccess = {
                                                            policyProvider?.clearContactContext()
                                                            scope.launch {
                                                                swipeRevealState.animateTo(0)
                                                                currentlyRevealedContactId = null
                                                            }
                                                            onConfirmDeleteContact(contact.contactId)
                                                        },
                                                        onCancel = {
                                                            policyProvider?.clearContactContext()
                                                        }
                                                    )
                                                },
                                                onDismiss = {
                                                    showDeleteDialog = false
                                                }
                                            )
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
                                                    // Delete action button (icon above label)
                                                    MaxHeightColumn(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clip(
                                                                shape = RoundedCornerShape(
                                                                    topStart = CornerRadius.Small,
                                                                    bottomStart = CornerRadius.Small
                                                                )
                                                            )
                                                            .clickable(onClick = onClickDelete)
                                                            .background(color = MaterialTheme.mangalaColors.bgSwipeAction)
                                                            .padding(horizontal = Dimensions.Padding.default),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.spacedBy(Spacing.XTINY, Alignment.CenterVertically)
                                                    ) {
                                                        Icon(
                                                            imageVector = MangalaWalletPack.Trash,
                                                            contentDescription = "Delete contact",
                                                            tint = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                                                            modifier = Modifier.size(Dimensions.IconSize)
                                                        )

                                                        Text(
                                                            text = "Delete",
                                                            style = MangalaTypography.Size10Medium(),
                                                            color = MaterialTheme.mangalaColors.buttonDestructiveContainer
                                                        )
                                                    }

                                                    // Send action button (icon above label)
                                                    MaxHeightColumn(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clip(
                                                                shape = RoundedCornerShape(
                                                                    topEnd = CornerRadius.Small,
                                                                    bottomEnd = CornerRadius.Small
                                                                )
                                                            )
                                                            .clickable(onClick = onClickSendAction)
                                                            .background(color = MaterialTheme.mangalaColors.bgSwipeAction)
                                                            .padding(horizontal = Dimensions.Padding.default),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.spacedBy(Spacing.XTINY, Alignment.CenterVertically)
                                                    ) {
                                                        Icon(
                                                            imageVector = MangalaWalletPack.Send,
                                                            contentDescription = "Send",
                                                            tint = MaterialTheme.mangalaColors.textLink,
                                                            modifier = Modifier.size(Dimensions.IconSize)
                                                        )
                                                        Text(
                                                            text = "Send",
                                                            style = MangalaTypography.Size10Medium(),
                                                            color = MaterialTheme.mangalaColors.textLink
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
                                                onStarClick = toggleFavorite
                                            )
                                        }
                            }
                        )
                    }
                }

                // Load more indicator
                if (contactsPaging.loadState.append is LoadState.Loading) {
                    item(key = "contacts_load_more") {
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