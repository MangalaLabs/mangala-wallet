package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.OutlineStar
import com.mangala.wallet.features.addressbook.icon.contacticon.Star
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * Header component for the Add Contact screen
 * Contains back button, title, and favorite button
 */
@Composable
fun ContactHeader(
    title: String,
    onBackClick: () -> Unit,
    isFavorite: Boolean = false,
    onFavoriteToggle: () -> Unit = {}
) {
    MaxWidthRow(
        modifier = Modifier.padding(vertical = Dimensions.Padding.small)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(48.dp) // Ensure 48dp touch target
        ) {
            Icon(
                imageVector = MangalaWalletPack.IcBack,
                contentDescription = "Back",
                tint = MaterialTheme.mangalaColors.iconPrimary,
                modifier = Modifier.size(Dimensions.IconSize_24) // Keep icon visually same size
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Title
        Text(
            text = title,
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onFavoriteToggle,
            modifier = Modifier.size(48.dp) // Ensure 48dp touch target
        ) {
            Icon(
                imageVector = if (isFavorite) ContactIcon.Star else ContactIcon.OutlineStar,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (isFavorite) MaterialTheme.mangalaColors.iconFavoriteStar else MaterialTheme.mangalaColors.iconPrimary,
                modifier = Modifier.size(Dimensions.IconSize) // Keep icon visually same size
            )
        }
    }
}