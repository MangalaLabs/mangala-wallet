package com.mangala.wallet.features.addressbook.presentation.group.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Check
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.features.addressbook.presentation.components.DocumentCopyButton
import com.mangala.wallet.features.addressbook.presentation.components.BlockchainIconBox
import androidx.compose.ui.zIndex
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.presentation.components.ContactQrButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.KeyboardDismissBox
import com.mangala.wallet.features.addressbook.utils.WalletAddressFormatter
import com.mangala.wallet.features.addressbook.presentation.components.UnifiedAddressItem

/**
 * Composable for wallet bottom sheet with pagination support
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletBottomSheetWithPagination(
    uiState: WalletBottomSheetUiState,
    onDismiss: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onWalletToggle: (String) -> Unit,
    onConfirm: () -> Unit,
    onLoadMore: () -> Unit,
    onCopyClick: (Int) -> Unit,
    onQrCodeClick: (Int) -> Unit
) {
    var showCopyMessage by remember { mutableStateOf(false) }
    var copiedWalletId by remember { mutableStateOf<String?>(null) }

    // Loại bỏ ModalBottomSheet wrapper vì đã có MangalaBottomSheetNavigator bên ngoài
    KeyboardDismissBox(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f) // Max height 60% of screen
            .background(MaterialTheme.mangalaColors.bg)
            .safeDrawingPadding()
            .imePadding() // Handle keyboard insets
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ColorsNew.stroke)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Add address",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar using reusable SearchBar component
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChanged,
                    placeholder = "Search address",
                )
            }

            // Wallet list section with white background and rounded corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(Dimensions.Padding.default)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.mangalaColors.bgInnerCard)
                    .padding(16.dp)
            ) {
                if (uiState.isLoading && uiState.wallets.isEmpty()) {
                    // Initial loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.mangalaColors.iconPrimary
                        )
                    }
                } else if (uiState.wallets.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isEmpty()) "No wallets available" else "No wallets match your search",
                            fontSize = 14.sp,
                            color = MaterialTheme.mangalaColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Wallet list with pagination support
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(uiState.wallets) { index, wallet ->
                            // Check if this wallet is selected
                            val isSelected =
                                uiState.selectedWalletAddressIds.contains(wallet.walletId)

                            // When we're within 5 items of the end, load more
                            if (index >= uiState.wallets.size - 5 && !uiState.isLoadingMore && !uiState.hasReachedEnd) {
                                LaunchedEffect(Unit) {
                                    onLoadMore()
                                }
                            }

                            // Use unified address item component
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                UnifiedAddressItem(
                                    blockchainSymbol = wallet.blockchainTypeSymbol,
                                    // If alias exists, use it; otherwise use contact name as main title
                                    walletAlias = wallet.walletAlias?.takeIf { it.isNotBlank() } ?: "",
                                    contactName = wallet.contactName,
                                    walletAddress = WalletAddressFormatter.formatForDisplay(wallet.walletAddress),
                                    fullWalletAddress = wallet.walletAddress,
                                    showCard = false,
                                    showCheckbox = true,
                                    isSelected = isSelected,
                                    onCheckboxClick = {
                                        onWalletToggle(wallet.walletId)
                                    },
                                    onCopyClick = {
                                        copiedWalletId = wallet.walletId
                                        showCopyMessage = true
                                        onCopyClick(index)
                                    },
                                    onQrCodeClick = {
                                        onQrCodeClick(index)
                                    },
                                )

                                // Bottom border - only show if not the last item
                                if (index < uiState.wallets.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.mangalaColors.border,
                                        thickness = 0.5.dp,
                                        modifier = Modifier.align(Alignment.BottomCenter)
                                    )
                                }

                                // Copy message overlay positioned below the copy button
                                this@Column.AnimatedVisibility(
                                    visible = showCopyMessage && copiedWalletId == wallet.walletId,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 40.dp, bottom = 8.dp)
                                        .zIndex(1000f)
                                ) {
                                    LaunchedEffect(Unit) {
                                        delay(2000)
                                        showCopyMessage = false
                                        copiedWalletId = null
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.mangalaColors.textPrimary.copy(
                                                    alpha = 0.85f
                                                ),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Copied to clipboard",
                                            color = MaterialTheme.mangalaColors.bgInnerCard,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // Loading more indicator
                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.mangalaColors.iconPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.mangalaColors.bgInnerCard)
                    .navigationBarsPadding() // Ensure button is above navigation bar
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimensions.Padding.default,
                            end = Dimensions.Padding.default,
//                        bottom = Dimensions.Padding.default
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selected count text (not a button anymore)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "${uiState.selectedCount} address selected",
                            fontSize = FontType.TINY_13,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.mangalaColors.textPrimary,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }

                    // Add address button
                    MangalaGradientButton(
                        label = "Add address",
                        onClick = onConfirm,
                        enabled = uiState.selectedCount > 0,
                        size = MangalaButtonSize.Medium,
                        modifier = Modifier
                            .weight(1f)
                    )
                }

            }
            // Loại bỏ Home indicator vì đã có handle từ MangalaBottomSheetNavigator
        }
    }
}