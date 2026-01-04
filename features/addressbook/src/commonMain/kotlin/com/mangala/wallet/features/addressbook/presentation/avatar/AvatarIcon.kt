package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import androidx.compose.foundation.Image
import com.mangala.wallet.common.mokoresources.Dimensions
import com.seiko.imageloader.rememberImagePainter

/**
 * Composable hiển thị avatar cho entity hỗ trợ AvatarSupport.
 * Hiển thị theo thứ tự ưu tiên:
 * 1. Ảnh từ thư viện (nếu có)
 * 2. Emoji (nếu có)
 * 3. Chữ cái đầu của tên (fallback)
 */
@Composable
fun AvatarIcon(
    name: String,
    iconString: String?,
    modifier: Modifier = Modifier,
    size: Dp = Dimensions.AddressBookContactAvatarSize,
    borderWidth: Dp = 0.dp,
    borderColor: Color? = null,
    backgroundColor: Color? = null,
    contentColor: Color? = null
) {
    val avatarSource = AvatarSource.fromString(iconString)

    // Sử dụng AvatarRenderer cho nền tảng cụ thể
    AvatarRenderer.RenderAvatar(
        name = name,
        avatarSource = avatarSource,
        modifier = modifier,
        size = size,
        borderWidth = borderWidth,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}