package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.ColorsNew

/**
 * Horizontal list of wallet type chips with single selection
 */
@Composable
fun ContactTypeChips(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    onCustomType: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Thêm debug log chi tiết hơn để xác nhận loại đã chọn
    println("ContactTypeChips - Current selectedType: '$selectedType', length: ${selectedType.length}, isEmpty: ${selectedType.isEmpty()}")
    
    // Sử dụng danh sách cố định để đảm bảo consistency
    val types = listOf("Hot Wallet", "Cold Storage", "Exchange", "DeFi")

    // Sử dụng Column thay vì LazyRow trực tiếp để thêm label
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        // Thêm label để làm rõ mục đích của các chip
        androidx.compose.material3.Text(
            text = "Wallet Type",
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(types) { type ->
                val isSelected = type == selectedType
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        println("ContactTypeChips - Selected type: $type")
                        onTypeSelected(type)
                    },
                    label = { Text(text = type) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = if (isSelected) ColorsNew.primary_100 else Color.Transparent,
                        labelColor = if (isSelected) ColorsNew.blueActionButton else ColorsNew.primary_600,
                        selectedContainerColor = ColorsNew.primary_100,
                        selectedLabelColor = ColorsNew.blueActionButton
                    )
                )
            }

            item {
                AssistChip(
                    onClick = onCustomType,
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Custom Type")
                        }
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.Transparent,
                        labelColor = ColorsNew.primary_600
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = ColorsNew.primary_200,
                        borderWidth = 1.dp,
                    )
                )
            }
        }
    }
}