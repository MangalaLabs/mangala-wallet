package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.mangala.wallet.features.addressbook.domain.model.Avatar
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource

/**
 * Desktop triển khai đơn giản, chỉ chuyển tiếp đến AvatarPickerButton
 */
@Composable
actual fun AvatarPickerButtonWrapper(
    avatar: Avatar,
    onPickAvatar: (AvatarSource) -> Unit,
    viewModel: AvatarPickerViewModel,
    modifier: Modifier,
    size: Dp,
    showEditButton: Boolean,
    enabled: Boolean,
    borderWidth: Dp,
    borderColor: Color?,
    backgroundColor: Color?,
    textColor: Color?
) {
    // Chuyển tiếp đến component thông thường
    AvatarPickerButton(
        avatar = avatar,
        onPickAvatar = onPickAvatar,
        viewModel = viewModel,
        modifier = modifier,
        size = size,
        showEditButton = showEditButton,
        enabled = enabled,
        borderWidth = borderWidth,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
        textColor = textColor
    )
}