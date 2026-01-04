package com.mangala.wallet.features.addressbook.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.features.addressbook.domain.model.CalendarContext

@Composable
fun QuickSelectSection(
    onQuickSelect: (QuickSelectType) -> Unit,
    calendarContext: CalendarContext? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        QuickSelectPill("Hôm nay", calendarContext) { onQuickSelect(QuickSelectType.TODAY) }
        QuickSelectPill("Mùng 1", calendarContext) { onQuickSelect(QuickSelectType.MUNG_1) }
        QuickSelectPill("Rằm", calendarContext) { onQuickSelect(QuickSelectType.RAM) }
        QuickSelectPill("Cuối tháng", calendarContext) { onQuickSelect(QuickSelectType.END_OF_MONTH) }
    }
}

@Composable
private fun QuickSelectPill(
    text: String,
    calendarContext: CalendarContext? = null,
    onClick: () -> Unit
) {
    // Dynamic colors based on leap month context
    val backgroundColor = if (calendarContext?.isInLeapMonth == true) Color(0xFFFFF5F5) else Color(0xFFF0F5FF)
    val borderColor = if (calendarContext?.isInLeapMonth == true) Color(0xFFFFB3B3) else Color(0xFFE0E5FF)
    val textColor = if (calendarContext?.isInLeapMonth == true) Color(0xFFFF6B6B) else Color(0xFF6366F1)
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, fontSize = 12.sp, color = textColor, fontWeight = FontWeight.Medium)
    }
}
