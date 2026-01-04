package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.features.addressbook.domain.model.AvatarConstants
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.utils.rgbToHex

// Background colors for emoji selection
private val BACKGROUND_COLORS = listOf(
    Color(0xFF00A699), // Teal
    Color(0xFF4095F9), // Blue  
    Color(0xFFFF6B6B), // Red
    Color(0xFF4ECDC4), // Light Teal
    Color(0xFFFFE66D), // Yellow
    Color(0xFF95E1D3), // Mint
    Color(0xFFFCE38A), // Light Yellow
    Color(0xFFFF8B94), // Pink
    Color(0xFF957FEF), // Purple
    Color(0xFF6C5CE7), // Dark Purple
    Color(0xFF00CEC9), // Cyan
    Color(0xFFE17055), // Orange
    Color(0xFF81ECEC), // Light Cyan
    Color(0xFFFD79A8), // Light Pink
    Color(0xFF6C5CE7), // Lavender
    Color(0xFF00B894)  // Green
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiPickerBottomSheet(
    onDismiss: () -> Unit,
    onEmojiSelected: (emoji: String, backgroundColor: String) -> Unit,
    currentAvatar: AvatarSource? = null,
    emojis: List<String> = AvatarConstants.ALL_EMOJIS,
    modifier: Modifier = Modifier
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    // Initialize từ current avatar nếu có
    val initialEmoji = if (currentAvatar is AvatarSource.Emoji) currentAvatar.emoji else "😂"
    val initialBackgroundColor = if (currentAvatar is AvatarSource.Emoji && currentAvatar.backgroundColor != null) {
        try {
            Color(currentAvatar.backgroundColor.removePrefix("#").toLong(16) or 0xFF000000)
        } catch (e: Exception) {
            BACKGROUND_COLORS[0]
        }
    } else {
        BACKGROUND_COLORS[0]
    }
    
    var selectedBackgroundColor by remember { mutableStateOf(initialBackgroundColor) }
    var selectedEmoji by remember { mutableStateOf(initialEmoji) }
    
    // Helper function to convert Color to hex string
    fun colorToHex(color: Color): String {
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        return rgbToHex(red, green, blue)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = modifier,
        containerColor = Color.White,
        contentColor = Color(0xFF262626),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFD9D9D9))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Header với back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.IcBack,
                        contentDescription = "Back",
                        tint = Color(0xFF262626)
                    )
                }
                
                Spacer(modifier = Modifier.width(32.dp)) // Space để cân bằng với IconButton
            }

            // Avatar preview ở giữa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(selectedBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedEmoji,
                        fontSize = 48.sp
                    )
                }
            }

            // Choose background color section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Choose background color",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF262626),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Background color grid - 8 columns according to Figma
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 120.dp) // Limit height for 2 rows
                ) {
                    items(BACKGROUND_COLORS) { color ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedBackgroundColor == color) 1.dp else 0.dp,
                                    color = if (selectedBackgroundColor == color) Color(0xFF00A699) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedBackgroundColor = color
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Choose emoji section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Choose emoji",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF262626),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Emoji grid - 8 columns according to Figma, hiển thị tất cả emoji theo thứ tự category
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp) // Tăng lại height vì không có category tabs
                ) {
                    items(emojis) { emoji ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(selectedBackgroundColor)
                                .clickable {
                                    selectedEmoji = emoji
                                    // Gọi callback với emoji và background color
                                    onEmojiSelected(emoji, colorToHex(selectedBackgroundColor))
                                }
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                    }
                }
            }

            // Bottom padding for safe area
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}