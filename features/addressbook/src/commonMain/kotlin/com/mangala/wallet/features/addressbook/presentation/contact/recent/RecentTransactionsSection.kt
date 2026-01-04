package com.mangala.wallet.features.addressbook.presentation.contact.recent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.features.addressbook.data.model.ContactRecentTransactionModel
import com.mangala.wallet.features.addressbook.presentation.contact.recent.components.TransactionItem
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

fun LazyListScope.recentTransactionsSection(
    transactionsPaging: LazyPagingItems<ContactRecentTransactionModel>,
    isSearching: Boolean,
    navigateToTransactionDetail: (ContactRecentTransactionModel) -> Unit,
    onTransactionClick: (ContactRecentTransactionModel) -> Unit,
    onClickSendToken: () -> Unit,
    onClickQrCode: (ContactRecentTransactionModel) -> Unit,
    navigateToImportAccount: () -> Unit,
    navigateToCreateAccount: () -> Unit,
    hasAnyImportedAccount: Boolean,
    privacyModeEnabled: Boolean = false,
    currentlyRevealedTransactionId: String?,
    onRevealedTransactionChange: (String?) -> Unit,
) {
    val placeholderRecentTxObject = ContactRecentTransactionModel(
        contactId = "",
        contactName = "",
        walletAddress = "",
        walletAddressId = "",
        walletAlias = "",
        walletSensitive = false,
        blockchainUid = "eos",
        blockchainName = "",
        blockchainSymbol = "",
        blockchainIcon = "",
        lastTransactionTime = 0L,
        lastTransactionAmount = "",
        lastTokenSymbol = "",
        transactionStatus = "",
        isSender = false,
        isFavorite = false,
        transactionId = "",
        avatar = null
    )

    stickyHeader(
        key = "recent_transactions_header",
    ) {
        Text(
            text = "Recent Transactions",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.mangalaColors.bg)
                .padding(bottom = Dimensions.Padding.half)
        )
    }

    // Check if we're in the initial loading phase (either loading or no source loaded yet)
    val isInitialLoad = (transactionsPaging.loadState.refresh is LoadStateLoading ||
                       transactionsPaging.loadState.source.refresh is LoadStateLoading) && transactionsPaging.itemCount == 0

    when {
        // Show skeleton when loading or when we have no items and mediator hasn't run yet
        isInitialLoad || (transactionsPaging.itemCount == 0 && transactionsPaging.loadState.mediator?.refresh is LoadStateLoading) ->
            items(10) {
                TransactionItem(
                    transaction = placeholderRecentTxObject,
                    isLoading = true,
                    onClick = {},
                    onClickDetail = {},
                    onClickQrCode = {}
                )
            }

        // Show empty state only when:
        // 1. Not searching
        // 2. No items
        // 3. Both source and mediator have finished loading
        isSearching.not() && 
        transactionsPaging.itemCount == 0 && 
        transactionsPaging.loadState.source.refresh is LoadStateNotLoading &&
        transactionsPaging.loadState.mediator?.refresh !is LoadStateLoading ->
            item {
                if (hasAnyImportedAccount)
                    NoRecentTransactionsState(onClickSendToken = onClickSendToken)
                else
                    NoImportedAccountForRecentTransactionState(
                        onClickCreate = navigateToCreateAccount,
                        onClickImport = navigateToImportAccount
                    )
            }

        // Show actual items
        else ->
            items(
                count = transactionsPaging.itemCount,
                key = transactionsPaging.itemKey { "${it.transactionId}_${it.walletAddress}" },
            ) { index ->
                transactionsPaging[index]?.let { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = {
                            onTransactionClick(transaction)
                        },
                        onClickDetail = {
                            navigateToTransactionDetail(transaction)
                        },
                        onClickQrCode = {
                            onClickQrCode(transaction)
                        },
                        privacyModeEnabled = privacyModeEnabled,
                        currentlyRevealedTransactionId = currentlyRevealedTransactionId,
                        onRevealedTransactionChange = onRevealedTransactionChange
                    )
                }
            }
    }

    if (transactionsPaging.loadState.append is LoadStateLoading)
        item(
            key = "recent_transactions_load_more",
        ) {
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