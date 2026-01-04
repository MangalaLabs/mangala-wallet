package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Camera
import com.mangala.wallet.features.addressbook.di.AvatarFactory
import com.mangala.wallet.features.addressbook.domain.model.Avatar
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarPickerBottomSheetWrapper
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarRenderer

/**
 * Avatar section with camera button for contact photo
 */
@Composable
fun AvatarSection(
    icon: String,
    onAvatarSelected: (String) -> Unit,
    contactName: String = ""
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.mangalaColors.bg,
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            var showAvatarPicker by remember { mutableStateOf(false) }
            
            val currentAvatarSource = remember(icon) {
                val parsed = AvatarSource.fromString(icon)
                parsed
            }

            // Tạo giả lập đối tượng Avatar
            val avatar = Avatar(name = contactName.ifEmpty { "A" }, avatarSource = currentAvatarSource)

            // Callback khi chọn ảnh
            val onImageSelected = remember {
                { path: String ->
                    onAvatarSelected(path)
                }
            }

            // Sử dụng AvatarFactory để tạo ViewModel
            val avatarPickerViewModel = remember {
                AvatarFactory.createAvatarPickerViewModel(onImageSelected)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar display with camera icon - similar to CreateGroupScreenNew
                Box(
                    modifier = Modifier.size(96.dp)
                ) {
                    AvatarRenderer.RenderAvatar(
                        name = avatar.name,
                        avatarSource = avatar.avatarSource,
                        size = 96.dp
                    )

                    // Camera icon overlay
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomEnd)
                            .clickable { showAvatarPicker = true }
                            .background(
                                color = MaterialTheme.mangalaColors.iconSecondary,
                                shape = CircleShape
                            )
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.Camera,
                            contentDescription = "Change avatar",
                            tint = MaterialTheme.mangalaColors.iconPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Avatar picker bottom sheet
            if (showAvatarPicker) {
                AvatarPickerBottomSheetWrapper(
                    onDismiss = { showAvatarPicker = false },
                    onAvatarSelected = { source: AvatarSource ->
                        val iconString = AvatarSource.toString(source)
                        // Always call onIconSelected, even for null (AvatarSource.None)
                        onAvatarSelected(iconString ?: "")
                        showAvatarPicker = false
                    },
                    viewModel = avatarPickerViewModel,
                    currentAvatar = currentAvatarSource,
                    entityName = contactName
                )
            }
        }
    }
}