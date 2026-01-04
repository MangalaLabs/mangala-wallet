package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Hide
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Show
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.ext.format
import com.mangala.wallet.utils.ext.formatFiat
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.sumOf

data class PnlData(
    val amount: BigDecimal?,
    val percentage: BigDecimal?,
    val color: Color
) {
    fun getFormattedAmountInSelectedUnit(
        selectedCurrency: AntelopeAccountBalanceUnit,
        exchangeRateData: Map<String, BigDecimal>?
    ): String? {
        val pnlAmount = amount ?: return null
        val pnlPercentage = percentage ?: return null

        val pnlAmountInSelectedUnit = when (selectedCurrency) {
            AntelopeAccountBalanceUnit.NativeCoin -> {
                // For native tokens, we'd need proper conversion from USD to native token value
                // This is a placeholder - proper implementation would require more context
                pnlAmount
            }
            AntelopeAccountBalanceUnit.USDT -> pnlAmount
            else -> {
                val exchangeRate = exchangeRateData?.get(selectedCurrency.currencySymbol)
                if (exchangeRate != null && exchangeRate > BigDecimal.ZERO) {
                    pnlAmount.times(exchangeRate)
                } else {
                    pnlAmount
                }
            }
        }

        val minDenomination = "0.01".toBigDecimal()
        val absAmount = if (pnlAmountInSelectedUnit < BigDecimal.ZERO) pnlAmountInSelectedUnit * (-1).toBigDecimal() else pnlAmountInSelectedUnit

        val formattedAmount = if (absAmount > BigDecimal.ZERO && absAmount < minDenomination) {
            if (pnlAmountInSelectedUnit >= 0) "<0.01" else "-<0.01"
        } else {
            "${if (pnlAmountInSelectedUnit >= 0) "+" else ""}${pnlAmountInSelectedUnit.format(2)}"
        }

        return "$formattedAmount ${selectedCurrency.symbol} (${if (pnlPercentage >= 0) "+" else ""}${pnlPercentage.format(2)}%)"
    }
}

data class PortfolioSummary(
    val totalPortfolioBalance: BigDecimal?,
    val totalBalanceDecimals: Long,
    val totalUsdValue: BigDecimal?,
    val totalPnlAmount: BigDecimal?,
    val totalPnlPercentage: BigDecimal,
    val accounts: List<AccountInfo>,
    val fiatSymbol: String,
    val selectedCoreBalanceUnit: AntelopeAccountBalanceUnit,
    val coinGeckoExchangeRateData: Map<String, BigDecimal>?
) {
    val totalPnlPercentageFormatted = totalPnlPercentage.format(2)

    val totalValueInSelectedUnit: BigDecimal? = when (selectedCoreBalanceUnit) {
        AntelopeAccountBalanceUnit.NativeCoin -> totalPortfolioBalance
        AntelopeAccountBalanceUnit.USDT -> totalUsdValue
        else -> {
            val exchangeRate = coinGeckoExchangeRateData?.get(selectedCoreBalanceUnit.currencySymbol)
            if (exchangeRate != null && exchangeRate > 0 && totalPortfolioBalance != null) {
                totalPortfolioBalance.times(exchangeRate)
            } else {
                BigDecimal.ZERO
            }
        }
    }

    val totalValueInSelectedUnitFormatted = totalValueInSelectedUnit?.formatFiat("") + " " + selectedCoreBalanceUnit.symbol
}

@Composable
fun AdaptivePortfolioHeader(
    isSingleAccountMode: Boolean,
    totalPortfolioBalance: BigDecimal?,
    balanceDecimals: Long,
    totalPortfolioUsd: BigDecimal?,
    totalAccounts: Int,
    activeAccountName: String,
    totalPnlAmount: BigDecimal?,
    totalPnlAmountFormatted: String?,
    pnlColor: Color,
    totalPnlPercentage: BigDecimal,
    isBalanceHidden: Boolean,
    fiatSymbol: String,
    selectedCurrency: AntelopeAccountBalanceUnit,
    coinGeckoExchangeRateData: Map<String, BigDecimal>?,
    onToggleHideBalance: () -> Unit,
    onCurrencyClick: () -> Unit,
    onCopyAccountName: () -> Unit = {},
    // New parameters for multi-account
    accounts: List<AccountInfo>,
    onAccountClick: (String) -> Unit = {},
    onSelectCurrency: (AntelopeAccountBalanceUnit) -> Unit,
) {
    AnimatedContent(
        targetState = isSingleAccountMode,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        }
    ) { singleMode ->
        if (singleMode) {
            // Single Account View - Simplified
            // Calculate PnL amount in selected currency unit
            val pnlAmountInSelectedUnit = accounts.mapNotNull {
                it.getCalculatedPnlAmountInSelectedUnit(selectedCurrency, coinGeckoExchangeRateData)
            }.ifEmpty { null }?.sumOf()

            val pnlData = PnlData(
                amount = pnlAmountInSelectedUnit,
                percentage = totalPnlPercentage,
                color = pnlColor
            )

            SingleAccountPortfolioView(
                accountName = activeAccountName,
                balance = totalPortfolioBalance,
                balanceDecimals = balanceDecimals,
                usdValue = totalPortfolioUsd,
                pnlAmount = totalPnlAmount,
                pnlAmountFormatted = pnlData.getFormattedAmountInSelectedUnit(selectedCurrency, coinGeckoExchangeRateData),
                pnlColor = pnlColor,
                pnlPercentage = totalPnlPercentage,
                isBalanceHidden = isBalanceHidden,
                selectedCurrency = selectedCurrency,
                exchangeRateData = coinGeckoExchangeRateData,
                onToggleHideBalance = onToggleHideBalance,
                onCurrencyClick = onCurrencyClick,
                onCopyAccountName = onCopyAccountName,
                onSelectCurrency = onSelectCurrency
            )
        } else {
            // Multi Account View - Simple design with bottom sheet
            val portfolioSummary = PortfolioSummary(
                totalPortfolioBalance = totalPortfolioBalance,
                totalBalanceDecimals = balanceDecimals,
                totalUsdValue = totalPortfolioUsd,
                totalPnlAmount = totalPnlAmount,
                totalPnlPercentage = totalPnlPercentage,
                fiatSymbol = fiatSymbol,
                accounts = accounts,
                selectedCoreBalanceUnit = selectedCurrency,
                coinGeckoExchangeRateData = coinGeckoExchangeRateData
            )

            MultiAccountSimpleView(
                portfolioSummary = portfolioSummary,
                balanceDecimals = balanceDecimals,
                isBalanceHidden = isBalanceHidden,
                selectedCurrency = selectedCurrency,
                onToggleHideBalance = onToggleHideBalance,
                onCurrencyClick = onCurrencyClick,
                onAccountClick = onAccountClick,
                onCopyAccountName = onCopyAccountName,
                onSelectCurrency = onSelectCurrency
            )
        }
    }
}

// Simple Multi-Account View

@Composable
private fun MultiAccountSimpleView(
    portfolioSummary: PortfolioSummary,
    balanceDecimals: Long,
    isBalanceHidden: Boolean,
    selectedCurrency: AntelopeAccountBalanceUnit,
    onToggleHideBalance: () -> Unit,
    onCurrencyClick: () -> Unit,
    onAccountClick: (String) -> Unit,
    onCopyAccountName: () -> Unit,
    onSelectCurrency: (AntelopeAccountBalanceUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAccount = portfolioSummary.accounts.firstOrNull { it.isActive }

    if (activeAccount != null) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Simple total portfolio row (no card)
            SimpleTotalRow(
                portfolioSummary = portfolioSummary
            )

            // Active account detail (reuse single account design)
            SingleAccountPortfolioView(
                accountName = activeAccount.accountName,
                balance = activeAccount.getBalanceInSelectedUnit(selectedCurrency, portfolioSummary.coinGeckoExchangeRateData),
                balanceDecimals = balanceDecimals,
                usdValue = activeAccount.usdValue,
                pnlAmount = activeAccount.pnlAmount,
                pnlAmountFormatted = activeAccount.getPnlAmountFormatted(selectedCurrency, portfolioSummary.coinGeckoExchangeRateData),
                pnlColor = activeAccount.pnlColor,
                pnlPercentage = activeAccount.pnlPercentage,
                isBalanceHidden = isBalanceHidden,
                selectedCurrency = selectedCurrency,
                exchangeRateData = portfolioSummary.coinGeckoExchangeRateData,
                onToggleHideBalance = onToggleHideBalance,
                onCurrencyClick = onCurrencyClick,
                onCopyAccountName = onCopyAccountName,
                isMultiAccountMode = true,
                onAccountSwitch = { onAccountClick("switch") },
                onSelectCurrency = onSelectCurrency
            )
        }
    }
}

@Composable
private fun SimpleTotalRow(
    portfolioSummary: PortfolioSummary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 0.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side - Elegant minimal design
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "TOTAL PORTFOLIO",
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = WalletThemeV2.Colors.tertiaryText,
                fontWeight = FontWeight.Normal,
                fontFamily = getInterFontFamily()
            )
            
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (portfolioSummary.totalValueInSelectedUnit != null) {
                        portfolioSummary.totalValueInSelectedUnitFormatted
                    } else {
                        "000.00 ${portfolioSummary.selectedCoreBalanceUnit.symbol}"
                    },
                    fontSize = 15.sp,
                    color = WalletThemeV2.Colors.secondaryText,
                    fontWeight = FontWeight.Medium,
                    fontFamily = getInterFontFamily(),
                    modifier = Modifier.mangalaWalletPlaceholder(
                        visible = portfolioSummary.totalValueInSelectedUnit == null
                    )
                )
                
                Text(
                    text = if (portfolioSummary.totalValueInSelectedUnit != null) {
                        "${if (portfolioSummary.totalPnlPercentage >= 0) "+" else ""}${portfolioSummary.totalPnlPercentageFormatted}%"
                    } else {
                        "+0.00%"
                    },
                    fontSize = 12.sp,
                    color = if (portfolioSummary.totalValueInSelectedUnit != null) {
                        if (portfolioSummary.totalPnlPercentage >= 0) {
                            WalletThemeV2.Colors.positiveGain.copy(alpha = 0.85f)
                        } else {
                            WalletThemeV2.Colors.negativeLoss.copy(alpha = 0.85f)
                        }
                    } else {
                        WalletThemeV2.Colors.secondaryText.copy(alpha = 0.5f)
                    },
                    fontWeight = FontWeight.Normal,
                    fontFamily = getInterFontFamily(),
                    modifier = Modifier.mangalaWalletPlaceholder(
                        visible = portfolioSummary.totalValueInSelectedUnit == null
                    )
                )
            }
        }
    }
}


@Composable
private fun SingleAccountPortfolioView(
    accountName: String,
    balance: BigDecimal?,
    balanceDecimals: Long,
    usdValue: BigDecimal?,
    pnlAmount: BigDecimal?,
    pnlAmountFormatted: String?,
    pnlColor: Color,
    pnlPercentage: BigDecimal?,
    isBalanceHidden: Boolean,
    selectedCurrency: AntelopeAccountBalanceUnit,
    exchangeRateData: Map<String, BigDecimal>? = null,
    onToggleHideBalance: () -> Unit,
    onCurrencyClick: () -> Unit,
    onCopyAccountName: () -> Unit,
    isMultiAccountMode: Boolean = false,
    onAccountSwitch: () -> Unit = {},
    onSelectCurrency: (AntelopeAccountBalanceUnit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WalletThemeV2.Colors.cardBackground)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Account Label Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Account name with copy icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Account name with switch icon inside (clickable in multi-account mode)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Color.White.copy(alpha = 0.08f)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .then(
                                if (isMultiAccountMode) {
                                    Modifier.clickable { onAccountSwitch() }
                                } else {
                                    Modifier
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = accountName,
                                fontSize = 13.sp,
                                color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium,
                                fontFamily = getInterFontFamily()
                            )
                            
                            // Switch icon inside account name box (only in multi-account mode)
                            if (isMultiAccountMode) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Switch Account",
                                    tint = WalletThemeV2.Colors.tertiaryText,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                    
                    // Copy icon
                    Icon(
                        imageVector = MangalaWalletPack.Copy,
                        contentDescription = "Copy account name",
                        tint = WalletThemeV2.Colors.secondaryText,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onCopyAccountName() }
                    )
                }
                
                // Hide/Show Balance Icon - iOS style
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            WalletThemeV2.Colors.secondaryBackground.copy(alpha = 0.5f)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.05f),
                            shape = CircleShape
                        )
                        .clickable { onToggleHideBalance() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBalanceHidden) MangalaWalletPack.Hide else MangalaWalletPack.Show,
                        contentDescription = "Toggle balance visibility",
                        tint = WalletThemeV2.Colors.secondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.clickable { expanded = true }
            ) {
                if (balance != null) {
                    Text(
                        text = if (isBalanceHidden) "••••••" else {
                            when {
                                balance >= 1000000 -> "${balance.divide(1000000.toBigDecimal()).format(balanceDecimals)}M"
                                balance >= 1000 -> "${balance.divide(1000.toBigDecimal()).format(balanceDecimals)}K"
                                else -> balance.format(balanceDecimals)
                            }
                        },
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = WalletThemeV2.Colors.primaryText,
                        fontFamily = getInterFontFamily(),
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.weight(1f, fill = false).basicMarquee()
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                CurrencyDropdown(
                    modifier = Modifier.padding(bottom = 4.dp),
                    selectedCurrency = selectedCurrency,
                    onSelectCurrency = onSelectCurrency,
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                )
            }

            if (pnlAmountFormatted != null) {
                Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))

                PnlDisplay(
                    pnlAmountFormatted = pnlAmountFormatted,
                    pnlColor = pnlColor,
                    isBalanceHidden = isBalanceHidden
                )
            }
        }
    }
}

@Composable
private fun CurrencyDropdown(
    modifier: Modifier = Modifier,
    selectedCurrency: AntelopeAccountBalanceUnit,
    onSelectCurrency: (AntelopeAccountBalanceUnit) -> Unit,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {}
) {
    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCurrency.symbol,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = WalletThemeV2.Colors.secondaryText,
                fontFamily = getInterFontFamily()
            )
            
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Change currency",
                tint = WalletThemeV2.Colors.secondaryText,
                modifier = Modifier.size(18.dp)
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .background(
                    color = WalletThemeV2.Colors.cardBackground,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            AntelopeAccountBalanceUnit.entries.forEach { currency ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = currency.symbol,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedCurrency == currency) {
                                WalletThemeV2.Colors.accentBlue
                            } else {
                                WalletThemeV2.Colors.primaryText
                            },
                            fontFamily = getInterFontFamily()
                        )
                    },
                    onClick = {
                        onSelectCurrency(currency)
                        onExpandedChange(false)
                    },
                    modifier = Modifier
                        .background(
                            if (selectedCurrency == currency) {
                                WalletThemeV2.Colors.accentBlue.copy(alpha = 0.1f)
                            } else {
                                Color.Transparent
                            }
                        )
                )
            }
        }
    }
}