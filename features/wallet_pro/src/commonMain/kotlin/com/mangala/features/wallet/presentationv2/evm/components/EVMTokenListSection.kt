package com.mangala.features.wallet.presentationv2.evm.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.features.wallet.presentationv2.antelope.components.TokenLogo
import com.mangala.features.wallet.presentationv2.evm.model.EVMAggregatedToken
import com.mangala.features.wallet.presentationv2.evm.model.EVMTokenAccountBreakdown
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Filter
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Search
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.token.domain.formattedBalanceForHuman
import com.mangala.wallet.model.token.domain.formattedValue
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.utils.ext.formatFiat
import dev.icerock.moko.resources.compose.stringResource

private fun Modifier.tokenListCard(): Modifier = this
    .fillMaxWidth()
    .clip(RoundedCornerShape(16.dp))
    .background(WalletThemeV2.Colors.cardBackground)
    .border(
        width = 1.dp,
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp)
    )

@Composable
private fun TokenListDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = WalletThemeV2.Dimensions.paddingLarge)
            .background(Color.White.copy(alpha = 0.03f))
    )
}

@Composable
fun EVMTokenListSection(
    tokens: List<TokenBalanceModel>?,
    isBalanceHidden: Boolean,
    isLoading: Boolean,
    currencySymbol: String,
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    isAllAccountsView: Boolean = false,
    aggregatedTokens: List<EVMAggregatedToken> = emptyList(),
    hasActiveFilters: Boolean = false,
    onSearchClick: () -> Unit = {},
    onSearchQueryChanged: (String) -> Unit = {},
    onFilterClick: () -> Unit = {},
    onTokenClick: (TokenBalanceModel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val noTokensMatchMessage = stringResource(MR.strings.message_no_tokens_match, searchQuery)
    val noTokensFoundMessage = stringResource(MR.strings.message_no_tokens_found)

    Column(modifier = modifier.fillMaxWidth()) {
        TokenListHeader(
            isSearchActive = isSearchActive,
            isAllAccountsView = isAllAccountsView,
            hasActiveFilters = hasActiveFilters,
            onSearchClick = onSearchClick,
            onFilterClick = onFilterClick
        )

        if (isSearchActive) {
            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))
            TokenSearchField(
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged
            )
        }

        Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))

        if (isAllAccountsView) {
            when {
                isLoading && aggregatedTokens.isEmpty() -> {
                    TokenListSkeleton()
                }

                aggregatedTokens.isEmpty() -> {
                    EmptyTokenState(
                        message = if (searchQuery.isNotEmpty()) noTokensMatchMessage else noTokensFoundMessage
                    )
                }

                else -> {
                    AggregatedTokenListContent(
                        tokens = aggregatedTokens,
                        isBalanceHidden = isBalanceHidden,
                        currencySymbol = currencySymbol
                    )
                }
            }
        } else {
            when {
                isLoading && tokens.isNullOrEmpty() -> {
                    TokenListSkeleton()
                }

                tokens.isNullOrEmpty() -> {
                    EmptyTokenState(
                        message = if (searchQuery.isNotEmpty()) noTokensMatchMessage else noTokensFoundMessage
                    )
                }

                else -> {
                    TokenListContent(
                        tokens = tokens,
                        isBalanceHidden = isBalanceHidden,
                        currencySymbol = currencySymbol,
                        onTokenClick = onTokenClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TokenListHeader(
    isSearchActive: Boolean,
    isAllAccountsView: Boolean,
    hasActiveFilters: Boolean,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    val tokensLabel = stringResource(MR.strings.label_tokens)
    val searchLabel = stringResource(MR.strings.label_search)
    val filterLabel = stringResource(MR.strings.label_filter_and_sort)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isAllAccountsView) stringResource(MR.strings.label_portfolio) else tokensLabel,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.8f),
            fontFamily = getInterFontFamily()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = MangalaWalletPack.Search,
                contentDescription = searchLabel,
                tint = if (isSearchActive) WalletThemeV2.Colors.evmAccent else WalletThemeV2.Colors.secondaryText,
                modifier = Modifier
                    .size(WalletThemeV2.Dimensions.iconSizeMedium)
                    .clickable { onSearchClick() }
            )

            Box {
                Icon(
                    imageVector = MangalaWalletPack.Filter,
                    contentDescription = filterLabel,
                    tint = if (hasActiveFilters) WalletThemeV2.Colors.evmAccent else WalletThemeV2.Colors.secondaryText,
                    modifier = Modifier
                        .size(WalletThemeV2.Dimensions.iconSizeMedium)
                        .clickable { onFilterClick() }
                )

                if (hasActiveFilters) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(WalletThemeV2.Colors.evmAccent)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}

@Composable
private fun TokenSearchField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val placeholderText = stringResource(MR.strings.message_wallet_details_search_tokens_hint)
    val searchLabel = stringResource(MR.strings.label_search)
    val clearLabel = stringResource(MR.strings.content_description_clear)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = {
            Text(
                text = placeholderText,
                color = WalletThemeV2.Colors.secondaryText.copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontFamily = getInterFontFamily()
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = searchLabel,
                tint = WalletThemeV2.Colors.secondaryText.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = if (searchQuery.isNotEmpty()) {
            {
                IconButton(onClick = { onSearchQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = clearLabel,
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

@Composable
private fun TokenListContent(
    tokens: List<TokenBalanceModel>,
    isBalanceHidden: Boolean,
    currencySymbol: String,
    onTokenClick: (TokenBalanceModel) -> Unit
) {
    Box(modifier = Modifier.tokenListCard()) {
        Column {
            tokens.forEachIndexed { index, token ->
                EVMTokenItem(
                    token = token,
                    isBalanceHidden = isBalanceHidden,
                    currencySymbol = currencySymbol,
                    onClick = { onTokenClick(token) }
                )

                if (index < tokens.lastIndex) {
                    TokenListDivider()
                }
            }
        }
    }
}

@Composable
private fun AggregatedTokenListContent(
    tokens: List<EVMAggregatedToken>,
    isBalanceHidden: Boolean,
    currencySymbol: String
) {
    Box(modifier = Modifier.tokenListCard()) {
        Column {
            tokens.forEachIndexed { index, token ->
                EVMAggregatedTokenItem(
                    token = token,
                    isBalanceHidden = isBalanceHidden,
                    currencySymbol = currencySymbol
                )

                if (index < tokens.lastIndex) {
                    TokenListDivider()
                }
            }
        }
    }
}

@Composable
private fun EVMAggregatedTokenItem(
    token: EVMAggregatedToken,
    isBalanceHidden: Boolean,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val pnlPercentage = remember(token.priceChangePercentage24h) {
        token.priceChangePercentage24h?.let {
            runCatching { BigDecimal.parseString(it) }.getOrNull()
        }
    }

    val pnlColor = remember(pnlPercentage) {
        when {
            pnlPercentage == null -> WalletThemeV2.Colors.secondaryText
            pnlPercentage > BigDecimal.ZERO -> WalletThemeV2.Colors.positiveGain
            pnlPercentage < BigDecimal.ZERO -> WalletThemeV2.Colors.negativeLoss
            else -> WalletThemeV2.Colors.secondaryText
        }
    }

    val label24h = stringResource(MR.strings.label_24h)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent)
                .clickable { expanded = !expanded }
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
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TokenLogo(
                        iconResource = token.logoUrl.takeIf { it.isNotEmpty() }
                            ?.let { ImageSource.Url(it) },
                        symbol = token.contractSymbol,
                        size = 22.dp
                    )
                }

                Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingMedium))

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
                        text = label24h,
                        fontSize = 10.sp,
                        color = WalletThemeV2.Colors.tertiaryText,
                        fontFamily = getInterFontFamily()
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isBalanceHidden) HIDDEN_BALANCE_STRING
                        else token.totalBalance.scale(4).toStringExpanded(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.9f),
                        fontFamily = getInterFontFamily()
                    )

                    Text(
                        text = if (isBalanceHidden) HIDDEN_BALANCE_STRING
                        else token.totalValueUsd.formatFiat(currencySymbol, decimalPlaces = 2),
                        fontSize = 12.sp,
                        color = WalletThemeV2.Colors.secondaryText,
                        fontFamily = getInterFontFamily()
                    )

                    Text(
                        text = formatPnlPercentage(pnlPercentage, isBalanceHidden),
                        fontSize = 10.sp,
                        color = if (isBalanceHidden) WalletThemeV2.Colors.secondaryText else pnlColor,
                        fontFamily = getInterFontFamily()
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = WalletThemeV2.Dimensions.paddingLarge + 24.dp + WalletThemeV2.Dimensions.spacingMedium,
                        end = WalletThemeV2.Dimensions.paddingLarge,
                        bottom = WalletThemeV2.Dimensions.paddingSmall
                    )
            ) {
                Text(
                    text = stringResource(MR.strings.label_allocation),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = WalletThemeV2.Colors.tertiaryText,
                    fontFamily = getInterFontFamily(),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                token.accountBreakdown.forEachIndexed { index, breakdown ->
                    AccountAllocationRow(
                        breakdown = breakdown,
                        isBalanceHidden = isBalanceHidden,
                        currencySymbol = currencySymbol,
                        isPrimaryAccount = index == 0
                    )

                    if (index < token.accountBreakdown.lastIndex) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountAllocationRow(
    breakdown: EVMTokenAccountBreakdown,
    isBalanceHidden: Boolean,
    currencySymbol: String,
    isPrimaryAccount: Boolean,
    modifier: Modifier = Modifier
) {
    val indicatorColor = if (isPrimaryAccount) {
        WalletThemeV2.Colors.evmAccent
    } else {
        WalletThemeV2.Colors.secondaryText.copy(alpha = 0.6f)
    }

    val percentageText = remember(breakdown.percentage) {
        "${(breakdown.percentage * 100).toInt()}%"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = indicatorColor.copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = indicatorColor,
                    startAngle = -90f,
                    sweepAngle = 360f * breakdown.percentage,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text(
                text = percentageText,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = indicatorColor,
                fontFamily = getInterFontFamily()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = breakdown.accountName,
            fontSize = 12.sp,
            color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.8f),
            fontFamily = getInterFontFamily(),
            modifier = Modifier.weight(1f)
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (isBalanceHidden) HIDDEN_BALANCE_STRING
                else breakdown.balance.scale(4).toStringExpanded(),
                fontSize = 12.sp,
                color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.8f),
                fontFamily = getInterFontFamily()
            )

            Text(
                text = if (isBalanceHidden) HIDDEN_BALANCE_STRING
                else breakdown.valueUsd.formatFiat(currencySymbol, decimalPlaces = 2),
                fontSize = 10.sp,
                color = WalletThemeV2.Colors.secondaryText,
                fontFamily = getInterFontFamily()
            )
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
            pnlPercentage == null -> WalletThemeV2.Colors.secondaryText
            pnlPercentage > BigDecimal.ZERO -> WalletThemeV2.Colors.positiveGain
            pnlPercentage < BigDecimal.ZERO -> WalletThemeV2.Colors.negativeLoss
            else -> WalletThemeV2.Colors.secondaryText
        }
    }

    val label24h = stringResource(MR.strings.label_24h)

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
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                TokenLogo(
                    iconResource = token.logoUrl.takeIf { it.isNotEmpty() }?.let { ImageSource.Url(it) },
                    symbol = token.contractSymbol,
                    size = 22.dp
                )
            }

            Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingMedium))

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
                    text = label24h,
                    fontSize = 10.sp,
                    color = WalletThemeV2.Colors.tertiaryText,
                    fontFamily = getInterFontFamily()
                )
            }

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

private fun formatPnlPercentage(pnl: BigDecimal?, isHidden: Boolean): String {
    if (isHidden) return HIDDEN_BALANCE_STRING
    if (pnl == null) return "0.00%"

    val sign = if (pnl > BigDecimal.ZERO) "+" else ""
    return "$sign${pnl.scale(2).toStringExpanded()}%"
}

@Composable
private fun TokenListSkeleton(modifier: Modifier = Modifier) {
    Box(modifier = modifier.tokenListCard()) {
        Column {
            repeat(3) { index ->
                SkeletonTokenItem()
                if (index < 2) {
                    TokenListDivider()
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
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .mangalaWalletPlaceholder(visible = true)
        )

        Spacer(modifier = Modifier.width(WalletThemeV2.Dimensions.spacingMedium))

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
            .tokenListCard()
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
