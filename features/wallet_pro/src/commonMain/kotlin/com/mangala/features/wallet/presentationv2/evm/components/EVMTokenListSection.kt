package com.mangala.features.wallet.presentationv2.evm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Search
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.token.domain.formattedBalanceForHuman
import com.mangala.wallet.model.token.domain.formattedValue
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.ui.imageloader.MultiImage
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder

private const val HIDDEN_PNL_STRING = "+*.**%"

@Composable
fun EVMTokenListSection(
    tokens: List<TokenBalanceModel>?,
    isBalanceHidden: Boolean,
    isLoading: Boolean,
    currencySymbol: String,
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    onSearchClick: () -> Unit = {},
    onSearchQueryChanged: (String) -> Unit = {},
    onTokenClick: (TokenBalanceModel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filteredTokens = remember(tokens, searchQuery) {
        if (searchQuery.isBlank()) {
            tokens
        } else {
            tokens?.filter { token ->
                token.contractSymbol.contains(searchQuery, ignoreCase = true) ||
                        token.contractName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header with search toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tokens",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.8f),
                fontFamily = getInterFontFamily()
            )

            Icon(
                imageVector = MangalaWalletPack.Search,
                contentDescription = "Search",
                tint = if (isSearchActive) WalletThemeV2.Colors.evmAccent else WalletThemeV2.Colors.secondaryText,
                modifier = Modifier
                    .size(WalletThemeV2.Dimensions.iconSizeMedium)
                    .clickable { onSearchClick() }
            )
        }

        // Search field
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
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = WalletThemeV2.Colors.secondaryText.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = WalletThemeV2.Colors.primaryText,
                    backgroundColor = WalletThemeV2.Colors.cardBackground.copy(alpha = 0.5f),
                    focusedBorderColor = WalletThemeV2.Colors.evmAccent.copy(alpha = 0.8f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    cursorColor = WalletThemeV2.Colors.evmAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        }

        Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))

        // Token list container
        when {
            isLoading && filteredTokens.isNullOrEmpty() -> {
                // Loading skeleton
                TokenListSkeleton()
            }

            filteredTokens.isNullOrEmpty() -> {
                // Empty state
                EmptyTokenState(
                    message = if (searchQuery.isNotEmpty()) "No tokens match \"$searchQuery\"" else "No tokens found"
                )
            }

            else -> {
                // Token list
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
                        filteredTokens.forEachIndexed { index, token ->
                            EVMTokenItem(
                                token = token,
                                isBalanceHidden = isBalanceHidden,
                                currencySymbol = currencySymbol,
                                onClick = { onTokenClick(token) }
                            )

                            if (index < filteredTokens.lastIndex) {
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
private fun EVMTokenItem(
    token: TokenBalanceModel,
    isBalanceHidden: Boolean,
    currencySymbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pnlPercentage = remember(token.priceChangePercentage24h) {
        token.priceChangePercentage24h?.let {
            runCatching { BigDecimal.parseString(it) }.getOrNull()
        }
    }

    val pnlColor = remember(pnlPercentage) {
        when {
            pnlPercentage == null -> Color(0xFF9CA3AF) // gray
            pnlPercentage > BigDecimal.ZERO -> Color(0xFF00A699) // green
            pnlPercentage < BigDecimal.ZERO -> Color(0xFFFF6B6B) // red
            else -> Color(0xFF9CA3AF) // gray
        }
    }

    Box(
        modifier = modifier
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
            // Token logo
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                EVMTokenLogo(
                    logoUrl = token.logoUrl,
                    symbol = token.contractSymbol,
                    size = 22.dp
                )
            }

            Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingMedium))

            // Token info - left side
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = token.contractSymbol,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.9f),
                    fontFamily = getInterFontFamily()
                )

                Text(
                    text = token.contractName,
                    fontSize = 12.sp,
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily(),
                    maxLines = 1
                )

                Text(
                    text = "24h",
                    fontSize = 10.sp,
                    color = WalletThemeV2.Colors.tertiaryText,
                    fontFamily = getInterFontFamily()
                )
            }

            // Right side - balance and PnL
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isBalanceHidden) HIDDEN_BALANCE_STRING else token.formattedBalanceForHuman(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.9f),
                    fontFamily = getInterFontFamily()
                )

                Text(
                    text = if (isBalanceHidden) HIDDEN_BALANCE_STRING else token.formattedValue(currencySymbol),
                    fontSize = 12.sp,
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily()
                )

                // PnL percentage
                Text(
                    text = formatPnlPercentage(pnlPercentage, isBalanceHidden),
                    fontSize = 10.sp,
                    color = if (isBalanceHidden) WalletThemeV2.Colors.secondaryText else pnlColor,
                    fontFamily = getInterFontFamily()
                )
            }
        }
    }
}

@Composable
private fun EVMTokenLogo(
    logoUrl: String?,
    symbol: String,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    if (!logoUrl.isNullOrEmpty()) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.Transparent)
        ) {
            MultiImage(
                imageSource = ImageSource.Url(logoUrl),
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }
    } else {
        // Fallback with symbol initials
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(getTokenColor(symbol)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol.take(2).uppercase(),
                fontSize = (size.value * 0.4).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

private fun getTokenColor(symbol: String): Color {
    return when (symbol.uppercase()) {
        "ETH" -> Color(0xFF627EEA)
        "USDT" -> Color(0xFF26A17B)
        "USDC" -> Color(0xFF2775CA)
        "BNB" -> Color(0xFFF3BA2F)
        "MATIC", "POL" -> Color(0xFF8247E5)
        "ARB" -> Color(0xFF28A0F0)
        "OP" -> Color(0xFFFF0420)
        "AVAX" -> Color(0xFFE84142)
        "DAI" -> Color(0xFFF4B731)
        "WETH" -> Color(0xFFEC6666)
        "LINK" -> Color(0xFF2A5ADA)
        "UNI" -> Color(0xFFFF007A)
        "AAVE" -> Color(0xFFB6509E)
        else -> Color(0xFF6B7280)
    }
}

private fun formatPnlPercentage(pnl: BigDecimal?, isHidden: Boolean): String {
    if (isHidden) return HIDDEN_PNL_STRING
    if (pnl == null) return "0.00%"

    val sign = if (pnl >= BigDecimal.ZERO) "+" else ""
    return "$sign${pnl.scale(2).toStringExpanded()}%"
}

@Composable
private fun TokenListSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
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
            repeat(3) { index ->
                SkeletonTokenItem()
                if (index < 2) {
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

@Composable
private fun SkeletonTokenItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = WalletThemeV2.Dimensions.paddingLarge,
                vertical = WalletThemeV2.Dimensions.paddingMedium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo skeleton
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .mangalaWalletPlaceholder(visible = true)
        )

        Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingMedium))

        // Info skeleton
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .mangalaWalletPlaceholder(visible = true)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .mangalaWalletPlaceholder(visible = true)
            )
        }

        // Balance skeleton
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .mangalaWalletPlaceholder(visible = true)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .mangalaWalletPlaceholder(visible = true)
            )
        }
    }
}

@Composable
private fun EmptyTokenState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WalletThemeV2.Colors.cardBackground)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = WalletThemeV2.Colors.secondaryText,
            fontFamily = getInterFontFamily()
        )
    }
}
