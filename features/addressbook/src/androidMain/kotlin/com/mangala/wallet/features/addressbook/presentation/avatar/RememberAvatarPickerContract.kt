package com.mangala.wallet.features.addressbook.presentation.avatar


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.mangala.wallet.features.addressbook.data.local.avatar.AndroidAvatarPickerContract
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource

/**
 * Composable để tạo và đăng ký AndroidAvatarPickerContract
 */
@Composable
fun rememberAvatarPickerContract(
    onImageSelected: (String) -> Unit
): AndroidAvatarPickerContract {
    val context = LocalContext.current

    // Tạo contract
    val contract = remember(context) {
        AndroidAvatarPickerContract(context, onImageSelected)
    }

    // Launcher để chọn ảnh
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageSelected(it.toString())
        }
    }

    // Launcher để yêu cầu quyền truy cập
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Quyền được cấp, mở bộ chọn ảnh
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
        contract.setImageLaunchCallback { contentType ->
            currentLaunchImage.value.invoke(contentType)
        }
        
        contract.setPermissionCallback { needed ->
            if (needed) {
                currentRequestPermission.value.invoke(true)
            }
        }
        
        onDispose { /* Clean up if needed */ }
    }

    return contract
}

/**
 * Composable wrapper cho AvatarPickerDialog sử dụng AndroidAvatarPickerContract
 */
@Composable
fun AndroidAvatarPicker(
    onDismiss: () -> Unit,
    onAvatarSelected: (AvatarSource) -> Unit,
    currentAvatar: AvatarSource? = null,
    entityName: String = "",
    allowRemove: Boolean = true
) {
    // Tạo contract với callback khi chọn ảnh
    val avatarPickerContract = rememberAvatarPickerContract { imagePath ->
        onAvatarSelected(AvatarSource.ImageUrl(imagePath))
        onDismiss()
    }

    // Tạo ViewModel với contract
    val viewModel = remember {
        AvatarPickerViewModel(avatarPickerContract)
    }

    // Hiển thị dialog
    AvatarPickerDialog(
        onDismiss = onDismiss,
        onAvatarSelected = onAvatarSelected,
        viewModel = viewModel,
        currentAvatar = currentAvatar,
        entityName = entityName,
        allowRemove = allowRemove
    )
}