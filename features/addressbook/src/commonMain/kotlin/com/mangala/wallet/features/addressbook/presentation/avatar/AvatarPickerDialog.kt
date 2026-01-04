package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource

@Composable
fun AvatarPickerDialog(
    onDismiss: () -> Unit,
    onAvatarSelected: (AvatarSource) -> Unit,
    viewModel: AvatarPickerViewModel,
    currentAvatar: AvatarSource? = null,
    entityName: String = "",
    allowRemove: Boolean = true
) {
    val avatarSource by viewModel.currentAvatar.collectAsState()
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showDefaultAvatarPicker by remember { mutableStateOf(false) }

    // Set initial avatar
    LaunchedEffect(currentAvatar) {
        viewModel.setInitialAvatar(currentAvatar)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Tiêu đề
                Text(
                    text = "Chọn avatar",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Hiển thị avatar hiện tại
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    AvatarIcon(
                        name = entityName.ifEmpty { "Preview" },
                        iconString = AvatarSource.toString(avatarSource),
                        size = 96.dp,
                        borderWidth = 1.dp
                    )
                }

                // Các tùy chọn
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Tùy chọn emoji
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { showEmojiPicker = true }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEmotions,
                            contentDescription = "Chọn emoji",
                            modifier = Modifier
                                .size(48.dp)
                                .padding(8.dp)
                        )
                        Text(
                            text = "Emoji",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Tùy chọn avatar mặc định
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { showDefaultAvatarPicker = true }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Chọn avatar mặc định",
                            modifier = Modifier
                                .size(48.dp)
                                .padding(8.dp)
                        )
                        Text(
                            text = "Mặc định",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Tùy chọn ảnh (chỉ hiển thị nếu picker được hỗ trợ)
                    if (viewModel.isImagePickerSupported()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.openImagePicker() }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Photo,
                                contentDescription = "Chọn ảnh",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(8.dp)
                            )
                            Text(
                                text = "Hình ảnh",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Tùy chọn xóa (nếu cho phép)
                    if (allowRemove && avatarSource !is AvatarSource.None) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    viewModel.onAvatarRemoved()
                                    onAvatarSelected(AvatarSource.None)
                                    onDismiss()
                                }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Xóa",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(8.dp)
                            )
                            Text(
                                text = "Xóa",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Nút hủy/xác nhận
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onAvatarSelected(avatarSource)
                            onDismiss()
                        }
                    ) {
                        Text("Xác nhận")
                    }
                }
            }
        }
    }

    // Hiển thị emoji picker nếu cần
    if (showEmojiPicker) {
        EmojiPickerDialog(
            onDismiss = { showEmojiPicker = false },
            onEmojiSelected = { emoji ->
                viewModel.onEmojiSelected(emoji)
                showEmojiPicker = false  // Đóng EmojiPickerDialog sau khi chọn
            }
        )
    }
    
    // Hiển thị dialog chọn avatar mặc định nếu cần
    if (showDefaultAvatarPicker) {
        DefaultAvatarPickerDialog(
            onDismiss = { showDefaultAvatarPicker = false },
            onAvatarSelected = { resourceName ->
                viewModel.onDefaultAvatarSelected(resourceName)
                showDefaultAvatarPicker = false
            }
        )
    }
}