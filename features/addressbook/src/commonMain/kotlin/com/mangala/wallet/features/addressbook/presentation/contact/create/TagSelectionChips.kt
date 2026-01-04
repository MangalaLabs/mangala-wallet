package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mangala.wallet.ui.theme.mangalaColors


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TagSelectionChips(
    availableTags: List<TagEntity>,
    selectedTags: Set<String>,
    onTagSelected: (String) -> Unit,
    onAddTagClick: () -> Unit,
    onTagCreated: (() -> Unit)? = null, // Thêm callback để thông báo khi tạo tag mới thành công
    onCreateTag: ((name: String, color: String) -> Unit)? = null, // Replace screenModel with callback
    isCreatingTag: Boolean = false, // New parameter for loading state
    modifier: Modifier = Modifier
) {
    // State to control bottom sheets visibility
    var showTagSelectionBottomSheet by remember { mutableStateOf(false) }
    var showCreateTagBottomSheet by remember { mutableStateOf(false) }
    var isCreatingTag by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Show tag selection bottom sheet when needed
    if (showTagSelectionBottomSheet) {
        TagSelectionBottomSheet(
            tags = availableTags,
            selectedTagIds = selectedTags,
            onTagSelected = { tagId ->
                onTagSelected(tagId)
                // Keep the bottom sheet open for multiple selections
                // Manual refresh is handled in the TagSelectionBottomSheet itself
            },
            onDismiss = {
                showTagSelectionBottomSheet = false
            },
            onTagCreated = {
                onTagCreated?.invoke()
            }
        )
    }

    // Show create tag bottom sheet when needed
    if (showCreateTagBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showCreateTagBottomSheet = false
                errorMessage = null
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            // Use the enhanced composable version
            CreateTagBottomSheetContentWithCallback(
                isLoading = isCreatingTag,
                errorMessage = errorMessage,
                onTagCreate = { name, color ->
                    isCreatingTag = true
                    errorMessage = null

                    if (onCreateTag != null) {
                        onCreateTag(name, color)
                        isCreatingTag = false
                        showCreateTagBottomSheet = false
                        onTagCreated?.invoke()
                    } else {
                        // Fallback if onCreateTag is not provided
                        isCreatingTag = false
                        errorMessage = "Create tag handler not available"
                    }
                },
                onCancel = {
                    showCreateTagBottomSheet = false
                    errorMessage = null
                }
            )
        }
    }

    // Show loading indicator when creating tag
    if (isCreatingTag) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.mangalaColors.iconPrimary
                )
                Text(
                    text = "Creating tag...",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.mangalaColors.textSecondary
                )
            }
        }
    } else {
        FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display selected tags from database
            availableTags.filter {
                it.name != "Favorite" && selectedTags.contains(it.id)
            }.forEach { tag ->
                val isSelected = selectedTags.contains(tag.id)
                // val tagColor = stringToColor(tag.color, MaterialTheme.mangalaColors.bg)
                // val backgroundColor = tagColor.copy(alpha = 0.15f)

                // Custom chip to control exact padding
                Surface(
                    modifier = Modifier.height(26.dp),
                    onClick = { onTagSelected(tag.id) },
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.mangalaColors.bgTagLight,
                    border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.mangalaColors.border) else null
                ) {
                    Text(
                        text = tag.name,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        ),
                        color = MaterialTheme.mangalaColors.textTag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // "+ Tag" button/chip styled like in Figma design with custom padding
            Surface(
                modifier = Modifier.height(26.dp),
                onClick = {
                    // Show the bottom sheet after "+ Tag" is clicked
                    if (availableTags.isEmpty()) {
                        // If no tags exist, directly show create tag sheet
                        showCreateTagBottomSheet = true
                    } else {
                        // Otherwise show selection sheet which has create option
                        showTagSelectionBottomSheet = true
                    }

                    // Call the provided handler for additional handling
                    onAddTagClick()
                },
                shape = RoundedCornerShape(50),
                color = MaterialTheme.mangalaColors.bg,
                border = BorderStroke(1.dp, MaterialTheme.mangalaColors.textLink)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = MaterialTheme.mangalaColors.textLink
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tag",
                        color = MaterialTheme.mangalaColors.textLink,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}

