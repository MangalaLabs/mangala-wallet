package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily

data class FilterOptions(
    val hideZeroBalances: Boolean = false,
    val sortBy: TokenSortBy = TokenSortBy.NAME,
    val viewMode: ViewMode = ViewMode.SINGLE_ACCOUNT
)

enum class TokenSortBy(val displayName: String) {
    NAME("Name"),
    BALANCE("Balance"),
    VALUE("USDT Value")
}

enum class ViewMode(val displayName: String) {
    SINGLE_ACCOUNT("Single Account"),
    ALL_ACCOUNTS("All Accounts")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentOptions: FilterOptions,
    onFilterOptionsChanged: (FilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = Color(0xFF1D263E),
        contentColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            
            // Title
            Text(
                text = "Filter & Sort",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontFamily = getInterFontFamily(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            
            // Hide Zero Balances Toggle
            FilterToggleItem(
                title = "Hide Zero Balances",
                isSelected = currentOptions.hideZeroBalances,
                onClick = {
                    onFilterOptionsChanged(currentOptions.copy(hideZeroBalances = !currentOptions.hideZeroBalances))
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sort By Section
            Text(
                text = "Sort By",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = getInterFontFamily(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            
            TokenSortBy.entries.forEach { sortBy ->
                FilterSelectableItem(
                    title = sortBy.displayName,
                    isSelected = currentOptions.sortBy == sortBy,
                    onClick = {
                        onFilterOptionsChanged(currentOptions.copy(sortBy = sortBy))
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // View Mode Section
            Text(
                text = "View Mode",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = getInterFontFamily(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            
            ViewMode.entries.forEach { viewMode ->
                FilterSelectableItem(
                    title = viewMode.displayName,
                    isSelected = currentOptions.viewMode == viewMode,
                    onClick = {
                        onFilterOptionsChanged(currentOptions.copy(viewMode = viewMode))
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FilterToggleItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2A3E6C),
                        Color(0xFF2A3E6C)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.8f),
                fontFamily = getInterFontFamily()
            )
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WalletThemeV2.Colors.accentBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterSelectableItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = if (isSelected) {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3B90FF),
                            Color(0xFFC27DFF)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2A3E6C),
                            Color(0xFF2A3E6C)
                        )
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = WalletThemeV2.Colors.primaryText.copy(alpha = if (isSelected) 0.95f else 0.8f),
                fontFamily = getInterFontFamily()
            )
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WalletThemeV2.Colors.accentBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}