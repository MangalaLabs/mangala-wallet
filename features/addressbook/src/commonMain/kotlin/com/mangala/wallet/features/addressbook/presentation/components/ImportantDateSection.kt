package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.features.addressbook.domain.model.CalendarType
import com.mangala.wallet.features.addressbook.domain.model.ImportantDate
import com.mangala.wallet.features.addressbook.domain.model.ImportantDateCategory
import kotlinx.datetime.LocalDate

@Composable
fun ImportantDateSection(
    importantDates: List<ImportantDate>,
    onAddDate: () -> Unit,
    onRemoveDate: (String) -> Unit,
    onEditDate: (ImportantDate) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Important Dates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (isEditable) {
                IconButton(
                    onClick = onAddDate,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Important Date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (importantDates.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "No important dates added",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                importantDates.forEach { date ->
                    ImportantDateItem(
                        importantDate = date,
                        onRemove = if (isEditable) ({ onRemoveDate(date.id) }) else null,
                        onEdit = if (isEditable) ({ onEditDate(date) }) else null
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportantDateItem(
    importantDate: ImportantDate,
    onRemove: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onEdit != null) {
                    Modifier.clickable { onEdit() }
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon/color
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(importantDate.category).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = getCategoryColor(importantDate.category),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = importantDate.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = formatDate(importantDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (importantDate.calendarType == CalendarType.LUNAR) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${importantDate.lunarDate?.displayName ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                if (importantDate.notes.isNotEmpty()) {
                    Text(
                        text = importantDate.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            if (onRemove != null) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Date",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun getCategoryColor(category: ImportantDateCategory): Color {
    return when (category) {
        ImportantDateCategory.BIRTHDAY -> MaterialTheme.colorScheme.primary
        ImportantDateCategory.ANNIVERSARY -> MaterialTheme.colorScheme.tertiary
        ImportantDateCategory.HOLIDAY -> MaterialTheme.colorScheme.secondary
        ImportantDateCategory.BUSINESS -> MaterialTheme.colorScheme.onSurfaceVariant
        ImportantDateCategory.OTHER -> MaterialTheme.colorScheme.outline
    }
}

private fun formatDate(importantDate: ImportantDate): String {
    return when (importantDate.calendarType) {
        CalendarType.SOLAR -> {
            // Show solar date for Solar calendar type
            val date = importantDate.date
            "${date.dayOfMonth}/${date.monthNumber}/${date.year} (Dương lịch)"
        }
        CalendarType.LUNAR -> {
            // Show lunar date for Lunar calendar type
            val lunarDate = importantDate.lunarDate
            if (lunarDate != null) {
                "${lunarDate.day}/${lunarDate.month}${if (lunarDate.isLeapMonth) " nhuận" else ""}/${lunarDate.yearCycle} (Âm lịch)"
            } else {
                // Fallback to solar date if lunar data is missing
                val date = importantDate.date
                "${date.dayOfMonth}/${date.monthNumber}/${date.year} (Âm lịch - converted)"
            }
        }
    }
}