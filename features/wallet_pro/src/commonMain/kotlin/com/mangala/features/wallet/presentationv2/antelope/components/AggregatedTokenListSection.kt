package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Close
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Filter
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.features.wallet.presentationv2.antelope.TokenUiState
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Search
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.utils.ext.format
import com.mangala.wallet.utils.ext.formatFiat
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit

private const val HIDDEN_USD_VALUE = "**.*** USDT"

data class AggregatedTokenUiState(
    val symbol: String,
    val name: String,
    val decimals: Long,
    val totalBalance: BigDecimal,
    val totalUsdValue: BigDecimal,
    val change24h: BigDecimal,
    val accountCount: Int,
    val accountBreakdown: List<TokenAccountBreakdown>,
    val iconResource: ImageSource?,
    val priceInFiat: BigDecimal?,
) {
    val totalBalanceFormatted = totalBalance.format(decimals)

    fun getValueInSelectedUnit(
        selectedCurrency: AntelopeAccountBalanceUnit,
        exchangeRateData: Map<String, BigDecimal>?
    ): BigDecimal {
        return when (selectedCurrency) {
            AntelopeAccountBalanceUnit.NativeCoin -> totalBalance
            AntelopeAccountBalanceUnit.USDT -> totalUsdValue
            else -> {
                val exchangeRate = exchangeRateData?.get(selectedCurrency.currencySymbol)
                if (exchangeRate != null && exchangeRate > BigDecimal.ZERO) {
                    totalUsdValue.times(exchangeRate)
                } else {
                    BigDecimal.ZERO
                }
            }
        }
    }

    fun getPnlPriceFormattedInSelectedUnit(
        selectedCurrency: AntelopeAccountBalanceUnit,
        exchangeRateData: Map<String, BigDecimal>?
    ): String {
        val priceInSelectedUnit = when (selectedCurrency) {
            AntelopeAccountBalanceUnit.NativeCoin -> priceInFiat?.div(priceInFiat ?: BigDecimal.ONE) // Price in native coins (should be handled differently)
            AntelopeAccountBalanceUnit.USDT -> priceInFiat
            else -> {
                val exchangeRate = exchangeRateData?.get(selectedCurrency.currencySymbol)
                if (exchangeRate != null && exchangeRate > BigDecimal.ZERO && priceInFiat != null) {
                    priceInFiat.times(exchangeRate)
                } else {
                    BigDecimal.ZERO
                }
            }
        }

        return "${priceInSelectedUnit?.abs().orZero().formatFiat("")} ${selectedCurrency.symbol} (${if (change24h >= 0) "+" else ""}${change24h.format(2)}%)"
    }
}

data class TokenAccountBreakdown(
    val accountName: String,
    val balance: BigDecimal?,
    val usdValue: BigDecimal?
)

@Composable
fun AggregatedTokenListSection(
    tokens: List<AggregatedTokenUiState>,
    activeAccountTokens: List<TokenUiState>? = null,
    activeAccountName: String = "",
    isAggregatedView: Boolean,
    isBalanceHidden: Boolean = false,
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    selectedCurrency: AntelopeAccountBalanceUnit,
    exchangeRateData: Map<String, BigDecimal>?,
    onToggleView: () -> Unit,
    onTokenClick: (AggregatedTokenUiState) -> Unit,
    onSearchClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header with Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAggregatedView) "Crypto" else activeAccountName,
                    fontSize = 16.sp, // Smaller than portfolio headers
                    fontWeight = FontWeight.Medium, // Less emphasis
                    color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.8f),
                    fontFamily = getInterFontFamily()
                )

                Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingSmall))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp) // Wider spacing between icons
            ) {
                Icon(
                    imageVector = MangalaWalletPack.Search,
                    contentDescription = "Search",
                    tint = if (isSearchActive) WalletThemeV2.Colors.accentBlue else WalletThemeV2.Colors.secondaryText,
                    modifier = Modifier
                        .size(WalletThemeV2.Dimensions.iconSizeMedium)
                        .clickable { onSearchClick() }
                )

                Icon(
                    imageVector = MangalaWalletPack.Filter,
                    contentDescription = "Filter",
                    tint = WalletThemeV2.Colors.secondaryText,
                    modifier = Modifier
                        .size(WalletThemeV2.Dimensions.iconSizeMedium)
                        .clickable { onFilterClick() }
                )
            }
        }

        if (isSearchActive) {
            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))

            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(isSearchActive) {
                if (isSearchActive) {
                    focusRequester.requestFocus()
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        text = "Search tokens...",
                        color = WalletThemeV2.Colors.secondaryText.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        fontFamily = getInterFontFamily()
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = WalletThemeV2.Colors.secondaryText.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(
                            onClick = { onSearchQueryChanged("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = WalletThemeV2.Colors.secondaryText.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else null,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = WalletThemeV2.Colors.primaryText,
                    backgroundColor = WalletThemeV2.Colors.cardBackground.copy(alpha = 0.5f),
                    focusedBorderColor = WalletThemeV2.Colors.accentBlue.copy(alpha = 0.8f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    cursorColor = WalletThemeV2.Colors.accentBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        }

        Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))

        // Token List
        if (isAggregatedView) {
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
                Column {
                    tokens.forEachIndexed { index, token ->
                        AggregatedTokenItem(
                            token = token,
                            selectedCurrency = selectedCurrency,
                            exchangeRateData = exchangeRateData,
                            isBalanceHidden = isBalanceHidden,
                            onClick = { onTokenClick(token) }
                        )

                        if (index < tokens.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .padding(horizontal = WalletThemeV2.Dimensions.paddingLarge)
                                    .background(Color.White.copy(alpha = 0.03f))
                            )
                        }
                    }
                }
            }
        } else {
            if (activeAccountTokens != null) {
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
                    Column {
                        activeAccountTokens.forEachIndexed { index, token ->
                            AccountTokenItem(
                                token = token,
                                selectedCurrency = selectedCurrency,
                                exchangeRateData = exchangeRateData,
                                isBalanceHidden = isBalanceHidden,
                                onClick = { /* TODO: Navigate to token detail */ }
                            )

                            if (index < activeAccountTokens.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .padding(horizontal = WalletThemeV2.Dimensions.paddingLarge)
                                        .background(Color.White.copy(alpha = 0.03f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TokenItem(
    iconResource: ImageSource?,
    symbol: String,
    name: String,
    balanceText: String,
    valueInSelectedUnitText: String,
    pnlContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = WalletThemeV2.Dimensions.paddingLarge,
                    vertical = WalletThemeV2.Dimensions.paddingMedium
                ),
            verticalAlignment = Alignment.Top
        ) {
            // Token Avatar - improved alignment
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                TokenLogo(
                    iconResource = iconResource,
                    symbol = symbol,
                    size = 22.dp
                )
            }

            Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingMedium))

            // Token Info - Left Side
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = symbol,
                    fontSize = 14.sp, // Reduced from medium
                    fontWeight = FontWeight.Medium, // Less bold
                    color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.9f),
                    fontFamily = getInterFontFamily()
                )

                Text(
                    text = name,
                    fontSize = 12.sp, // Smaller
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily()
                )

                Text(
                    text = "24h",
                    fontSize = 10.sp, // Smaller
                    color = WalletThemeV2.Colors.tertiaryText,
                    fontFamily = getInterFontFamily()
                )
            }

            // Right Side - Amount and Values
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = balanceText,
                    fontSize = 14.sp, // Reduced
                    fontWeight = FontWeight.Medium,
                    color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.9f),
                    fontFamily = getInterFontFamily()
                )

                Text(
                    text = valueInSelectedUnitText,
                    fontSize = 12.sp, // Smaller
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily()
                )

                pnlContent()
            }
        }
    }
}

@Composable
private fun AccountTokenItem(
    token: TokenUiState,
    selectedCurrency: AntelopeAccountBalanceUnit,
    exchangeRateData: Map<String, BigDecimal>?,
    isBalanceHidden: Boolean = false,
    onClick: () -> Unit
) {
    TokenItem(
        iconResource = token.iconResource,
        symbol = token.symbol,
        name = token.name,
        balanceText = if (isBalanceHidden) "****.**" else token.balanceFormatted.orEmpty(),
        valueInSelectedUnitText = if (isBalanceHidden) HIDDEN_USD_VALUE else "${token.getValueInSelectedUnit(selectedCurrency, exchangeRateData)?.formatFiat("")} ${selectedCurrency.symbol}",
        pnlContent = {
            PnlDisplay(
                pnlAmountFormatted = token.getPnlAmountFormattedInSelectedUnit(selectedCurrency, exchangeRateData),
                pnlColor = token.pnlColor,
                isBalanceHidden = isBalanceHidden,
                valueFontSize = 10.sp,
                selectedCurrency = selectedCurrency,
                showLabel = false
            )
        },
        onClick = onClick
    )
}

@Composable
private fun AggregatedTokenItem(
    token: AggregatedTokenUiState,
    selectedCurrency: AntelopeAccountBalanceUnit,
    exchangeRateData: Map<String, BigDecimal>?,
    isBalanceHidden: Boolean = false,
    onClick: () -> Unit
) {
    TokenItem(
        iconResource = token.iconResource,
        symbol = token.symbol,
        name = token.name,
        balanceText = if (isBalanceHidden) "****.**" else token.totalBalanceFormatted,
        valueInSelectedUnitText = if (isBalanceHidden) HIDDEN_USD_VALUE else "${token.getValueInSelectedUnit(selectedCurrency, exchangeRateData).formatFiat("")} ${selectedCurrency.symbol}",
        pnlContent = {
            if (!isBalanceHidden) {
                Text(
                    text = token.getPnlPriceFormattedInSelectedUnit(selectedCurrency, exchangeRateData),
                    fontSize = 10.sp,
                    color = if (token.change24h >= 0) WalletThemeV2.Colors.positiveGain else WalletThemeV2.Colors.negativeLoss,
                    fontFamily = getInterFontFamily()
                )
            } else {
                Text(
                    text = "**.*% (***.**%)",
                    fontSize = 10.sp,
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily()
                )
            }
        },
        onClick = onClick
    )
}