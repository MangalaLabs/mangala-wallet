package com.mangala.wallet.features.addressbook.presentation.group.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.CheckIcon
import com.mangala.wallet.features.addressbook.presentation.components.ContactColumnWithActionRowAndMultipleBlockchainsRow
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.features.addressbook.presentation.contact.recent.NoContactSearchResultsState
import com.mangala.wallet.features.addressbook.presentation.tag.model.AddressSelectionContactModel
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.component.KeyboardDismissBox

/**
 * Improved bottom sheet content for group address selection
 * Features:
 * - Max height 60% of screen
 * - Button always visible at bottom
 * - Surface wrapper for better elevation
 * - Safe drawing padding for gesture navigation
 * - Selected count display
 */
@Composable
fun GroupAddressSelectionSheetContent(
    contactsPaging: LazyPagingItems<AddressSelectionContactModel>,
    onToggleSelectContact: (AddressSelectionContactModel) -> Unit,
    onApplySelections: () -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onQrCodeClick: (String) -> Unit,
) {
    // Calculate selected count using derived state for performance
    val selectedCount by remember {
        derivedStateOf {
            contactsPaging.itemSnapshotList.items.count { it.isSelected }
        }
    }

    KeyboardDismissBox(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime) // Use WindowInsets directly
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.6f) // Max 60% but can expand
                .background(MaterialTheme.mangalaColors.bg)
                .safeDrawingPadding(), // Safe area padding for gesture navigation
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        // Handle bar
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(CornerRadius.Medium))
                .background(MaterialTheme.mangalaColors.border)
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        // Title
        Text(
            text = "Add address",
            style = MangalaTypography.Size14Medium(),
            color = MaterialTheme.mangalaColors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(Spacing.BASE))

        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.Padding.default)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChanged,
                placeholder = "Search contact",
            )
        }

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        // Contact list with flexible height
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Takes remaining space
                .fillMaxWidth()
                .padding(horizontal = Dimensions.Padding.default)
                .clip(RoundedCornerShape(CornerRadius.Small))
                .background(MaterialTheme.mangalaColors.bgInnerCard),
            contentPadding = PaddingValues(
                vertical = Dimensions.Padding.small,
                horizontal = Dimensions.Padding.default
            ),
        ) {
            when {
                // Empty search results
                searchQuery.isNotBlank() &&
                        contactsPaging.itemCount == 0 &&
                        contactsPaging.loadState.refresh is LoadState.NotLoading -> {
                    item {
                        NoContactSearchResultsState()
                    }
                }

                // Loading state
                contactsPaging.loadState.refresh is LoadState.Loading && contactsPaging.itemCount == 0 -> {
                    item {
                        MaxWidthBox(
                            modifier = Modifier
                                .mangalaWalletPlaceholder(
                                    visible = true,
                                    shape = RectangleShape,
                                    color = MaterialTheme.mangalaColors.skeletonBase,
                                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
                                )
                                .height(300.dp),
                        ) {}
                    }
                }

                // Contact items
                else -> {
                    items(
                        count = contactsPaging.itemCount,
                        key = contactsPaging.itemKey { it.contactWithMultipleBlockchainsModel.contactId },
                    ) { index ->
                        contactsPaging[index]?.let { contact ->
                            val dividerColor = MaterialTheme.mangalaColors.border
                            val isLast = index == contactsPaging.itemCount - 1 && 
                                        contactsPaging.loadState.append.endOfPaginationReached

                            MaxWidthRow(
                                modifier = Modifier
                                    .drawWithCache {
                                        onDrawWithContent {
                                            drawContent()
                                            // Draw divider except for last item
                                            if (!isLast) {
                                                drawLine(
                                                    color = dividerColor,
                                                    start = Offset(0f, size.height - 1.dp.toPx()),
                                                    end = Offset(size.width, size.height - 1.dp.toPx()),
                                                    strokeWidth = 1.dp.toPx()
                                                )
                                            }
                                        }
                                    }
                                    .padding(start = Dimensions.Padding.default,
                                        top = Dimensions.Padding.small,
                                        bottom = Dimensions.Padding.small
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Contact info
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ContactColumnWithActionRowAndMultipleBlockchainsRow(
                                        contact = contact.contactWithMultipleBlockchainsModel,
                                        privacyModeEnabled = false,
                                        isDisplayStar = true,
                                        onQrCodeClick = {
                                            onQrCodeClick(it.contactId)
                                        },
                                    )
                                }

                                // Selection checkbox
                                Box(
                                    modifier = Modifier
                                        .size(Dimensions.IconButtonSize)
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.dp,
                                            color = if (!contact.isSelected) {
                                                MaterialTheme.mangalaColors.border
                                            } else {
                                                MaterialTheme.mangalaColors.bgInnerCard
                                            },
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onToggleSelectContact(contact)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (contact.isSelected) {
                                        Image(
                                            imageVector = MangalaWalletPack.CheckIcon,
                                            contentDescription = null,
                                            modifier = Modifier.size(Dimensions.IconButtonSize)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Loading more indicator
            if (contactsPaging.loadState.append is LoadState.Loading) {
                item(key = "contacts_load_more") {
                    MaxWidthBox(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(vertical = Dimensions.Padding.small)
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

        // Bottom button section with Surface for better presentation
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimensions.Padding.default,
                    vertical = Dimensions.Padding.small
                ),
            color = MaterialTheme.mangalaColors.bgInnerCard,
            shape = RoundedCornerShape(CornerRadius.Small),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.Padding.default),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Selected count display
                if (selectedCount > 0) {
                    Text(
                        text = "$selectedCount address${if (selectedCount > 1) "es" else ""} selected",
                        style = MangalaTypography.Size13Regular(),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Dimensions.Padding.half),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Action button
                MangalaGradientButton(
                    label = "Add address",
                    onClick = onApplySelections,
                    size = MangalaButtonSize.Big,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedCount > 0
                )
            }
        }
    }
    }
}

/**
 * Modal Bottom Sheet wrapper for GroupAddressSelectionSheetContent
 * This provides a consistent API similar to other bottom sheets in the AddressBook module
 * 
 * Usage example:
 * ```
 * var showAddressSelection by remember { mutableStateOf(false) }
 * val contactsPaging = viewModel.contactPagingFlow.collectAsLazyPagingItems()
 * 
 * // Show bottom sheet
 * GroupAddressSelectionBottomSheet(
 *     isVisible = showAddressSelection,
 *     contactsPaging = contactsPaging,
 *     onToggleSelectContact = { contact ->
 *         viewModel.toggleContactSelection(contact)
 *     },
 *     onApplySelections = {
 *         viewModel.applySelections()
 *         showAddressSelection = false
 *     },
 *     searchQuery = searchQuery,
 *     onSearchQueryChanged = viewModel::updateSearchQuery,
 *     onQrCodeClick = { contactId ->
 *         // Handle QR code display
 *     },
 *     onDismiss = { showAddressSelection = false }
 * )
 * ```
 * 
 * @param isVisible Controls visibility of the bottom sheet
 * @param contactsPaging Paging data for contacts
 * @param onToggleSelectContact Callback when a contact selection is toggled
 * @param onApplySelections Callback when selections are applied
 * @param searchQuery Current search query
 * @param onSearchQueryChanged Callback when search query changes
 * @param onQrCodeClick Callback when QR code is clicked
 * @param onDismiss Callback when bottom sheet is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupAddressSelectionBottomSheet(
    isVisible: Boolean,
    contactsPaging: LazyPagingItems<AddressSelectionContactModel>,
    onToggleSelectContact: (AddressSelectionContactModel) -> Unit,
    onApplySelections: () -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onQrCodeClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.mangalaColors.bg,
            contentColor = MaterialTheme.mangalaColors.textPrimary,
            dragHandle = {
                // Custom drag handle following the guidelines
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 16.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.mangalaColors.border)
                )
            }
        ) {
            GroupAddressSelectionSheetContent(
                contactsPaging = contactsPaging,
                onToggleSelectContact = onToggleSelectContact,
                onApplySelections = onApplySelections,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onQrCodeClick = onQrCodeClick
            )
        }
    }
}

