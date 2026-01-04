package com.mangala.wallet.features.addressbook.presentation.contact.recent.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.mangala.wallet.features.addressbook.utils.getStatusColor
import com.mangala.wallet.features.addressbook.utils.getStatusText
import com.mangala.wallet.ui.theme.MangalaTypography

@Composable
fun TransactionStatusText(
    status: String,
    modifier: Modifier = Modifier
) {
    val statusText = remember(status) { getStatusText(status) }
    val statusColor = remember(status) { getStatusColor(status) }
    
    Text(
        text = statusText,
        style = MangalaTypography.Size12Regular(),
        color = statusColor,
        modifier = modifier
    )
}