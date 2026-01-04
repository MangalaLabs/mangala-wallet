package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.presentation.contact.recent.BlockchainIcons.getBackgroundColorForSymbol
import com.mangala.wallet.features.addressbook.utils.getImageResourceForSymbol
import com.mangala.wallet.features.addressbook.utils.getImageResourceFromPath
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * Enhanced ContactItem with privacy mode support
 * This demonstrates how to integrate ObfuscatedAddress into existing contact list items
 */
@Composable
fun ContactItemWithPrivacy(
    contact: ContactWithMultipleBlockchainsModel,
    privacyModeEnabled: Boolean,
    onContactClick: (ContactModel) -> Unit = {},
    onStarClick: ((ContactModel) -> Unit)? = null,
    onQrCodeClick: (ContactModel) -> Unit = { _ -> },
    isLoading: Boolean = false,
) {
    val contactModel = remember(contact) { contact.toContactModel() }

    val onContactClickRef = rememberUpdatedState(onContactClick)
    val onContactClickRemembered = remember(contactModel) {
        {
            onContactClickRef.value(contactModel)
        }
    }

    MaxWidthColumn(
        modifier = Modifier
            .mangalaWalletPlaceholder(
                visible = isLoading,
                color = MaterialTheme.mangalaColors.skeletonBase,
                highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
                shape = RoundedCornerShape(CornerRadius.Small),
            )
            .clip(RoundedCornerShape(CornerRadius.Small))
            .clickable(onClick = onContactClickRemembered)
            .background(MaterialTheme.mangalaColors.bgInnerCard)
            .padding(
                vertical = Dimensions.Padding.small,
                horizontal = Dimensions.Padding.default
            ),
    ) {
        ContactColumnWithActionRowAndMultipleBlockchainsRow(
            contact = contact,
            privacyModeEnabled = privacyModeEnabled,
            onStarClick = onStarClick,
            onQrCodeClick = onQrCodeClick,
        )
    }
}


@Composable
fun BlockchainIconBox(
    symbol: String,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    iconSize: Dp = 12.dp,
    iconPath: String? = null, // Still need default value because some composable use Model that does not have iconPath
) {
    val backgroundColor = remember(symbol) {
        getBackgroundColorForSymbol(symbol)
    }
    val imageResource = remember(symbol, iconPath) {
        // Use database icon path if provided, otherwise fallback to symbol mapping
        if (iconPath != null) {
            getImageResourceFromPath(iconPath)
        } else {
            getImageResourceForSymbol(symbol)
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Nếu có image resource thì sử dụng, không thì sử dụng icon
        if (imageResource != null) {

            LocalImage(
                modifier = Modifier.size(iconSize),
                imageResource = imageResource,
            )
        }
    }
}
