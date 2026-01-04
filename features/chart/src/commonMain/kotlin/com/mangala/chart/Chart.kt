package com.mangala.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.utils.ext.formatCompact

@Composable
fun Chart(
    modifier: Modifier = Modifier,
    chartModifier: Modifier,
    token: TokenBalanceModel?,
    showPriceChangePercentage: Boolean = true,
    isLoading: Boolean
) {
    Chart(
        modifier = modifier,
        chartModifier = chartModifier,
        items = token?.sparklineIn7d?.price,
        priceChangePercentage = try {
            token?.priceChangePercentage7d?.let { BigDecimal.parseString(it) }
        } catch (e: Exception) {
            null
        },
        showPriceChangePercentage = showPriceChangePercentage,
        isLoading = isLoading
    )
}

@Composable
fun Chart(
    modifier: Modifier = Modifier,
    items: List<Double>?,
    priceChangePercentage: BigDecimal?,
    showPriceChangePercentage: Boolean = true,
    chartModifier: Modifier = Modifier,
    isLoading: Boolean = items.isNullOrEmpty()
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val firstItem = items?.firstOrNull()
        val lastItem = items?.lastOrNull()

        if (firstItem != null && lastItem != null && !isLoading) {
            val color = if (firstItem < lastItem) Colors.green else Colors.coral

            if (showPriceChangePercentage && priceChangePercentage != null) {
                Text(
                    priceChangePercentage.formatCompact(2) + "%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight(400),
                    color = color
                )
                Spacer(Modifier.height(6.dp))
            }
            TokenChart(
                chartModifier,
                sparklineData = items,
                color = color,
            )
        } else {
            // We need to show placeholder in a separate component because setting it directly in the chart will cause the chart to not render after load finishes
            Box(Modifier.mangalaWalletPlaceholder(isLoading, modifier = chartModifier))
            if (showPriceChangePercentage) {
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier.mangalaWalletPlaceholder(
                        isLoading,
                        modifier = Modifier.size(56.dp, 18.dp)
                    )
                )
            }
        }
    }
}