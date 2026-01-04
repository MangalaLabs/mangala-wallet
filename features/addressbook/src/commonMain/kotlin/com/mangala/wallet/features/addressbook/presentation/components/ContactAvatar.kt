package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.Star
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarIcon
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ContactAvatar(
    contactId: String,
    contactName: String = "Contact",
    isFavorite: Boolean = false,
    isDisplayStar: Boolean = true,
    iconString: String? = null,
) {
    // Don't use default avatar if iconString is null - let AvatarIcon handle the fallback
    Box(
        modifier = Modifier.size(40.dp),
    ) {
        AvatarIcon(
            name = contactName,
            iconString = iconString,
        )

        if (isFavorite && isDisplayStar) {
            Icon(
                imageVector = ContactIcon.Star,
                contentDescription = "Favorite",
                tint = MaterialTheme.mangalaColors.iconFavoriteStar,
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}
