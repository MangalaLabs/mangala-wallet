package com.mangala.wallet.features.addressbook.presentation.avatar


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.domain.model.*

/**
 * Button với AvatarIcon và tính năng chỉnh sửa
 *
 * @param support Entity hiển thị avatar
 * @param onPickAvatar Callback khi cần chọn avatar mới
 * @param modifier Modifier
 * @param size Kích thước avatar
 * @param showEditButton Hiển thị nút chỉnh sửa hay không
 * @param enabled Button có thể tương tác không
 */
@Composable
fun AvatarPickerButton(
    avatar: Avatar,
    onPickAvatar: (AvatarSource) -> Unit,
    viewModel: AvatarPickerViewModel,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    showEditButton: Boolean = true,
    enabled: Boolean = true,
    borderWidth: Dp = 1.dp,
    borderColor: Color? = null,
    backgroundColor: Color? = null,
    textColor: Color? = null
) {
    var showAvatarPicker by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // Avatar
        AvatarIcon(
            name = avatar.name,
            iconString = AvatarSource.toString(avatar.avatarSource),
            modifier = Modifier,
            size = size,
            borderWidth = borderWidth,
            borderColor = borderColor,
            backgroundColor = backgroundColor,
            contentColor = textColor
        )

        // Edit button
        if (showEditButton) {
            // Camera button với hiệu ứng tương tự Figma
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        color = Color(0xCCCCCCCC), // Màu có độ trong suốt để tạo hiệu ứng blur
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .clickable(enabled = enabled) { showAvatarPicker = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MangalaWalletPack.Camera,
                    contentDescription = "Edit Avatar",
                    modifier = Modifier.size(16.dp), // Kích thước icon từ Figma
                    tint = Color(0xFF292929) // Màu từ Figma #292929
                )
            }
        }
    }

    // Avatar picker dialog
    if (showAvatarPicker) {
        AvatarPickerDialog(
            onDismiss = { showAvatarPicker = false },
            onAvatarSelected = { avatarSource ->
                onPickAvatar(avatarSource)
                showAvatarPicker = false
            },
            viewModel = viewModel,
            currentAvatar = avatar.avatarSource,
            entityName = avatar.name
        )
    }
}