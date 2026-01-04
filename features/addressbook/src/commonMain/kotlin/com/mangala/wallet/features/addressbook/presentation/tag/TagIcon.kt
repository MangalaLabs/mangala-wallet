package com.mangala.wallet.features.addressbook.presentation.tag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarIcon

/**
 * Composable for displaying a tag icon based on the icon field in the database.
 * Similar to GroupIcon, this uses the AvatarIcon component for consistent avatar rendering.
 * If icon starts with "emoji:", it displays the emoji.
 * Otherwise, it displays the first letter of the tag name.
 *
 * @param name The name of the tag (used for fallback)
 * @param icon The icon string from the database (can be null)
 * @param backgroundColor The background color for the circle (for first letter fallback)
 * @param modifier Additional modifier for customization
 * @param size The size of the avatar
 */
@Composable
fun TagIcon(
    name: String,
    icon: String?,
    backgroundColor: Color = ColorsNew.tagTeal, // Sử dụng màu tagTeal từ ColorsNew
    contentColor: Color? = null,
    modifier: Modifier = Modifier,
    size: Dp = Dimensions.AddressBookContactAvatarSize,
    useFullOpacityBackground: Boolean = false, // Thêm tham số cho phép sử dụng opacity đầy đủ
) {
    val finalBackgroundColor = remember(useFullOpacityBackground, backgroundColor) {
        if (useFullOpacityBackground) {
            backgroundColor
        } else {
            backgroundColor.copy(alpha = 0.2f) // Mặc định giữ nguyên hành vi cũ
        }
    }

    // Use contentColor if provided, otherwise auto-detect based on background brightness
    val finalContentColor = remember(contentColor, backgroundColor) {
        contentColor ?: run {
            // Auto-detect text color based on background brightness
            if (backgroundColor.luminance() > 0.5f) {
                Color.Black
            } else {
                Color.White
            }
        }
    }


    AvatarIcon(
        name = name,
        iconString = icon,
        modifier = modifier,
        size = size,
        backgroundColor = finalBackgroundColor,
        contentColor = finalContentColor,
        borderWidth = 0.dp
    )
} 