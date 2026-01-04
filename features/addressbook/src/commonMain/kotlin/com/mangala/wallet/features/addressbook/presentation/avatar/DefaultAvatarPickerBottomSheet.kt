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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAvatarPickerBottomSheet(
    onDismiss: () -> Unit,
    onAvatarSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var searchQuery by remember { mutableStateOf("") }
    
    // List of default avatars - using simple placeholders for now
    val defaultAvatars = remember {
        listOf(
            "Avatar1", "Avatar2", "Avatar3", "Avatar4", "Avatar5",
            "Avatar6", "Avatar7", "Avatar8", "Avatar9"
        )
    }
    
    // Filter avatars based on search query
    val filteredAvatars = remember(searchQuery, defaultAvatars) {
        if (searchQuery.isBlank()) {
            defaultAvatars
        } else {
            defaultAvatars.filter { name ->
                name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF262626)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Default Avatars",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF262626),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.width(32.dp)) // Balance for IconButton
            }

            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search avatars...",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp)
            )

            // Default avatars grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(0.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(filteredAvatars) { resourceName ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(Color.White)
                            .clickable {
                                onAvatarSelected(resourceName)
                                onDismiss()
                            }
                            .padding(16.dp)
                    ) {
                        // Simple placeholder avatar
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F0F0))
                                .border(1.dp, Color(0xFFE0E0E0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = resourceName.take(2),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6D6D6D)
                            )
                        }
                    }
                }
                
                // Fill remaining slots with placeholder if needed
                val remainingSlots = (3 - (filteredAvatars.size % 3)) % 3
                items(remainingSlots) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(Color.White)
                    )
                }
            }

            // Bottom padding for safe area
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}