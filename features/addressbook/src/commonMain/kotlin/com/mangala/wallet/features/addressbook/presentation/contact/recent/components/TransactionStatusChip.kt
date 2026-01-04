package com.mangala.wallet.features.addressbook.presentation.contact.recent.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TransactionError
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TransactionPending
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TransactionSuccess
import com.mangala.wallet.features.addressbook.data.model.enum.TransactionStatus
import com.mangala.wallet.features.addressbook.utils.getStatusColor
import com.mangala.wallet.features.addressbook.utils.getStatusText
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.theme.MangalaTypography

@Composable
fun TransactionStatusChip(
    status: TransactionStatus,
    modifier: Modifier = Modifier
) {
    val statusColor = remember(status) {
        getStatusColor(status.name)
    }
    
    val statusIcon = remember(status) {
        when (status) {
            TransactionStatus.DRAFT -> MangalaWalletPack.TransactionPending
            TransactionStatus.PENDING -> MangalaWalletPack.TransactionPending
            TransactionStatus.CONFIRMED -> MangalaWalletPack.TransactionSuccess
            TransactionStatus.FAILED -> MangalaWalletPack.TransactionError
        }
    }
    
    val statusText = remember(status) {
        getStatusText(status.name)
    }

    MaxWidthColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Status icon
        Image(
            imageVector = statusIcon,
            contentDescription = "Transaction Status",
            modifier = Modifier.size(40.dp),
        )

        Spacer(modifier = Modifier.height(Spacing.XTINY))

        // Status text
        Text(
            text = statusText,
            style = MangalaTypography.Size13Medium(),
            color = statusColor,
        )
    }
}