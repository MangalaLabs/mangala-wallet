package com.mangala.wallet.features.addressbook.presentation.contact.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.ColorsNew

@Composable
fun NotesSection(
    notes: String,
    onChangeValue: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = ColorsNew.white,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Note",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ColorsNew.black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = if (notes.isNotEmpty()) notes else "No notes",
                fontSize = 14.sp,
                color = if (notes.isNotEmpty()) ColorsNew.primary_500 else ColorsNew.primary_400,
                fontWeight = FontWeight.Normal
            )
        }
    }
}