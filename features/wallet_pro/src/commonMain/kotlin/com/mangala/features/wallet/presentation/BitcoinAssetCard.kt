package com.mangala.features.wallet.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.ui.utils.formattedCompactBalance
import com.mangala.wallet.utils.ext.formatCompact
import com.mangala.wallet.utils.onClickIfNotLoading

@Composable
fun BitcoinAssetCard(
    balanceModel: TokenBalanceModel?,
    fiatCurrencySymbol: String,
    isBalanceVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    enabled: Boolean = false
) {
    AssetCard(
        symbol = balanceModel?.contractSymbol.orEmpty(),
        imageSource = ImageSource.Resource(balanceModel?.localImage),
        name = balanceModel?.contractName.orEmpty(),
        formattedPrice = fiatCurrencySymbol + balanceModel?.currentPrice.formatCompact(),
        isBalanceVisible = isBalanceVisible,
        onClick = { onClickIfNotLoading(isLoading, onClick) },
        sparklineData = balanceModel?.sparklineIn7d?.price,
        priceChangePercentage24h = try {
            balanceModel?.priceChangePercentage24h?.let { BigDecimal.parseString(it) }
        } catch (e: Exception) {
            null
        },
        formattedCompactBalance = balanceModel?.formattedCompactBalance(),
        isLoading = isLoading,
        enabled = enabled
    )
}
