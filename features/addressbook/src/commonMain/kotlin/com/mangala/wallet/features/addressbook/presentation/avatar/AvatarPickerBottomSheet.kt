package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.features.addressbook.data.model.avatar.AvatarHistoryEntity
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarRenderer
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * Expect function for platform-specific wrapper
 */
@Composable
expect fun AvatarPickerBottomSheetWrapper(
    onDismiss: () -> Unit,
    onAvatarSelected: (AvatarSource) -> Unit,
    viewModel: AvatarPickerViewModel,
    currentAvatar: AvatarSource? = null,
    entityName: String = "",
    allowRemove: Boolean = true,
    modifier: Modifier = Modifier
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarPickerBottomSheet(
    onDismiss: () -> Unit,
    onAvatarSelected: (AvatarSource) -> Unit,
    viewModel: AvatarPickerViewModel,
    currentAvatar: AvatarSource? = null,
    entityName: String = "",
    allowRemove: Boolean = true,
    modifier: Modifier = Modifier
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var searchQuery by remember { mutableStateOf("") }
    val avatarSource by viewModel.currentAvatar.collectAsState()
    
    // States for nested bottom sheets
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showDefaultAvatarPicker by remember { mutableStateOf(false) }
    var showRemoveConfirmDialog by remember { mutableStateOf(false) }

    // Set initial avatar
    LaunchedEffect(currentAvatar) {
        viewModel.setInitialAvatar(currentAvatar)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = modifier,
        containerColor = MaterialTheme.mangalaColors.bg,
        contentColor = MaterialTheme.mangalaColors.textPrimary,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.mangalaColors.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding() // Safe area padding for gesture navigation
                .navigationBarsPadding() // Ensure content is above navigation bar
                .padding(horizontal = 16.dp)
        ) {
            // Header với back button và title
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
                        tint = MaterialTheme.mangalaColors.iconPrimary
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Recent",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.width(32.dp)) // Balance for IconButton
            }

            // Bỏ tab selector

            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    viewModel.searchAvatars(it)
                },
                placeholder = "Search avatars...",
            )

            // Content based on selected tab and search state
            if (searchQuery.isNotEmpty()) {
                SearchResultsGrid(
                    searchResults = viewModel.searchResults.collectAsState().value,
                    onAvatarSelected = { historyEntity ->
                        viewModel.onHistoryAvatarSelected(historyEntity)
                        onAvatarSelected(historyEntity.avatarSource)
                        onDismiss()
                    }
                )
            } else {
                MainAvatarGrid(
                    viewModel = viewModel,
                    onAvatarSelected = onAvatarSelected,
                    onDismiss = onDismiss,
                    showEmojiPicker = { showEmojiPicker = true }
                )
            }

            // Remove photo button
            if (allowRemove && avatarSource !is AvatarSource.None) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        showRemoveConfirmDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.mangalaColors.buttonDestructiveContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.mangalaColors.buttonDestructiveContainer)
                ) {
                    Text(
                        text = "Remove Photo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
    
    // Remove confirmation dialog
    if (showRemoveConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveConfirmDialog = false },
            title = {
                Text(
                    text = "Remove Photo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove this photo?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRemoveConfirmDialog = false
                        // First set the avatar to None in viewModel
                        viewModel.onAvatarRemoved()
                        // Then notify parent with the updated value from viewModel
                        onAvatarSelected(viewModel.currentAvatar.value)
                        // Finally close the bottom sheet
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.mangalaColors.buttonDestructiveContainer
                    )
                ) {
                    Text(
                        text = "Remove",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRemoveConfirmDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.mangalaColors.textSecondary
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
    }
    
    // Show nested bottom sheets
    if (showEmojiPicker) {
        EmojiPickerBottomSheet(
            onDismiss = { showEmojiPicker = false },
            onEmojiSelected = { emoji, backgroundColor ->
                viewModel.onEmojiWithBackgroundSelected(emoji, backgroundColor)
                onAvatarSelected(viewModel.currentAvatar.value)
                showEmojiPicker = false
                onDismiss()
            },
            currentAvatar = avatarSource
        )
    }
    
    if (showDefaultAvatarPicker) {
        DefaultAvatarPickerBottomSheet(
            onDismiss = { showDefaultAvatarPicker = false },
            onAvatarSelected = { resourceName ->
                viewModel.onDefaultAvatarSelected(resourceName)
                onAvatarSelected(viewModel.currentAvatar.value)
                showDefaultAvatarPicker = false
                onDismiss()
            }
        )
    }
}

@Composable
private fun AvatarSelectionItem(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    backgroundBrush: Brush? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .then(
                if (backgroundBrush != null) {
                    Modifier.background(backgroundBrush)
                } else {
                    Modifier.background(backgroundColor)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// =====================================================
// HISTORY COMPONENTS
// =====================================================

// TabButton đã bị xóa vì không cần thiết

@Composable
private fun RecentAvatarsGrid(
    recentAvatars: List<AvatarHistoryEntity>,
    onAvatarSelected: (AvatarHistoryEntity) -> Unit,
    onLoadMore: () -> Unit,
    hasMorePages: Boolean,
    modifier: Modifier = Modifier
) {
    if (recentAvatars.isEmpty()) {
        EmptyRecentState(modifier = modifier)
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = modifier.heightIn(max = 400.dp)
        ) {
            items(recentAvatars) { historyEntity ->
                HistoryAvatarItem(
                    historyEntity = historyEntity,
                    onClick = { onAvatarSelected(historyEntity) }
                )
            }

            if (hasMorePages) {
                item {
                    LoadMoreButton(onClick = onLoadMore)
                }
            }
        }
    }
}

@Composable
private fun SearchResultsGrid(
    searchResults: List<AvatarHistoryEntity>,
    onAvatarSelected: (AvatarHistoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (searchResults.isEmpty()) {
        EmptySearchState(modifier = modifier)
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = modifier.heightIn(max = 400.dp)
        ) {
            items(searchResults) { historyEntity ->
                HistoryAvatarItem(
                    historyEntity = historyEntity,
                    onClick = { onAvatarSelected(historyEntity) }
                )
            }
        }
    }
}

@Composable
private fun MainAvatarGrid(
    viewModel: AvatarPickerViewModel,
    onAvatarSelected: (AvatarSource) -> Unit,
    onDismiss: () -> Unit,
    showEmojiPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recentAvatars = viewModel.recentAvatars.collectAsState().value

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        modifier = modifier.heightIn(max = 400.dp)
    ) {
        // Ô đầu tiên: Upload from device option (Album)
        item {
            AvatarSelectionItem(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable {
                        if (viewModel.isImagePickerSupported()) {
                            viewModel.openImagePicker()
                        }
                    },
                backgroundColor = MaterialTheme.mangalaColors.bgInnerCard
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Choose from album",
                        tint = MaterialTheme.mangalaColors.iconSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Album",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.mangalaColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Ô thứ hai: Emoji option
        item {
            AvatarSelectionItem(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable { showEmojiPicker() },
                backgroundColor = Color.Transparent,
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF69898),
                        Color(0xFFF859BB)
                    )
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "😂",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Emoji",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Ô thứ ba trở đi: Recent avatars từ history (nếu có)
        items(recentAvatars) { historyEntity ->
            HistoryAvatarItem(
                historyEntity = historyEntity,
                onClick = {
                    viewModel.onHistoryAvatarSelected(historyEntity)
                    onAvatarSelected(historyEntity.avatarSource)
                    onDismiss()
                }
            )
        }

        // Không hiển thị placeholder nếu không có recent avatars
    }
}

@Composable
private fun HistoryAvatarItem(
    historyEntity: AvatarHistoryEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.mangalaColors.bgInnerCard)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Hiển thị avatar hình vuông cho history - fill toàn bộ ô
        SquareAvatarRenderer(
            avatarSource = historyEntity.avatarSource,
            fillContainer = true,
            modifier = Modifier.matchParentSize()
        )

        // Usage count badge
        if (historyEntity.usageCount > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .background(
                        MaterialTheme.mangalaColors.textLink,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = historyEntity.usageCount.toString(),
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyRecentState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No recent avatars",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.mangalaColors.textSecondary
        )
        Text(
            text = "Your recently used avatars will appear here",
            fontSize = 12.sp,
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun EmptySearchState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No results found",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.mangalaColors.textSecondary
        )
        Text(
            text = "Try a different search term",
            fontSize = 12.sp,
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun LoadMoreButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.mangalaColors.bgInnerCard)
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack, // Use appropriate icon
                contentDescription = "Load more",
                tint = MaterialTheme.mangalaColors.iconSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "More",
                fontSize = 10.sp,
                color = MaterialTheme.mangalaColors.textSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Custom avatar renderer cho history grid - hiển thị hình vuông
 */
@Composable
private fun SquareAvatarRenderer(
    avatarSource: AvatarSource,
    size: Dp = 40.dp,
    fillContainer: Boolean = false,
    modifier: Modifier = Modifier
) {
    when (avatarSource) {
        is AvatarSource.Emoji -> {
            // Hiển thị emoji
            Box(
                modifier = if (fillContainer) {
                    modifier.background(Color.Transparent)
                } else {
                    modifier.size(size).background(Color.Transparent)
                },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarSource.emoji,
                    fontSize = if (fillContainer) 32.sp else (size.value * 0.6).sp
                )
            }
        }
        is AvatarSource.ImageUrl -> {
            // Hiển thị ảnh hình vuông
            SquareImageRenderer(
                imageUrl = avatarSource.url,
                fillContainer = fillContainer,
                size = size,
                modifier = modifier
            )
        }
        is AvatarSource.DefaultAvatar -> {
            // Hiển thị default avatar placeholder
            Box(
                modifier = modifier
                    .size(size)
                    .background(MaterialTheme.mangalaColors.bgInnerCard),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarSource.resourceName.take(2),
                    color = MaterialTheme.mangalaColors.textSecondary,
                    fontSize = (size.value * 0.3).sp
                )
            }
        }
        is AvatarSource.None -> {
            // Empty state
            Box(
                modifier = modifier
                    .size(size)
                    .background(MaterialTheme.mangalaColors.bgInnerCard)
            )
        }
    }
}

/**
 * Platform-specific image renderer cho hình vuông
 */
@Composable
expect fun SquareImageRenderer(
    imageUrl: String,
    fillContainer: Boolean = false,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
)