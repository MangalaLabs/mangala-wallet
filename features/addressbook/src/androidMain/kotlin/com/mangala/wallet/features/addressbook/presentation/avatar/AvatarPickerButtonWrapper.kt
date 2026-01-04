package com.mangala.wallet.features.addressbook.presentation.avatar

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.mangala.wallet.features.addressbook.data.local.avatar.AndroidAvatarPickerContract
import com.mangala.wallet.features.addressbook.domain.model.Avatar
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource

/**
 * Android specific wrapper cho AvatarPickerButton
 * Đăng ký ActivityResultLauncher cho AndroidAvatarPickerContract
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
    // Lấy contract từ viewModel
    val contract = viewModel.avatarPickerContract as? AndroidAvatarPickerContract
    
    // Tạo launcher để chọn ảnh - bây giờ không lưu trữ trong contract
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Thông báo cho callback trong contract
            contract?.onImageSelected?.invoke(selectedUri.toString())
            viewModel.onImageSelected(selectedUri.toString())
            // Gọi onPickAvatar ngay để cập nhật UI ngay lập tức
            onPickAvatar(AvatarSource.ImageUrl(selectedUri.toString()))
        }
    }
    
    // Tạo launcher để xin quyền
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Nếu đã có quyền, mở image picker
            imageLauncher.launch("image/*")
        }
    }
    
    // Đăng ký callback (không phải launcher) với contract
    val currentLaunchImage = rememberUpdatedState { contentType: String ->
        imageLauncher.launch(contentType)
    }
    
    val currentRequestPermission = rememberUpdatedState { _: Boolean ->
        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    
    DisposableEffect(contract) {
        contract?.setImageLaunchCallback { contentType ->
            currentLaunchImage.value.invoke(contentType)
        }
        
        contract?.setPermissionCallback { needed ->
            if (needed) {
                currentRequestPermission.value.invoke(true)
            }
        }
        
        onDispose { /* Clean up if needed */ }
    }
    
    // Sử dụng component gốc
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