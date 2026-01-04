package com.mangala.wallet.features.addressbook.presentation.contact.recent.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun TransactionAmountSection(
    formattedAmount: String,
    usdEquivalent: String? = null,
    modifier: Modifier = Modifier
) {
    MaxWidthColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Amount
        Text(
            text = formattedAmount,
            style = MangalaTypography.Size17SemiBold(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        usdEquivalent?.let { usd ->
            Spacer(modifier = Modifier.height(Spacing.XTINY))
            
            // USD Equivalent
            Text(
                text = usd,
                style = MangalaTypography.Size14Regular(),
                color = MaterialTheme.mangalaColors.textSecondary
            )
        }
    }
}