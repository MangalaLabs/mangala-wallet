package com.mangala.wallet.features.addressbook.presentation.group

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
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.presentation.components.DeleteConfirmationDialog
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.features.addressbook.presentation.group.model.GroupGroupedByAlphabetUiModel
import com.mangala.wallet.features.addressbook.presentation.common.AlphabetListKeyGenerator
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsContent(
    groupsPaging: LazyPagingItems<GroupGroupedByAlphabetUiModel>,
    searchQuery: String?,
    onSearchQueryChange: (String) -> Unit,
    onAddGroup: () -> Unit,
    onGroupClick: (GroupModel) -> Unit,
    onConfirmDeleteGroup: (String) -> Unit,
    onGroupSend: (GroupModel) -> Unit,
    clearGroupLocalChanges: () -> Unit,
) {
    // Track manual refresh state
    var isManualRefreshing by remember { mutableStateOf(false) }
    
    // Get key generator for groups
    val keyGenerator = remember { AlphabetListKeyGenerator.groupsKeyGenerator }
    
    // Clean up key generator when composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            keyGenerator.clear()
        }
    }
    
    // Update manual refresh state when load state changes
    LaunchedEffect(groupsPaging.loadState) {
        if (groupsPaging.loadState.refresh !is LoadState.Loading) {
            isManualRefreshing = false
        }
    }
    
    // Global state to manage which item is currently revealed (only one at a time)
    var currentlyRevealedGroupId by remember { mutableStateOf<String?>(null) }
    
    val placeholderGroupObject = remember {
        GroupModel(
            id = "",
            name = "",
            description = "",
            mainBlockchainId = "",
            icon = "",
            color = "",
            privacyLevel = "0",
            securityLevel = "0",
            createdAt = 0L,
            updatedAt = 0L,
            mainBlockchainName = "",
            mainBlockchainSymbol = "",
            mainBlockchainIcon = "",
            walletAddressCount = 0
        )
    }

    MangalaPullToRefreshBox(
        isRefreshing = isManualRefreshing && groupsPaging.loadState.refresh is LoadState.Loading,
        onRefresh = {
            isManualRefreshing = true
            clearGroupLocalChanges()
            groupsPaging.refresh()
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
                placeholder = "Search your groups"
            )

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            // Clear key generator when list refreshes
            LaunchedEffect(groupsPaging.loadState.refresh) {
                if (groupsPaging.loadState.refresh is LoadState.Loading) {
                    keyGenerator.clear()
                }
            }

            // Groups content
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
            ) {
                when {
                    // No search results
                    searchQuery.isNotNullOrBlank() &&
                            groupsPaging.itemCount == 0 &&
                            groupsPaging.loadState.refresh is LoadState.NotLoading -> item {
                        NoGroupSearchResultsState()
                    }

                    // Empty groups (no search)
                    searchQuery.isNullOrBlank() &&
                            groupsPaging.itemCount == 0 &&
                            groupsPaging.loadState.refresh is LoadState.NotLoading -> item {
                        EmptyGroupsState(onAddNewGroup = onAddGroup)
                    }

                    // Show skeleton loading
                    groupsPaging.loadState.refresh is LoadState.Loading && groupsPaging.itemCount == 0 ->
                        items(10) {
                            GroupItem(
                                group = placeholderGroupObject,
                                isLoading = true,
                                onGroupClick = {}
                            )
                        }

                    else -> {
                        for (index in 0 until groupsPaging.itemCount) {
                            groupsPaging.peek(index)?.let { item ->
                                // Get previous item ID for context-based key generation
                                val previousItemId = if (index > 0) {
                                    (groupsPaging.peek(index - 1) as? GroupGroupedByAlphabetUiModel.GroupItem)?.group?.id
                                } else null
                                
                                when (item) {
                                    is GroupGroupedByAlphabetUiModel.AlphabetHeader -> {
                                        val headerKey = keyGenerator.generateHeaderKey(item.alphabet, previousItemId)
                                        
                                        stickyHeader(key = headerKey) {
                                            Text(
                                                text = item.alphabet,
                                                style = MangalaTypography.Size14Medium(),
                                                color = MaterialTheme.mangalaColors.textSecondary,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(MaterialTheme.mangalaColors.bg)
                                                    .padding(top = Dimensions.Padding.half)
                                            )
                                        }
                                    }

                                    is GroupGroupedByAlphabetUiModel.GroupItem -> {
                                        val itemKey = keyGenerator.generateItemKey(item.group.id)
                                        
                                        item(key = itemKey) {
                                            (groupsPaging[index] as? GroupGroupedByAlphabetUiModel.GroupItem)?.group?.let { group ->
                                                val scope = rememberCoroutineScope()
                                                var revealedWidth by remember { mutableStateOf(Float.MAX_VALUE) }
                                                val swipeRevealState = rememberSwipeRevealState(revealedWidth = revealedWidth)
                                                var showDeleteDialog by remember { mutableStateOf(false) }

                                                // Monitor this item's reveal state and update global state
                                                LaunchedEffect(swipeRevealState.offset) {
                                                    val isRevealed = swipeRevealState.offset > 0f
                                                    if (isRevealed && currentlyRevealedGroupId != group.id) {
                                                        // This item is now revealed, update global state
                                                        currentlyRevealedGroupId = group.id
                                                    } else if (!isRevealed && currentlyRevealedGroupId == group.id) {
                                                        // This item is now closed, clear global state
                                                        currentlyRevealedGroupId = null
                                                    }
                                                }

                                                // Close this item if another item becomes revealed
                                                LaunchedEffect(currentlyRevealedGroupId) {
                                                    if (currentlyRevealedGroupId != null &&
                                                        currentlyRevealedGroupId != group.id &&
                                                        swipeRevealState.offset > 0f
                                                    ) {
                                                        // Another item is revealed and this one is open, close this one
                                                        swipeRevealState.snapTo(0)
                                                    }
                                                }

                                                val onClickDelete: () -> Unit = remember(group) {
                                                    {
                                                        scope.launch {
                                                            // First, animate the swipe back to closed state
                                                            swipeRevealState.animateTo(0)
                                                            currentlyRevealedGroupId = null
                                                        }
                                                        showDeleteDialog = true
                                                    }
                                                }

                                                val onClickGroupSend = remember(group) {
                                                    {
                                                        scope.launch {
                                                            swipeRevealState.animateTo(0)
                                                            currentlyRevealedGroupId = null
                                                        }
                                                        onGroupSend(group) // Navigate to group send
                                                    }
                                                }

                                                // Delete confirmation dialog
                                                if (showDeleteDialog) {
                                                    DeleteConfirmationDialog(
                                                        title = "Delete group?",
                                                        message = "Are you sure you want to delete ${group.name ?: "this group"}? This action cannot be undone.",
                                                        onConfirm = {
                                                            scope.launch {
                                                                swipeRevealState.animateTo(0)
                                                                currentlyRevealedGroupId = null
                                                            }
                                                            onConfirmDeleteGroup(group.id)
                                                            showDeleteDialog = false
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
                                                                    contentDescription = "Delete group",
                                                                    tint = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                                                                    modifier = Modifier.size(Dimensions.IconSize)
                                                                )

                                                                Text(
                                                                    text = "Delete",
                                                                    style = MangalaTypography.Size10Medium(),
                                                                    color = MaterialTheme.mangalaColors.buttonDestructiveContainer
                                                                )
                                                            }

                                                            // Group Send action button (icon above label)
                                                            MaxHeightColumn(
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .clip(
                                                                        shape = RoundedCornerShape(
                                                                            topEnd = CornerRadius.Small,
                                                                            bottomEnd = CornerRadius.Small
                                                                        )
                                                                    )
                                                                    .clickable(onClick = onClickGroupSend)
                                                                    .background(color = MaterialTheme.mangalaColors.bgSwipeAction)
                                                                    .padding(horizontal = Dimensions.Padding.default),
                                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                                verticalArrangement = Arrangement.spacedBy(Spacing.XTINY, Alignment.CenterVertically)
                                                            ) {
                                                                Icon(
                                                                    imageVector = MangalaWalletPack.Send,
                                                                    contentDescription = "Group Send",
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
                                                    GroupItem(
                                                        group = group,
                                                        onGroupClick = onGroupClick
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Load more indicator
                if (groupsPaging.loadState.append is LoadState.Loading) {
                    item(key = "groups_load_more") {
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