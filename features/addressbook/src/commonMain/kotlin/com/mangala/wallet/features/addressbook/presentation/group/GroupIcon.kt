package com.mangala.wallet.features.addressbook.presentation.group

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarIcon

/**
 * Composable for displaying a group icon based on the icon field in the database.
 * If icon starts with "emoji:", it displays the emoji.
 * Otherwise, it displays the first letter of the group name.
 *
 * @param name The name of the group (used for fallback)
 * @param icon The icon string from the database (can be null)
 * @param backgroundColor The background color for the circle (for first letter fallback)
 * @param modifier Additional modifier for customization
 */
@Composable
fun GroupIcon(
    name: String,
    icon: String?,
    backgroundColor: Color,
    modifier: Modifier = Modifier.size(40.dp),
    size: Dp = 40.dp,
) {
    AvatarIcon(
        name = name,
        iconString = icon,
        modifier = modifier,
        backgroundColor = backgroundColor.copy(alpha = 0.2f),
        contentColor = backgroundColor,
        borderWidth = 0.dp,
        size = size
    )
}
