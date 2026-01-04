package com.mangala.wallet.features.addressbook.presentation.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.LazyPagingItems
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Trash
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.presentation.components.DeleteConfirmationDialog
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.features.addressbook.presentation.tag.model.TagGroupedByAlphabetUiModel
import com.mangala.wallet.features.addressbook.presentation.common.AlphabetListKeyGenerator
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.MangalaPullToRefreshBox
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MangalaSwipeRevealContainer
import com.mangala.wallet.ui.component.MaxHeightColumn
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.rememberSwipeRevealState
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.utils.isNotNullOrBlank
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagTabContent(
    tagsPaging: LazyPagingItems<TagGroupedByAlphabetUiModel>,
    searchQuery: String?,
    onSearchQueryChange: (String) -> Unit,
    onAddTag: () -> Unit,
    onTagClick: (TagEntity) -> Unit,
    onConfirmDeleteTag: (String) -> Unit,
    clearTagLocalChanges: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullToRefreshState()
    // Track manual refresh state
    var isManualRefreshing by remember { mutableStateOf(false) }

    // Get key generator for tags
    val keyGenerator = remember { AlphabetListKeyGenerator.tagsKeyGenerator }
    
    // Clean up key generator when composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            keyGenerator.clear()
        }
    }
    
    // Update manual refresh state when load state changes
    LaunchedEffect(tagsPaging.loadState) {
        if (tagsPaging.loadState.refresh !is LoadState.Loading) {
            isManualRefreshing = false
        }
    }
    
    // Global state to manage which item is currently revealed (only one at a time)
    var currentlyRevealedTagId by remember { mutableStateOf<String?>(null) }
    
    val placeholderTagObject = remember {
        TagEntity(
            id = "",
            name = "",
            color = "",
            textColor = "",
            icon = "",
            isDeleted = false,
            createdAt = kotlinx.datetime.Clock.System.now(),
            updatedAt = kotlinx.datetime.Clock.System.now(),
            contactCount = 0
        )
    }

    MangalaPullToRefreshBox(
        isRefreshing = isManualRefreshing && tagsPaging.loadState.refresh == LoadStateLoading,
        state = pullRefreshState,
        onRefresh = {
            isManualRefreshing = true
            clearTagLocalChanges()
            tagsPaging.refresh()
            scope.launch {
                delay(1000)
                if (isActive && isManualRefreshing && tagsPaging.loadState.refresh != LoadStateLoading) {
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
                placeholder = "Search your tags"
            )

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            // Clear key generator when list refreshes
            LaunchedEffect(tagsPaging.loadState.refresh) {
                if (tagsPaging.loadState.refresh is LoadState.Loading) {
                    keyGenerator.clear()
                }
            }

            // Tags content
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
            ) {
                when {
                    // No search results
                    searchQuery.isNotNullOrBlank() &&
                            tagsPaging.itemCount == 0 &&
                            tagsPaging.loadState.refresh is LoadState.NotLoading -> item {
                        NoTagSearchResultsState()
                    }

                    // Empty tags (no search)
                    searchQuery.isNullOrBlank() &&
                            tagsPaging.itemCount == 0 &&
                            tagsPaging.loadState.refresh is LoadState.NotLoading -> item {
                        EmptyTagsState(onAddNewTag = onAddTag)
                    }

                    // Show skeleton loading
                    tagsPaging.loadState.refresh is LoadState.Loading && tagsPaging.itemCount == 0 ->
                        items(10) {
                            TagItem(
                                tag = placeholderTagObject,
                                onTagClick = {},
                                isLoading = true
                            )
                        }

                    else -> {
                        for (index in 0 until tagsPaging.itemCount) {
                            tagsPaging.peek(index)?.let { item ->
                                // Get previous item ID for context-based key generation
                                val previousItemId = if (index > 0) {
                                    (tagsPaging.peek(index - 1) as? TagGroupedByAlphabetUiModel.TagItem)?.tag?.id
                                } else null
                                
                                when (item) {
                                    is TagGroupedByAlphabetUiModel.AlphabetHeader -> {
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

                                    is TagGroupedByAlphabetUiModel.TagItem -> {
                                        val itemKey = keyGenerator.generateItemKey(item.tag.id)
                                        
                                        item(key = itemKey) {
                                            (tagsPaging[index] as? TagGroupedByAlphabetUiModel.TagItem)?.tag?.let { tag ->
                                                val scope = rememberCoroutineScope()
                                                var revealedWidth by remember { mutableStateOf(Float.MAX_VALUE) }
                                                val swipeRevealState = rememberSwipeRevealState(revealedWidth = revealedWidth)
                                                var showDeleteDialog by remember { mutableStateOf(false) }

                                                // Monitor this item's reveal state and update global state
                                                LaunchedEffect(swipeRevealState.offset) {
                                                    val isRevealed = swipeRevealState.offset > 0f
                                                    if (isRevealed && currentlyRevealedTagId != tag.id) {
                                                        // This item is now revealed, update global state
                                                        currentlyRevealedTagId = tag.id
                                                    } else if (!isRevealed && currentlyRevealedTagId == tag.id) {
                                                        // This item is now closed, clear global state
                                                        currentlyRevealedTagId = null
                                                    }
                                                }

                                                // Close this item if another item becomes revealed
                                                LaunchedEffect(currentlyRevealedTagId) {
                                                    if (currentlyRevealedTagId != null &&
                                                        currentlyRevealedTagId != tag.id &&
                                                        swipeRevealState.offset > 0f
                                                    ) {
                                                        // Another item is revealed and this one is open, close this one
                                                        swipeRevealState.snapTo(0)
                                                    }
                                                }

                                                val onClickDelete: () -> Unit = remember(tag) {
                                                    {
                                                        scope.launch {
                                                            // First, animate the swipe back to closed state
                                                            swipeRevealState.animateTo(0)
                                                            currentlyRevealedTagId = null
                                                        }
                                                        showDeleteDialog = true
                                                    }
                                                }

                                                // Delete confirmation dialog
                                                if (showDeleteDialog) {
                                                    DeleteConfirmationDialog(
                                                        title = "Delete tag?",
                                                        message = "Are you sure you want to delete ${tag.name}? This action cannot be undone.",
                                                        onConfirm = {
                                                            scope.launch {
                                                                swipeRevealState.animateTo(0)
                                                                currentlyRevealedTagId = null
                                                            }
                                                            onConfirmDeleteTag(tag.id)
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
                                                                            bottomStart = CornerRadius.Small,
                                                                            topEnd = CornerRadius.Small,
                                                                            bottomEnd = CornerRadius.Small
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
                                                                    contentDescription = "Delete tag",
                                                                    tint = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                                                                    modifier = Modifier.size(Dimensions.IconSize)
                                                                )

                                                                Text(
                                                                    text = "Delete",
                                                                    style = MangalaTypography.Size10Medium(),
                                                                    color = MaterialTheme.mangalaColors.buttonDestructiveContainer
                                                                )
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    TagItem(
                                                        tag = tag,
                                                        onTagClick = onTagClick
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
                if (tagsPaging.loadState.append is LoadState.Loading) {
                    item(key = "tags_load_more") {
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

@Composable
fun EmptyTagsState(
    onAddNewTag: () -> Unit,
) {
    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoGroup,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = "Categorize Your Contacts, Your Way",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = "Use tags like 'Partners', 'Staking', or 'Friends' to quickly organize and search your contacts.  Create your first tag to get started.",
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.BASE))

        MangalaGradientButton(
            label = "Create New Tag",
            onClick = onAddNewTag,
            size = MangalaButtonSize.Small,
            modifier = Modifier
                .defaultMinSize(minWidth = 180.dp),
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}

@Composable
fun NoTagSearchResultsState() {
    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoGroup,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = "No Tags Found",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = "Make sure you've spelled the tag correctly. Tags are used to filter the list of contacts they are assigned to.)",
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}
