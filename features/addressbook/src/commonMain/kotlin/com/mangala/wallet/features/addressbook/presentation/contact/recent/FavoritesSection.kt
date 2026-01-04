package com.mangala.wallet.features.addressbook.presentation.contact.recent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Add
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRightWithStem
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.presentation.components.ContactAvatar
import com.mangala.wallet.features.addressbook.presentation.contact.recent.BlockchainIcons.getBackgroundColorForSymbol
import com.mangala.wallet.features.addressbook.presentation.contact.recent.BlockchainIcons.getColorForSymbol
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

fun LazyListScope.favoritesSection(
    favorites: List<ContactModel>?,
    onContactClick: (ContactModel) -> Unit,
    onToggleShowHideClick: () -> Unit,
    navigateToFavoriteContacts: () -> Unit,
    isShowFavorite: Boolean,
) {
    if (favorites?.isEmpty() == true) return
    val placeholderFavoriteObject = ContactModel(
        contactId = "foo",
        contactName = "Foo Contact",
        walletAddress = "0x1234567890abcdef1234567890abcdef12345678",
        walletAddressId = "foo-wallet-id",
        walletAlias = "Foo Wallet",
        walletSensitive = false,
        blockchainName = "Ethereum",
        blockchainSymbol = "ETH",
        blockchainIcon = "",
        blockChainColor = "#627EEA",
        isFavorite = true,
        isSensitive = false,
    )

    item {
        MaxWidthColumn {
            MaxWidthRow(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Favorites",
                    style = MangalaTypography.Size17Medium(),
                    color = MaterialTheme.mangalaColors.textPrimary
                )

                Text(
                    text = if (isShowFavorite) "Hide" else "Show",
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    modifier = Modifier
                        .clickable(onClick = onToggleShowHideClick)
                        .padding(
                            vertical = Dimensions.Padding.quarter,
                            horizontal = Dimensions.Padding.half
                        ),
                )
            }

            if (isShowFavorite) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimensions.Padding.half),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                ) {
                    when {
                        favorites == null -> {
                            items(10) {
                                FavoriteContactItem(
                                    contact = placeholderFavoriteObject,
                                    isLoading = true,
                                    onClick = {}
                                )
                            }
                        }
                        else -> {
                            items(
                                items = favorites,
                                key = { it.contactId }
                            ) { contact ->
                                FavoriteContactItem(
                                    contact = contact,
                                    onClick = { onContactClick(contact) }
                                )
                            }

                            item(
                                key = "navigate_to_favorite_contacts",
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    IconButton(
                                        onClick = navigateToFavoriteContacts,
                                        modifier = Modifier.size(Dimensions.AddressBookContactAvatarSize)
                                    ) {
                                        Icon(
                                            imageVector = MangalaWalletPack.ArrowRightWithStem,
                                            contentDescription = "Navigate to all favorites",
                                            tint = MaterialTheme.mangalaColors.iconPrimary,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(Spacing.XTINY))

                                    Text(
                                        text = "See All",
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.mangalaColors.textPrimary,
                                        style = MangalaTypography.Size12Medium(),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.TINY))
        }
    }
}

@Composable
fun FavoriteContactItem(
    contact: ContactModel,
    isLoading: Boolean = false,
    onClick: () -> Unit,
) {
    val name = remember(contact.contactName) {
        contact.contactName.split(" ").first()
    }
    val backgroundColor = remember(contact.blockchainSymbol) {
        getBackgroundColorForSymbol(contact.blockchainSymbol)
    }
    val symbolColor = remember(contact.blockchainSymbol) {
        getColorForSymbol(contact.blockchainSymbol)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .mangalaWalletPlaceholder(
                visible = isLoading,
                color = MaterialTheme.mangalaColors.skeletonBase,
                highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
            )
            .clickable(onClick = onClick)
            .width(56.dp)
    ) {
        ContactAvatar(
            contactId = contact.contactId,
            contactName = contact.contactName,
            isFavorite = false,
            iconString = contact.avatar
        )

        Spacer(modifier = Modifier.height(Spacing.XTINY))

        Text(
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.mangalaColors.textPrimary,
            style = MangalaTypography.Size12Medium(),
        )

        Spacer(modifier = Modifier.height(Spacing.XTINY))

        Text(
            text = contact.blockchainSymbol,
            textAlign = TextAlign.Center,
            color = symbolColor,
            style = MangalaTypography.Size10Medium(),
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
                .padding(horizontal = Dimensions.Padding.quarter)
        )

    }
}