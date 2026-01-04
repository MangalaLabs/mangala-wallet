package com.mangala.wallet.features.addressbook.presentation.contact.recent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.ColorsNew

@Composable
fun AlphabetIndex(
    letters: List<Char>,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        letters.forEach { letter ->
            Text(
                text = letter.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ColorsNew.primary_600,
                modifier = Modifier
                    .clickable { onLetterSelected(letter) }
                    .padding(vertical = 2.dp)
            )
        }
    }
}