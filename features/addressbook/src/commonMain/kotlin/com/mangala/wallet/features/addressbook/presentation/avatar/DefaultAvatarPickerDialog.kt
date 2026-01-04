package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun DefaultAvatarPickerDialog(
    onDismiss: () -> Unit,
    onAvatarSelected: (String) -> Unit
) {
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
                Text(
                    text = "Chọn avatar mặc định",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Hiển thị grid các avatar mặc định
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(5) { index ->
                        val resourceName = when (index) {
                            0 -> "AvatarA"
                            1 -> "AvatarB"
                            2 -> "AvatarC"
                            3 -> "AvatarD"
                            4 -> "AvatarE"
                            else -> "AvatarA" // Fallback
                        }
                        
                        val imageResource = when (index) {
                            0 -> MR.images.AvatarA
                            1 -> MR.images.AvatarB
                            2 -> MR.images.AvatarC
                            3 -> MR.images.AvatarD
                            4 -> MR.images.AvatarE
                            else -> MR.images.AvatarA // Fallback
                        }
                        
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    onAvatarSelected(resourceName)
                                }
                        ) {
                            Image(
                                painter = painterResource(imageResource),
                                contentDescription = "Avatar $resourceName",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.LightGray, CircleShape)
                            )
                        }
                    }
                }
                
                // Nút hủy/đóng
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy")
                    }
                }
            }
        }
    }
}