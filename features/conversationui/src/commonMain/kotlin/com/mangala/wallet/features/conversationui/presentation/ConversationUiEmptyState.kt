package com.mangala.wallet.features.conversationui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ColumnScope.ConversationUiEmptyState() {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Start a conversation",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.mangalaColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Type a message below to begin chatting",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.mangalaColors.textSecondary
        )
    }
}