package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.features.wallet.presentationv2.antelope.TokenUiState
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.sumOf
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.ext.format
import com.mangala.wallet.utils.ext.formatFiat
import com.mangala.wallet.utils.truncateDecimal
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit
import com.mangala.wallet.ui.WalletThemeV2

data class AccountInfo(
    val accountName: String,
    val balance: BigDecimal?,
    val pnlAmount: BigDecimal? = null,
    val pnlPercentage: BigDecimal? = null,
    val isActive: Boolean = false,
    val tokens: List<TokenUiState>? = null
) {
    val usdValue: BigDecimal? = tokens?.mapNotNull { it.usdValue }?.sumOf()
    val usdValueFormatted = usdValue?.formatFiat("")
    val nativeTokenValue: BigDecimal? = tokens?.mapNotNull { if (it.balance != null) it.priceInNativeCoin to it.balance else null }?.sumOf { it.first * it.second }

    val calculatedPnlAmount: BigDecimal? = pnlAmount ?: tokens?.mapNotNull { it.pnlAmount }?.sumOf()

    fun getCalculatedPnlAmountInSelectedUnit(
        selectedCurrency: AntelopeAccountBalanceUnit,
        exchangeRateData: Map<String, BigDecimal>?
    ): BigDecimal? {
        return pnlAmount ?: tokens?.mapNotNull {
            it.getPnlAmountInSelectedUnit(selectedCurrency, exchangeRateData)
        }?.sumOf()
    }

    val calculatedPnlPercentage: BigDecimal? = pnlPercentage ?: run {
        val totalUsdValue = usdValue ?: BigDecimal.ZERO
        val totalPnlAmount = calculatedPnlAmount ?: BigDecimal.ZERO
        if (totalUsdValue > BigDecimal.ZERO && totalPnlAmount != BigDecimal.ZERO) {
            val decimalMode = DecimalMode(decimalPrecision = 10, roundingMode = RoundingMode.ROUND_HALF_CEILING)
            totalPnlAmount.divide(totalUsdValue - totalPnlAmount, decimalMode) * 100
        } else null
    }

    val pnlColor = if (calculatedPnlAmount != null && calculatedPnlAmount >= BigDecimal.ZERO) {
        WalletThemeV2.Colors.positiveGain
    } else {
        WalletThemeV2.Colors.negativeLoss
    }
    val pnlPercentageFormatted = calculatedPnlPercentage?.let { if (it >= 0) "+" else "" + calculatedPnlPercentage.format(2) }
    val balanceFormatted = balance?.format(decimalPlaces = 4)

    fun getPnlAmountFormatted(
        selectedCurrency: AntelopeAccountBalanceUnit,
        exchangeRateData: Map<String, BigDecimal>?
    ): String? {
        return getPnlAmountFormattedInSelectedUnit(selectedCurrency, exchangeRateData)
    }

    fun getBalanceInSelectedUnit(
        selectedCurrency: AntelopeAccountBalanceUnit,
        exchangeRateData: Map<String, BigDecimal>?
    ): BigDecimal? {
        return when (selectedCurrency) {
            AntelopeAccountBalanceUnit.NativeCoin -> nativeTokenValue
            AntelopeAccountBalanceUnit.USDT -> usdValue
            else -> {
                val exchangeRate = exchangeRateData?.get(selectedCurrency.currencySymbol)
                if (exchangeRate != null && exchangeRate > BigDecimal.ZERO && nativeTokenValue != null) {
                    nativeTokenValue * exchangeRate
                } else {
                    BigDecimal.ZERO
                }
            }
        }
    }

    fun getPnlAmountFormattedInSelectedUnit(
        selectedCurrency: AntelopeAccountBalanceUnit?,
        exchangeRateData: Map<String, BigDecimal>?
    ): String? {
        if (selectedCurrency == null || exchangeRateData == null) return null

        val pnlPercentage = calculatedPnlPercentage ?: return null

        val tokenPnlsInSelectedUnit = tokens?.mapNotNull { token ->
            token.getPnlAmountInSelectedUnit(selectedCurrency, exchangeRateData)
        }

        val totalPnlAmountInSelectedUnit = tokenPnlsInSelectedUnit?.sumOf() ?: return null

        if (totalPnlAmountInSelectedUnit == BigDecimal.ZERO && tokenPnlsInSelectedUnit.isEmpty()) {
            return null
        }

        val minDenomination = "0.01".toBigDecimal()
        val absAmount = if (totalPnlAmountInSelectedUnit < BigDecimal.ZERO) totalPnlAmountInSelectedUnit * (-1).toBigDecimal() else totalPnlAmountInSelectedUnit

        val formattedAmount = if (absAmount > BigDecimal.ZERO && absAmount < minDenomination) {
            if (totalPnlAmountInSelectedUnit >= 0) "<0.01" else "-<0.01"
        } else {
            "${if (totalPnlAmountInSelectedUnit >= 0) "+" else ""}${totalPnlAmountInSelectedUnit.format(2)}"
        }

        return "$formattedAmount ${selectedCurrency.symbol} (${if (pnlPercentage >= 0) "+" else ""}${pnlPercentage.format(2)}%)"
    }
}
