package com.mangala.wallet.features.addressbook.presentation.contact.recent.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ReceiveButton
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SendButton
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun TransactionAmountDisplay(
    formattedAmount: String,
    isSender: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            imageVector = if (isSender) MangalaWalletPack.SendButton else MangalaWalletPack.ReceiveButton,
            contentDescription = if (isSender) "Sent" else "Received",
            tint = MaterialTheme.mangalaColors.iconPrimary,
            modifier = Modifier.size(Dimensions.IconButtonSize14)
        )

        Spacer(modifier = Modifier.width(Spacing.XTINY))

        Text(
            text = formattedAmount,
            color = MaterialTheme.mangalaColors.textPrimary,
            style = MangalaTypography.Size14Medium()
        )
    }
}