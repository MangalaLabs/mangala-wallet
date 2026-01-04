package com.mangala.wallet.features.addressbook.presentation.avatar

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.mangala.wallet.features.addressbook.data.local.avatar.AndroidAvatarPickerContract
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource

/**
 * Android specific wrapper cho AvatarPickerBottomSheet
 * Đăng ký ActivityResultLauncher cho AndroidAvatarPickerContract
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
    // Lấy contract từ viewModel
    val contract = viewModel.avatarPickerContract as? AndroidAvatarPickerContract
    
    // Tạo launcher để chọn ảnh
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Thông báo cho callback trong contract
            contract?.onImageSelected?.invoke(selectedUri.toString())
            viewModel.onImageSelected(selectedUri.toString())
            // Gọi onAvatarSelected với content URI - sẽ được upload trong saveGroup
            onAvatarSelected(AvatarSource.ImageUrl(selectedUri.toString()))
            onDismiss()
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
    
    // Đăng ký callback với contract
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