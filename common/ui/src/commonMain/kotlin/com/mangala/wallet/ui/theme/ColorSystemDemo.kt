package com.mangala.wallet.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ColorSystemDemo() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.mangalaColors.bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Mangala Color System Demo",
                color = MaterialTheme.mangalaColors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            ColorItem("Background", MaterialTheme.mangalaColors.bg)
        }
        
        item {
            ColorItem("Inner Card Background", MaterialTheme.mangalaColors.bgInnerCard)
        }
        
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = MaterialTheme.mangalaColors.bgHighlight,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Highlight Background (Gradient)",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.mangalaColors.bgInnerCard,
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 2.dp,
                        brush = MaterialTheme.mangalaColors.borderHighlight,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Highlight Border (Gradient)",
                    color = MaterialTheme.mangalaColors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Primary Text",
                    color = MaterialTheme.mangalaColors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Secondary Text",
                    color = MaterialTheme.mangalaColors.textSecondary,
                    fontSize = 14.sp
                )
                Text(
                    text = "Link Text",
                    color = MaterialTheme.mangalaColors.textLink,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ColorItem(
    name: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.mangalaColors.bgInnerCard,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                MaterialTheme.mangalaColors.border,
                RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, RoundedCornerShape(4.dp))
                .border(1.dp, MaterialTheme.mangalaColors.border, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            color = MaterialTheme.mangalaColors.textPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}