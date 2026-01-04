package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder

@Composable
fun PnlDisplay(
    pnlAmountFormatted: String?,
    pnlColor: Color,
    isBalanceHidden: Boolean = false,
    labelText: String = "24h: ",
    labelFontSize: TextUnit = WalletThemeV2.Typography.fontSizeBody,
    valueFontSize: TextUnit = WalletThemeV2.Typography.fontSizeBody,
    labelColor: Color = WalletThemeV2.Colors.secondaryText,
    hiddenPlaceholder: String? = null,
    selectedCurrency: AntelopeAccountBalanceUnit? = null,
    showLabel: Boolean = true,
    modifier: Modifier = Modifier
) {
    val actualHiddenPlaceholder = hiddenPlaceholder ?: "+*,*** ${selectedCurrency?.symbol.orEmpty()} (+*.**%)"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (showLabel) {
            Text(
                text = labelText,
                fontSize = labelFontSize,
                color = labelColor,
                fontFamily = getInterFontFamily(),
                modifier = Modifier.mangalaWalletPlaceholder(
                    visible = pnlAmountFormatted == null && !isBalanceHidden
                )
            )
        }

        Text(
            text = when {
                isBalanceHidden -> actualHiddenPlaceholder
                pnlAmountFormatted != null -> pnlAmountFormatted
                else -> "+0.00 ${selectedCurrency?.symbol.orEmpty()} (+0.00%)"
            },
            fontSize = valueFontSize,
            fontWeight = FontWeight.Medium,
            color = when {
                isBalanceHidden -> WalletThemeV2.Colors.secondaryText
                pnlAmountFormatted != null -> pnlColor
                else -> WalletThemeV2.Colors.secondaryText.copy(alpha = 0.5f)
            },
            fontFamily = getInterFontFamily(),
            modifier = Modifier.mangalaWalletPlaceholder(
                visible = pnlAmountFormatted == null && !isBalanceHidden
            )
        )
    }
}