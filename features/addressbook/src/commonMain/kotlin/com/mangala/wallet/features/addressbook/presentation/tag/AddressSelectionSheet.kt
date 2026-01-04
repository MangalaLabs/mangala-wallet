package com.mangala.wallet.features.addressbook.presentation.tag

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.platform.LocalConfiguration
import com.mangala.wallet.ui.component.KeyboardDismissBox

/**
 * Bottom sheet content for contact selection - works with deferring selection until the "Add contact" button is clicked
 * Shared component used across the app
 *
 * Height behavior solutions:
 * 1. Uses fillMaxHeight(0.6f) to set max height to 60% of screen
 * 2. Parent screen (AddressSelectionScreen) can call bottomSheetNavigator.expand() to force full expansion
 * 3. MangalaBottomSheetNavigator should be configured with skipHalfExpanded = true for immediate full height
 *
 * If bottom sheet still opens partially:
 * - Check MangalaBottomSheetNavigator configuration in parent screen
 * - Consider using requiredHeight with LocalConfiguration to set fixed height
 * - Ensure bottomSheetNavigator.expand() is called in LaunchedEffect
 */
@Composable
fun AddressSelectionSheetContent(
    contactsPaging: LazyPagingItems<AddressSelectionContactModel>,
    onToggleSelectContact: (AddressSelectionContactModel) -> Unit,
    onApplySelections: () -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onQrCodeClick: (String) -> Unit,
) {
    val maxHeight = androidx.compose.ui.platform.LocalConfiguration.current.screenHeightDp.dp * 0.6f
    
    KeyboardDismissBox(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime) // Use WindowInsets directly
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeight) // Max 60% but can expand
                .background(MaterialTheme.mangalaColors.bg)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(CornerRadius.Medium))
                    .background(MaterialTheme.mangalaColors.border)
            )

            Text(
                text = "Add contact",
                style = MangalaTypography.Size14Medium(),
                color = MaterialTheme.mangalaColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(Spacing.TINY))

            MaxWidthRow(
                modifier = Modifier
                    .padding(
                        vertical = Dimensions.Padding.half,
                        horizontal = Dimensions.Padding.default
                    ),
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChanged,
                    placeholder = "Search contact",
                )

            // TODO: Implement the filter icon button
        }

            Spacer(modifier = Modifier.height(Spacing.TINY))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Dimensions.Padding.default)
                .background(
                    color = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = RoundedCornerShape(CornerRadius.Small)
                ),
            contentPadding = PaddingValues(
                vertical = Dimensions.Padding.small,
                horizontal = Dimensions.Padding.default
            ),
        ) {
            when {
                searchQuery.isNotBlank() &&
                        contactsPaging.itemCount == 0 &&
                        contactsPaging.loadState.refresh is LoadState.NotLoading -> item {
                    NoContactSearchResultsState()
                }

                contactsPaging.loadState.refresh is LoadState.Loading && contactsPaging.itemCount == 0 -> item {
                    MaxWidthBox(
                        modifier = Modifier
                            .mangalaWalletPlaceholder(
                                visible = true,
                                shape = RectangleShape,
                                color = MaterialTheme.mangalaColors.skeletonBase,
                                highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
                            )
                            .height(500.dp),
                    ) {}
                }

                else -> items(
                    count = contactsPaging.itemCount,
                    key = contactsPaging.itemKey { it.contactWithMultipleBlockchainsModel.contactId },
                ) { index ->
                    contactsPaging[index]?.let { contact ->
                        val dividerColor = MaterialTheme.mangalaColors.border

                        val isLast = index == contactsPaging.itemCount - 1 && contactsPaging.loadState.append.endOfPaginationReached

                        MaxWidthRow(
                            modifier = Modifier
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()

                                        if (isLast.not())
                                            drawLine(
                                                color = dividerColor,
                                                start = Offset(0f, size.height - 1.dp.toPx()),
                                                end = Offset(size.width, size.height - 1.dp.toPx()),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                    }
                                }
                                .padding(
                                    top = Dimensions.Padding.small,
                                    bottom = Dimensions.Padding.small,
                                    start = Dimensions.Padding.default,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
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

                            Box(
                                modifier = Modifier
                                    .size(Dimensions.IconButtonSize)
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (!contact.isSelected) MaterialTheme.mangalaColors.border else MaterialTheme.mangalaColors.bgInnerCard,
                                        CircleShape
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

            // Button section with Surface wrapper for better elevation
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // Ensure button is above navigation bar
                    .padding(
                        horizontal = Dimensions.Padding.default,
                        vertical = Dimensions.Padding.small
                    ),
                color = MaterialTheme.mangalaColors.bg
            ) {
                Column() {
                    // Selected count display
                    val selectedCount =
                        contactsPaging.itemSnapshotList.items.count { it.isSelected }
                    MangalaGradientButton(
                        label = "Add contact",
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