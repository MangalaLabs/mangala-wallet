package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.domain.model.Avatar
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource

/**
 * Wrapper cho AvatarPickerButton với hỗ trợ đa nền tảng
 * Trên Android, wrapper này sẽ cung cấp các ActivityResultLauncher cần thiết
 * Trên các nền tảng khác, nó sẽ chuyển tiếp đến AvatarPickerButton thông thường
 */
@Composable
expect fun AvatarPickerButtonWrapper(
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
)