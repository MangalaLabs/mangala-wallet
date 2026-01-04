package com.mangala.wallet.features.addressbook.presentation.contact.recent.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.InfoCircle
import com.mangala.wallet.features.addressbook.data.model.ContactRecentTransactionModel
import com.mangala.wallet.features.addressbook.presentation.components.ContactRowWithActions
import com.mangala.wallet.features.addressbook.utils.getTimeAgo
import com.mangala.wallet.ui.component.MangalaSwipeRevealContainer
import com.mangala.wallet.ui.component.MaxHeightColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.rememberSwipeRevealState
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import kotlinx.coroutines.launch

@Composable
fun TransactionItem(
    transaction: ContactRecentTransactionModel,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    onClickDetail: () -> Unit,
    onClickQrCode: () -> Unit,
    privacyModeEnabled: Boolean = false,
    currentlyRevealedTransactionId: String? = null,
    onRevealedTransactionChange: (String?) -> Unit = {},
) {
    val scope = rememberCoroutineScope()

    var revealedWidth by remember { mutableStateOf(Float.MAX_VALUE) }
    val swipeRevealState = rememberSwipeRevealState(revealedWidth = revealedWidth)

    // Create unique ID for this transaction item
    val transactionKey = remember(transaction) { "${transaction.transactionId}_${transaction.walletAddress}" }

    // Monitor this item's reveal state and update global state
    LaunchedEffect(swipeRevealState.offset) {
        val isRevealed = swipeRevealState.offset > 0f
        if (isRevealed && currentlyRevealedTransactionId != transactionKey) {
            // This item is now revealed, update global state
            onRevealedTransactionChange(transactionKey)
        } else if (!isRevealed && currentlyRevealedTransactionId == transactionKey) {
            // This item is now closed, clear global state
            onRevealedTransactionChange(null)
        }
    }

    // Close this item if another item becomes revealed
    LaunchedEffect(currentlyRevealedTransactionId) {
        if (currentlyRevealedTransactionId != null &&
            currentlyRevealedTransactionId != transactionKey &&
            swipeRevealState.offset > 0f
        ) {
            // Another item is revealed and this one is open, close this one
            swipeRevealState.snapTo(0)
        }
    }

    val onClickDetailTransaction = remember(swipeRevealState, onClickDetail) {
        {
            scope.launch {
                swipeRevealState.animateTo(0)
                onRevealedTransactionChange(null)
            }
            onClickDetail()
        }
    }
    MangalaSwipeRevealContainer(
        state = swipeRevealState,
        revealedWidth = revealedWidth,
        revealContent = {
            MaxHeightColumn(
                modifier = Modifier
                    .graphicsLayer {
                        revealedWidth = size.width
                    }
                    .padding(start = Dimensions.Padding.default)
                    .clip(shape = RoundedCornerShape(CornerRadius.Small))
                    .clickable(onClick = onClickDetailTransaction)
                    .background(color = MaterialTheme.mangalaColors.bgSwipeAction)
                    .padding(horizontal = Dimensions.Padding.default),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.XTINY, Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = MangalaWalletPack.InfoCircle,
                    contentDescription = "Transaction Details",
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .mangalaWalletPlaceholder(
                    visible = isLoading,
                    color = MaterialTheme.mangalaColors.skeletonBase,
                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
                    shape = RoundedCornerShape(CornerRadius.Small),
                )
                .clip(RoundedCornerShape(CornerRadius.Small))
                .clickable(onClick = onClick)
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .padding(
                    vertical = Dimensions.Padding.small,
                    horizontal = Dimensions.Padding.default
                ),
        ) {
            MaxWidthRow(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Contact information using unified component
                ContactRowWithActions(
                    contactId = transaction.contactId,
                    contactName = transaction.contactName,
                    avatar = transaction.avatar,
                    textToCopy = transaction.walletAddress,
                    onClickQrCode = onClickQrCode,
                    isFavorite = transaction.isFavorite,
                    privacyModeEnabled = privacyModeEnabled,
                    privacyDisplayMode = transaction.privacyDisplayMode, // ✅ FIX: Pass privacy display mode
                    address = transaction.walletAddress
                )

                Spacer(modifier = Modifier.weight(1f))

                // Right side - Transaction information
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Transaction amount with icon using reusable component
                    TransactionAmountDisplay(
                        formattedAmount = transaction.formattedAmount,
                        isSender = transaction.isSender
                    )

                    TransactionStatusText(
                        status = transaction.transactionStatus
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.XSMALL))

            // Timestamp and memo
            MaxWidthRow(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val transactionTime = remember(transaction.lastTransactionTime) {
                    getTimeAgo(transaction.lastTransactionTime)
                }
                // Timestamp on the left
                Text(
                    text = transactionTime,
                    color = MaterialTheme.mangalaColors.textSecondary,
                    style = MangalaTypography.Size12Regular(),
                    modifier = Modifier.padding(end = Dimensions.Padding.quarter),
                )

                // Memo on the right
                Text(
                    text = transaction.memo,
                    color = MaterialTheme.mangalaColors.textSecondary,
                    style = MangalaTypography.Size12Regular(),
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Dimensions.Padding.quarter)
                )
            }
        }
    }
}