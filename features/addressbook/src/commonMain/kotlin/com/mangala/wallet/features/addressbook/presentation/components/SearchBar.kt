package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * SearchBar component tái sử dụng cho các màn hình trong ứng dụng
 *
 * @param query Giá trị text hiện tại trong thanh tìm kiếm
 * @param onQueryChange Callback khi người dùng thay đổi text
 * @param placeholder Text hiển thị khi ô tìm kiếm trống
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search",
) {
    // Use BasicTextField for more control over the appearance - No wrapper Box needed
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp) // Height from Figma design
                .clip(RoundedCornerShape(12.dp))
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = Color(0x1A000000)
                )
                .background(
                    color = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = RoundedCornerShape(12.dp)
                ),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.mangalaColors.textPrimary
            ),
            cursorBrush = SolidColor(MaterialTheme.mangalaColors.textPrimary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(20.dp), // Fixed inner content height
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search icon with explicit alignment
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.mangalaColors.iconSecondary,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterVertically)
                    )

                    // Content area with proper alignment
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // Show placeholder if empty
                        if (query.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = MaterialTheme.mangalaColors.textSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(),
                                style = TextStyle(
                                    lineHeight = 16.sp // Match icon height for better alignment
                                )
                            )
                        }
                        // Actual text field
                        innerTextField()
                    }
                }
            }
        )
    }
}