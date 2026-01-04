package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.OutlineStar
import com.mangala.wallet.features.addressbook.icon.contacticon.Star
import com.mangala.wallet.features.addressbook.presentation.privacy.PrivacyAwareAddressText
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun RowScope.ContactRowWithActions(
    contactId: String,
    contactName: String,
    avatar: String?,
    address: String,
    textToCopy: String,
    onClickQrCode: () -> Unit,
    isFavorite: Boolean = false,
    isDisplayStar: Boolean = false,
    onCopyComplete: (() -> Unit)? = null,
    onClickStar: (() -> Unit)? = null,
    privacyModeEnabled: Boolean = false,
    privacyDisplayMode: DisplayMode = DisplayMode.FULL,
    isSensitive: Boolean = false,
) {
    // Avatar
    ContactAvatar(
        contactId = contactId,
        contactName = contactName,
        isFavorite = isFavorite,
        isDisplayStar = isDisplayStar,
        iconString = avatar
    )

    Spacer(modifier = Modifier.width(Spacing.TINY))

    Column {
        Text(
            text = contactName,
            style = MangalaTypography.Size14Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Use the reusable PrivacyAwareAddressText component
            PrivacyAwareAddressText(
                address = address,
                privacyModeEnabled = privacyModeEnabled,
                isSensitive = isSensitive,
                style = MangalaTypography.Size14Regular(),
                color = MaterialTheme.mangalaColors.textSecondary,
                privacyDisplayMode = privacyDisplayMode
            )

            Spacer(modifier = Modifier.width(Spacing.XTINY))

            // Always show copy button, copy full address even when privacy mode is enabled
            DocumentCopyButton(
                textToCopy = textToCopy,
                label = "Copy Address",
                onCopyComplete = onCopyComplete,
                iconTint = MaterialTheme.mangalaColors.iconSecondary,
            )

            ContactQrButton(
                onShowQrCodeClick = onClickQrCode
            )

            onClickStar?.let {
                IconButton(
                    onClick = onClickStar,
                    modifier = Modifier.size(Dimensions.IconButtonSize)
                ) {
                    Icon(
                        imageVector = if (isFavorite) ContactIcon.Star else ContactIcon.OutlineStar,
                        contentDescription = "Toggle Favorite",
                        tint = if (isFavorite) MaterialTheme.mangalaColors.iconFavoriteStar else MaterialTheme.mangalaColors.iconSecondary,
                        modifier = Modifier.size(Dimensions.IconSize)
                    )
                }
            }
        }
    }
}

@Composable
fun ColumnScope.ContactColumnWithActionRowAndMultipleBlockchainsRow(
    contact: ContactWithMultipleBlockchainsModel,
    privacyModeEnabled: Boolean,
    isDisplayStar: Boolean = false,
    onStarClick: ((ContactModel) -> Unit)? = null,
    onQrCodeClick: (ContactModel) -> Unit,
) {
    val contactModel = remember(contact) { contact.toContactModel() }
    val (networkShow, amountOfNetworkCompacted) = remember(contact) {
        contact.additionalBlockchainTypes.distinct().let {
            Pair(
                sequence {
                    yield(contact.primaryBlockchainSymbol to contact.primaryBlockchainIcon)
                    yieldAll(
                        it.asSequence().take(2)
                            .map { blockchainTypeEntity -> blockchainTypeEntity.symbol to blockchainTypeEntity.icon })
                }.toList(),
                (it.size - 2).takeIf { count -> count > 0 }?.let { count -> "+$count" })
        }
    }

    val onQrCodeClickRef = rememberUpdatedState(onQrCodeClick)
    val onStarClickRef = rememberUpdatedState(onStarClick)
    val onQrCodeClickRemembered = remember(contactModel) {
        {
            onQrCodeClickRef.value(contactModel)
        }
    }
    val onStarClickRemembered = remember(contactModel) {
        onStarClickRef.value?.let {
            {
                it(contactModel)
            }
        }
    }

    MaxWidthRow(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ContactRowWithActions(
            contactId = contact.contactId,
            contactName = contact.contactName,
            avatar = contact.avatar,
            textToCopy = contact.primaryWalletAddress,
            onClickQrCode = onQrCodeClickRemembered,
            isFavorite = contact.isFavorite,
            isDisplayStar = isDisplayStar,
            onClickStar = onStarClickRemembered,
            privacyModeEnabled = privacyModeEnabled,
            isSensitive = contact.primaryWalletSensitive ?: false,
            address = contact.primaryWalletAddress,
            privacyDisplayMode = contact.privacyDisplayMode
        )
    }

    // Network icons row
    MaxWidthRow(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            start = Dimensions.AddressBookContactAvatarSize + Spacing.TINY,
        ),
        horizontalArrangement = Arrangement.spacedBy(Spacing.XTINY),
    ) {
        networkShow.forEach { (symbol, icon) ->
            BlockchainIconBox(
                symbol = symbol,
                size = 16.dp,
                iconSize = 12.dp,
                iconPath = icon
            )
        }

        amountOfNetworkCompacted?.let {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = MaterialTheme.mangalaColors.bg,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = amountOfNetworkCompacted,
                    style = MangalaTypography.Size8SemiBold(),
                    color = MaterialTheme.mangalaColors.textSecondary,
                )
            }
        }
    }
}