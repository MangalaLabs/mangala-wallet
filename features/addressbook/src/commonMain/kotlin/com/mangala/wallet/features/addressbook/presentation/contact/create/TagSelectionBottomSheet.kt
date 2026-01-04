package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.platform.LocalConfiguration
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.utils.stringToColor
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.features.addressbook.presentation.tag.TagIcon
import com.mangala.wallet.ui.component.KeyboardDismissBox

/**
 * Bottom Sheet for tag selection based on Figma design
 * Displays alphabetically categorized tags with search functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelectionBottomSheet(
    tags: List<TagEntity>,
    selectedTagIds: Set<String>,
    onTagSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    onTagCreated: (() -> Unit)? = null // Thêm callback để thông báo khi tạo tag mới thành công
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    // Store tags in internal state to enable refreshing and updating counts
    var currentTags by remember { mutableStateOf(tags) }
    
    // Map to track temporary contact counts for tags while in the bottom sheet
    val localTagContactCounts = remember { mutableMapOf<String, Int>() }
    
    // Initialize local counts from original tag data and track original selection state
    val originalSelectedState = remember { mutableMapOf<String, Boolean>() }
    
    LaunchedEffect(tags, selectedTagIds) {
        currentTags = tags
        // Initialize the local contact counts with values from tags
        tags.forEach { tag ->
            // Determine if tag is currently selected
            val isSelectedNow = selectedTagIds.contains(tag.id)
            val baseCount = tag.contactCount ?: 0
            
            // For selected tags, we need to add 1 to the count since the database
            // counts don't include this contact yet (it hasn't been saved)
            val adjustedCount = if (isSelectedNow) baseCount + 1 else baseCount
            
            localTagContactCounts[tag.id] = adjustedCount
            // Track which tags were originally selected
            originalSelectedState[tag.id] = isSelectedNow
        }
        
        // Now update the displayed tags with adjusted counts
        currentTags = currentTags.map { tag ->
            val adjustedCount = localTagContactCounts[tag.id] ?: tag.contactCount ?: 0
            tag.copy(contactCount = adjustedCount)
        }
    }
    
    // State to control Create Tag bottom sheet visibility
    var showCreateTagBottomSheet by remember { mutableStateOf(false) }
    
    // Show Create Tag modal bottom sheet if needed
    if (showCreateTagBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateTagBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.mangalaColors.bg,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            // Use the new composable version
            CreateTagBottomSheetContent(
                onTagCreated = { tagEntity ->
                    // Select the newly created tag and continue with current bottom sheet
                    onTagSelected(tagEntity.id)
                    // Gọi callback để thông báo tạo tag thành công
                    onTagCreated?.invoke()
                    showCreateTagBottomSheet = false
                },
                onDismiss = { 
                    showCreateTagBottomSheet = false 
                }
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.mangalaColors.bg,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        TagSelectionBottomSheetContent(
            tags = currentTags,
            selectedTagIds = selectedTagIds,
            onTagSelected = { tagId ->
                // Call the parent handler to update the selection
                onTagSelected(tagId)
                
                // Update the contact count locally in UI for immediate feedback
                val wasSelected = selectedTagIds.contains(tagId)
                val willBeSelected = !wasSelected // Selection will toggle after call
                val tag = currentTags.find { it.id == tagId } ?: return@TagSelectionBottomSheetContent
                
                // Update the local count map based on change from original state
                val currentCount = localTagContactCounts[tagId] ?: tag.contactCount ?: 0

                val newCount = when {
                    // Selected → Unselected: Remove 1 from the count
                    wasSelected && !willBeSelected -> {
                        (currentCount - 1).coerceAtLeast(0)
                    }
                    // Unselected → Selected: Add 1 to the count
                    !wasSelected && willBeSelected -> {
                        currentCount + 1
                    }
                    // No change (shouldn't happen, but for safety)
                    else -> currentCount
                }
                
                localTagContactCounts[tagId] = newCount
                
                // Update the tags list with updated contact count
                currentTags = currentTags.map { existingTag -> 
                    if (existingTag.id == tagId) {
                        existingTag.copy(contactCount = newCount)
                    } else {
                        existingTag
                    }
                }
            },
            onDismiss = onDismiss,
            onCreateTagClick = { showCreateTagBottomSheet = true }
        )
    }
}

@Composable
private fun TagSelectionBottomSheetContent(
    tags: List<TagEntity>,
    selectedTagIds: Set<String>,
    onTagSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    onCreateTagClick: () -> Unit
) {

    KeyboardDismissBox(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime) // Use WindowInsets directly
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.6f) // Max 60% but can expand
                .background(MaterialTheme.mangalaColors.bg)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Title
            Text(
                text = "Add tag",
                style = MangalaTypography.Size14Medium(),
                color = MaterialTheme.mangalaColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            // Search bar
            var searchQuery by remember { mutableStateOf("") }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = "Search your tag"
                    )
                }

            // Add button - now connected to createTag functionality
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.mangalaColors.bgInnerCard)
                    .clickable { onCreateTagClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create new tag",
                    tint = MaterialTheme.mangalaColors.iconPrimary
                )
            }
        }

        // Filter tags based on search query
        val filteredTags = remember(searchQuery, tags) {
            if (searchQuery.isEmpty()) {
                tags
            } else {
                tags.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }
            }
        }

        // Group tags by first letter alphabetically
        val groupedTags = remember(filteredTags) {
            filteredTags.groupBy {
                it.name.firstOrNull()?.uppercaseChar() ?: '#'
            }
        }

        // Tag list with alphabetic grouping
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Make LazyColumn take remaining space
                .padding(horizontal = 16.dp)
        ) {
            if (filteredTags.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No tags found",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.mangalaColors.textSecondary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        MangalaGradientButton(
                            label = "Create new tag",
                            onClick = onCreateTagClick,
                            size = MangalaButtonSize.Medium
                        )
                    }
                }
            } else {
                groupedTags.forEach { (letter, tagsInGroup) ->
                    item {
                        Text(
                            text = letter.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.mangalaColors.textSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(tagsInGroup) { tag ->
                        TagItem(
                            tag = tag,
                            isSelected = selectedTagIds.contains(tag.id),
                            onClick = { onTagSelected(tag.id) }
                        )
                    }
                }
            }
        }

            // Bottom button section with Surface for better elevation
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // Ensure button is above navigation bar
                    .padding(
                        horizontal = Dimensions.Padding.default,
                        vertical = Dimensions.Padding.small
                    ),
                color = MaterialTheme.mangalaColors.bg
            ) {
            Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MangalaGradientButton(
                        label = "Add tag",
                        onClick = onDismiss,
                        size = MangalaButtonSize.Big,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedTagIds.isNotEmpty()
                    )
                }
            }
        }
    }
}

@Composable
private fun TagItem(
    tag: TagEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.mangalaColors.bgBadge else MaterialTheme.mangalaColors.bgInnerCard)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use TagIcon component for consistent avatar display
            val backgroundColor = if (tag.color.toIntOrNull() != null) {
                // If it's a valid index, use indexToColor directly
                ColorsNew.indexToColor(tag.color.toInt())
            } else {
                // Otherwise fall back to stringToColor for hex values
                stringToColor(tag.color, ColorsNew.primary_500)
            }
            
            // Calculate text color
            val textColor = if (tag.textColor?.toIntOrNull() != null) {
                // If it's a valid index, use indexToColor directly
                ColorsNew.indexToColor(tag.textColor.toInt())
            } else {
                // Otherwise fall back to stringToColor for hex values
                if (tag.textColor != null) {
                    stringToColor(tag.textColor, Color.White)
                } else {
                    // If textColor is null, calculate the appropriate contrast color
                    stringToColor(tag.calculateTextColor(), Color.White)
                }
            }
            
            TagIcon(
                name = tag.name,
                icon = tag.icon,
                backgroundColor = backgroundColor,
                contentColor = textColor,
                modifier = Modifier.size(40.dp),
                useFullOpacityBackground = true
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Tag info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tag.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary
                )

                Text(
                    text = "${tag.contactCount ?: 0} contact",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.mangalaColors.textSecondary
                )
            }
        }
    }
}