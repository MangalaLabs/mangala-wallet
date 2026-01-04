package com.mangala.wallet.features.addressbook.presentation.contact.recent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.Star
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun NoRecentTransactionsState(
    onClickSendToken: () -> Unit,
) {
    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoTransaction,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = "Ready to See Your Crypto in Action?",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = "Make your first move-send, receive, or swap-and this space will \"Come Alive\" with your transaction history.",
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.BASE))

        MangalaGradientButton(
            label = "Send token",
            onClick = onClickSendToken,
            size = MangalaButtonSize.Small,
            modifier = Modifier
                .defaultMinSize(minWidth = 180.dp),
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}

@Composable
fun NoTransactionSearchResultsState() {
    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoTransaction,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = "No Matching Transactions Found",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = "Please double-check for typos or try different keywords. You can also search by contact name, wallet address, tag, or blockchain.",
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}

@Composable
internal fun NoImportedAccountForRecentTransactionState(
    onClickCreate: () -> Unit,
    onClickImport: () -> Unit,
) {
    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoTransaction,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = MR.strings.title_addressbook_recent_transaction_no_imported_account.desc().localized(),
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = MR.strings.message_addressbook_recent_transaction_no_imported_account.desc().localized(),
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.BASE))

        MangalaGradientButton(
            label = MR.strings.button_addressbook_recent_transaction_create.desc().localized(),
            onClick = onClickCreate,
            size = MangalaButtonSize.Small,
            modifier = Modifier
                .defaultMinSize(minWidth = 180.dp),
        )

        Spacer(modifier = Modifier.height(Spacing.BASE))

        MangalaGradientButton(
            label = MR.strings.button_addressbook_recent_transaction_import.desc().localized(),
            onClick = onClickImport,
            size = MangalaButtonSize.Small,
            buttonStyle = MangalaButtonStyle.TRANSPARENT,
            modifier = Modifier
                .defaultMinSize(minWidth = 180.dp),
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}

@Composable
fun EmptyFavoritesState(
    onAddFavorite: () -> Unit,
) {
    val inlineContentFavoriteIconId = remember { "inlineContentFavoriteIcon" }

    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoFavorite,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = "Create Shortcuts to Key Contacts",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = buildAnnotatedString {
//                Need a better way to handle this to support localization
                append("Tap the star icon ")
                appendInlineContent(inlineContentFavoriteIconId, "[favorite icon]")
                append(" on any contact to add them here for quicker access next time.")
            },
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center,
            inlineContent = mapOf(
                inlineContentFavoriteIconId to InlineTextContent(
                    placeholder = Placeholder(
                        width = 14.sp,
                        height = 14.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    ),
                    children = {
                        Icon(
                            imageVector = ContactIcon.Star,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.mangalaColors.textLink,
                        )
                    }
                )
            )
        )

        Spacer(modifier = Modifier.height(Spacing.BASE))

        MangalaGradientButton(
            label = "Add your favorites contact",
            onClick = onAddFavorite,
            size = MangalaButtonSize.Small,
            modifier = Modifier
                .defaultMinSize(minWidth = 180.dp),
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}

@Composable
fun NoFavoriteContactSearchResultsState() {
    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoFavorite,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = "No Favorite Contacts Found",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = "Please check your search terms. Remember, this search only applies to contacts you've marked as favorites.",
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}

@Composable
fun EmptyContactState(
    onAddContact: () -> Unit,
) {
    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoContact,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = "Build Your Address Book",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = "Save wallet addresses to send tokens faster, more securely, and never worry about copy-paste errors again.",
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.BASE))

        MangalaGradientButton(
            label = "Add contact",
            onClick = onAddContact,
            size = MangalaButtonSize.Small,
            modifier = Modifier
                .defaultMinSize(minWidth = 180.dp),
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}

@Composable
fun NoContactSearchResultsState() {
    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoContact,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = "No Contacts Found",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = "Check for typos. You can also search by wallet address, tag, or blockchain name.",
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}
