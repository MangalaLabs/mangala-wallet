package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource

/**
 * iOS implementation - chỉ gọi trực tiếp component gốc
 * vì iOS có thể có cách xử lý image picker khác
 */
@Composable
actual fun AvatarPickerBottomSheetWrapper(
    onDismiss: () -> Unit,
    onAvatarSelected: (AvatarSource) -> Unit,
    viewModel: AvatarPickerViewModel,
    currentAvatar: AvatarSource?,
    entityName: String,
    allowRemove: Boolean,
    modifier: Modifier
) {
    AvatarPickerBottomSheet(
        onDismiss = onDismiss,
        onAvatarSelected = onAvatarSelected,
        viewModel = viewModel,
        currentAvatar = currentAvatar,
        entityName = entityName,
        allowRemove = allowRemove,
        modifier = modifier
    )
}