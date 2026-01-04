package com.mangala.wallet.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.font.FontWeight
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun TotalTransactionValue(
    value: String?
) {
    MaxWidthRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL),
        verticalAlignment = Alignment.Bottom
    ) {
        TextNormal(
            MR.strings.all_total.desc().localized(),
            color = MaterialTheme.mangalaColors.textPrimary,
            modifier = Modifier.alignBy(FirstBaseline),
        )
        Row(Modifier.alignBy(FirstBaseline)) {
            TextTitle4(
                modifier = Modifier.mangalaWalletPlaceholder(
                    value == null,
                ), // TODO: Confirm loading design,
                text = value
                    ?: "           ", // Blank string so that the label always align to this text, even when the placeholder is showing or not
                color = MaterialTheme.mangalaColors.textSecondary,
                fontWeight = FontWeight.W500
            )
        }

    }
}