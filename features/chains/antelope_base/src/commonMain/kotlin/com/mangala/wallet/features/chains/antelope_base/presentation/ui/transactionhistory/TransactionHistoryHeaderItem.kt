package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun TransactionHistoryHeaderItem(item: TransactionHistoryItemAntelope.HeaderItem, paddingHorizontal: Dp = 0.dp) {
    MaxWidthColumn(
        Modifier.padding(
            top = Dimensions.Padding.large,
            bottom = Dimensions.Padding.half,
            start = paddingHorizontal,
            end = paddingHorizontal
        )
    ) {
        val text = when (item) {
            is TransactionHistoryItemAntelope.HeaderItem.Date -> {
                item.date
            }

            is TransactionHistoryItemAntelope.HeaderItem.Today -> {
                MR.strings.message_transaction_history_date_today.desc().localized()
            }

            else -> {
                MR.strings.message_transaction_history_date_yesterday.desc().localized()
            }
        }
        TextDescription2(text, color = MaterialTheme.mangalaColors.textPrimary, fontWeight = FontWeight.Medium)
    }
}