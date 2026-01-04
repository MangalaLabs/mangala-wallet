package com.mangala.wallet.features.addressbook.presentation.group.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Delete
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Wallet
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.mokoresources.MR
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.zIndex
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.features.addressbook.presentation.components.BlockchainIconBox
import com.mangala.wallet.features.addressbook.presentation.components.ContactQrButton
import com.mangala.wallet.features.addressbook.presentation.components.DocumentCopyButton
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.compose.localized
import com.mangala.wallet.ui.theme.mangalaColors
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.Qrcode

/**
 * HTML-style Wallet List component that mimics an HTML/CSS structure for displaying wallet addresses.
 *
 * @param wallets List of wallet addresses to display
 * @param onAddAddressClick Callback when Add Address button is clicked
 * @param onCopyClick Callback when copy button is clicked on an address (receives index of wallet)
 * @param onQrCodeClick Callback when QR code button is clicked on an address (receives index of wallet)
 * @param onDeleteClick Callback when delete button is clicked on an address (receives index of wallet)
 * @param isLoading Whether more data is being loaded
 * @param hasMoreData Whether there's more data to load
 * @param onLoadMore Callback to load more data when scrolling to the bottom
 * @param totalCount Total number of wallets (optional, if known)
 */
@Composable
fun WalletListHtmlStyle(
    wallets: List<GroupWallet>,
    onAddAddressClick: () -> Unit,
    onCopyClick: (index: Int) -> Unit = {},
    onQrCodeClick: (index: Int) -> Unit = {},
    onDeleteClick: (index: Int) -> Unit = {},
    isLoading: Boolean = false,
    hasMoreData: Boolean = false,
    onLoadMore: () -> Unit = {},
    totalCount: Int? = null
) {
    var showCopyMessage by remember { mutableStateOf(false) }
    var copiedWalletIndex by remember { mutableStateOf<Int?>(null) }

    // Container div với Figma style
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.mangalaColors.bgInnerCard)
    ) {
        // Header section với padding theo Figma: 12px vertical, 16px horizontal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            HeaderSection(
                count = totalCount ?: wallets.size,
                onAddAddressClick = onAddAddressClick
            )
        }

        // Wallet list section
        if (wallets.isNotEmpty() || isLoading) {
            WalletListSection(
                wallets = wallets,
                onCopyClick = onCopyClick,
                onQrCodeClick = onQrCodeClick,
                onDeleteClick = onDeleteClick,
                showCopyMessage = showCopyMessage,
                copiedWalletIndex = copiedWalletIndex,
                onCopyWithOverlay = { index ->
                    copiedWalletIndex = index
                    showCopyMessage = true
                },
                onHideCopyOverlay = {
                    showCopyMessage = false
                    copiedWalletIndex = null
                },
                isLoading = isLoading,
                hasMoreData = hasMoreData,
                onLoadMore = onLoadMore
            )
        } else {
            // Empty state
            EmptyStateSection()
        }
    }
}

/**
 * Header section with title and add button
 */
@Composable
private fun HeaderSection(
    count: Int,
    onAddAddressClick: () -> Unit
) {
    // Header với Figma style
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Title heading với Figma style
        Text(
            text = "${MR.strings.label_address_list.desc().localized()} ($count)",
            fontWeight = FontWeight.Medium, // Figma: Medium weight
            fontSize = 14.sp, // Figma: Changed from 16sp to 14sp
            letterSpacing = (-0.01).sp, // Figma: -1% letter spacing
            color = MaterialTheme.mangalaColors.textPrimary
        )

//        // Add button với style Figma
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(20.dp), // Fixed height for entire row
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Start
//        ) {
//            Icon(
//                imageVector = Icons.Default.Add,
//                contentDescription = "Add",
//                tint = ColorsNew.blueActionButton,
//                modifier = Modifier.size(20.dp)
//            )
//
//            Spacer(modifier = Modifier.width(4.dp))
//
//            Text(
//                text = text,
//                style = TextStyle(
//                    fontSize = 14.sp,
//                    color = ColorsNew.blueActionButton,
//                    fontWeight = FontWeight.Medium
//                ),
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
        Row(
            modifier = Modifier
                .clickable { onAddAddressClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Address",
                tint = MaterialTheme.mangalaColors.textLink,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(4.dp)) // Figma: Reduced from 6dp

            Text(
                text = MR.strings.label_add_address.desc().localized(),
                fontSize = 13.sp, // Figma: Changed from 14sp to 13sp
                fontWeight = FontWeight.Medium, // Figma: Medium weight
                letterSpacing = (-0.01).sp, // Figma: -1% letter spacing
                color = MaterialTheme.mangalaColors.textLink
            )
        }
    }
}

/**
 * Wallet list section (like a <ul> with multiple <li> elements)
 */
@Composable
private fun WalletListSection(
    wallets: List<GroupWallet>,
    onCopyClick: (index: Int) -> Unit,
    onQrCodeClick: (index: Int) -> Unit,
    onDeleteClick: (index: Int) -> Unit,
    showCopyMessage: Boolean,
    copiedWalletIndex: Int?,
    onCopyWithOverlay: (index: Int) -> Unit,
    onHideCopyOverlay: () -> Unit,
    isLoading: Boolean = false,
    hasMoreData: Boolean = false,
    onLoadMore: () -> Unit = {}
) {
    // Wallet list với Figma style
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        wallets.forEachIndexed { index, wallet ->
            // Each wallet item với border stroke theo Figma
            WalletItem(
                wallet = wallet,
                index = index,
                onCopyClick = {
                    onCopyWithOverlay(index)
                    onCopyClick(index)
                },
                onQrCodeClick = { onQrCodeClick(index) },
                onDeleteClick = { onDeleteClick(index) },
                showCopyOverlay = showCopyMessage && copiedWalletIndex == index,
                onHideCopyOverlay = onHideCopyOverlay,
                isLast = index == wallets.size - 1
            )
        }

        // Load more trigger when reaching the last items
        if (wallets.isNotEmpty() && hasMoreData && !isLoading) {
            LaunchedEffect(wallets.size) {
                // Trigger load more when we have items and more data is available
                onLoadMore()
            }
        }

        // Loading indicator at the bottom
        if (isLoading && wallets.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.mangalaColors.iconPrimary
                )
            }
        }
    }
}

/**
 * Individual wallet item (like an <li> element)
 */
@Composable
private fun WalletItem(
    wallet: GroupWallet,
    index: Int,
    onCopyClick: () -> Unit,
    onQrCodeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    showCopyOverlay: Boolean,
    onHideCopyOverlay: () -> Unit,
    isLast: Boolean = false
) {
    // Formatting wallet address for display
    val shortenedAddress = if (wallet.walletAddress.length > 12) {
        val prefix = wallet.walletAddress.take(6)
        val suffix = wallet.walletAddress.takeLast(4)
        "$prefix...$suffix"
    } else {
        wallet.walletAddress
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Wallet item content với Figma style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp), // Figma: 12px vertical padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Blockchain icon (like an <img> with styling)
                BlockchainIconBox(
                    symbol = wallet.blockchainTypeSymbol,
                    size = 32.dp,
                    iconSize = 24.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Wallet info section
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Wallet name row với Figma style
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Determine display logic based on alias availability

                        // Main title: Alias if available, otherwise Contact Name
                        if (!wallet.walletAlias.isNullOrBlank()) {
                            Text(
                                text = wallet.walletAlias,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                letterSpacing = (-0.01).sp, // Figma: -1% letter spacing
                                color = MaterialTheme.mangalaColors.textPrimary
                            )
                        }

                        // Owner name - only show if alias exists (to avoid duplication)
                        Text(
                            text = wallet.contactName,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp, // Figma: 13sp instead of 14sp
                            letterSpacing = (-0.01).sp, // Figma: -1% letter spacing
                            color = MaterialTheme.mangalaColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Address row với Figma style
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Address text
                        Text(
                            text = shortenedAddress,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.mangalaColors.textPrimary
                        )

                        // Copy button
                        Icon(
                            imageVector = MangalaWalletPack.Copy,
                            contentDescription = "Copy Address",
                            tint = MaterialTheme.mangalaColors.iconSecondary,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onCopyClick() }
                        )

                        // QR code button
                        Icon(
                            imageVector = ContactIcon.Qrcode,
                            contentDescription = "Show QR Code",
                            tint = MaterialTheme.mangalaColors.iconSecondary,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onQrCodeClick() }
                        )
                    }
                }

                // Delete button với Figma style
                Icon(
                    imageVector = MangalaWalletPack.Delete,
                    contentDescription = "Delete Address",
                    tint = MaterialTheme.mangalaColors.textLink,
                    modifier = Modifier
                        .size(20.dp) // Figma: 20px size
                        .clickable { onDeleteClick() }
                )
            }

            // Bottom border stroke theo Figma (0.5px thickness) - không hiển thị cho item cuối
            if (!isLast) {
                HorizontalDivider(
                    color = MaterialTheme.mangalaColors.border,
                    thickness = 0.5.dp, // Figma: 0.5px stroke
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Copy message overlay
        AnimatedVisibility(
            visible = showCopyOverlay,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 60.dp)
                .zIndex(1000f)
        ) {
            LaunchedEffect(Unit) {
                delay(2000)
                onHideCopyOverlay()
            }

            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Copied to clipboard",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

/**
 * Empty state display when no wallets are available
 */
@Composable
private fun EmptyStateSection() {
    // Empty state container với Figma style
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Empty state text với Figma style
        Text(
            text = MR.strings.label_no_addresses.desc().localized(),
            color = MaterialTheme.mangalaColors.textSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}