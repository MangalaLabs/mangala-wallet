package com.mangala.wallet.features.addressbook.presentation.components.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalendarBottomActions(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    primaryGradient: Brush,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF666666)
            ),
            border = BorderStroke(1.5.dp, Color(0xFFE0E0E0)),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Text("Hủy", fontWeight = FontWeight.W500, fontSize = 14.sp)
        }
        
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6366F1)
            ),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Text("Xác nhận", fontWeight = FontWeight.W500, fontSize = 14.sp, color = Color.White)
        }
    }
}
