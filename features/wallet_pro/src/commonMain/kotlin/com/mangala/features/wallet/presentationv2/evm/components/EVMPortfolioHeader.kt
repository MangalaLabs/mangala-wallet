package com.mangala.features.wallet.presentationv2.evm.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.features.wallet.presentationv2.evm.EVMAccountInfo
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Hide
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Show
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.utils.PNL_DECIMAL_PLACES
import com.mangala.wallet.utils.ext.formatCompact

/**
 * Portfolio header for EVM wallet screen
 * Key difference from Antelope: displays truncated hex address (0x1234...abcd)
 * instead of account names
 */
@Composable
fun EVMPortfolioHeader(
    isSingleAccountMode: Boolean,
    activeAccount: EVMAccountInfo?,
    totalPortfolioUsd: BigDecimal?,
    totalPnlAmountFormatted: String?,
    pnlColor: Color,
    isBalanceHidden: Boolean,
    fiatSymbol: String,
    accounts: List<EVMAccountInfo>,
    onToggleHideBalance: () -> Unit,
    onCopyAddress: () -> Unit,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isSingleAccountMode,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        modifier = modifier
    ) { singleMode ->
        if (singleMode) {
            // Single Account View
            SingleAccountEVMView(
                account = activeAccount,
                isBalanceHidden = isBalanceHidden,
                onToggleHideBalance = onToggleHideBalance,
                onCopyAddress = onCopyAddress
            )
        } else {
            // Multi Account View
            MultiAccountEVMView(
                totalPortfolioUsd = totalPortfolioUsd,
                totalPnlAmountFormatted = totalPnlAmountFormatted,
                pnlColor = pnlColor,
                activeAccount = activeAccount,
                isBalanceHidden = isBalanceHidden,
                fiatSymbol = fiatSymbol,
                onToggleHideBalance = onToggleHideBalance,
                onCopyAddress = onCopyAddress,
                onAccountClick = onAccountClick
            )
        }
    }
}

@Composable
private fun SingleAccountEVMView(
    account: EVMAccountInfo?,
    isBalanceHidden: Boolean,
    onToggleHideBalance: () -> Unit,
    onCopyAddress: () -> Unit,
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Address Label Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Address with copy icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Truncated address display (0x1234...abcd)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = account?.displayAddress ?: "0x0000...0000",
                            fontSize = 13.sp,
                            color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            fontFamily = getInterFontFamily(),
                            modifier = Modifier.mangalaWalletPlaceholder(account == null)
                        )
                    }

                    // Copy icon
                    Icon(
                        imageVector = MangalaWalletPack.Copy,
                        contentDescription = "Copy address",
                        tint = WalletThemeV2.Colors.secondaryText,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onCopyAddress() }
                    )
                }

                // Hide/Show Balance Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(WalletThemeV2.Colors.secondaryBackground.copy(alpha = 0.5f))
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

            // Balance Display - uses totalValueFormatted from EVMAccountInfo (same as EvmAccountItemUiModel)
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (isBalanceHidden) {
                        "••••••"
                    } else {
                        account?.totalValueFormatted ?: "\$0.00"
                    },
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = WalletThemeV2.Colors.primaryText,
                    fontFamily = getInterFontFamily(),
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .basicMarquee()
                        .mangalaWalletPlaceholder(account?.totalValuePlaceholderEnabled == true)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "USD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily(),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // PnL Display - uses formattedPnl from EVMAccountInfo (same as EvmAccountItemUiModel)
            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))

            EVMPnlDisplay(
                pnlAmountFormatted = account?.formattedPnl,
                pnlColor = account?.pnlColor ?: WalletThemeV2.Colors.secondaryText,
                isBalanceHidden = isBalanceHidden,
                isLoading = account?.formattedPnlPlaceholderEnabled == true
            )
        }
    }
}

@Composable
private fun MultiAccountEVMView(
    totalPortfolioUsd: BigDecimal?,
    totalPnlAmountFormatted: String?,
    pnlColor: Color,
    activeAccount: EVMAccountInfo?,
    isBalanceHidden: Boolean,
    fiatSymbol: String,
    onToggleHideBalance: () -> Unit,
    onCopyAddress: () -> Unit,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Portfolio Total Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 0.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                        text = if (totalPortfolioUsd != null) {
                            "$${totalPortfolioUsd.formatCompact(PNL_DECIMAL_PLACES)}"
                        } else {
                            "$0.00 USD"
                        },
                        fontSize = 15.sp,
                        color = WalletThemeV2.Colors.secondaryText,
                        fontWeight = FontWeight.Medium,
                        fontFamily = getInterFontFamily(),
                        modifier = Modifier.mangalaWalletPlaceholder(totalPortfolioUsd == null)
                    )
                }
            }
        }

        // Active Account Card
        ActiveAccountCard(
            account = activeAccount,
            isBalanceHidden = isBalanceHidden,
            onToggleHideBalance = onToggleHideBalance,
            onCopyAddress = onCopyAddress,
            onAccountClick = onAccountClick
        )
    }
}

@Composable
private fun ActiveAccountCard(
    account: EVMAccountInfo?,
    isBalanceHidden: Boolean,
    onToggleHideBalance: () -> Unit,
    onCopyAddress: () -> Unit,
    onAccountClick: () -> Unit,
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Address Row with account switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Clickable address box with switch indicator
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onAccountClick() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = account?.displayAddress ?: "0x0000...0000",
                                fontSize = 13.sp,
                                color = WalletThemeV2.Colors.primaryText.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium,
                                fontFamily = getInterFontFamily(),
                                modifier = Modifier.mangalaWalletPlaceholder(account == null)
                            )

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Switch Account",
                                tint = WalletThemeV2.Colors.tertiaryText,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Copy icon
                    Icon(
                        imageVector = MangalaWalletPack.Copy,
                        contentDescription = "Copy address",
                        tint = WalletThemeV2.Colors.secondaryText,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onCopyAddress() }
                    )
                }

                // Hide/Show Balance Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(WalletThemeV2.Colors.secondaryBackground.copy(alpha = 0.5f))
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

            // Balance Display - uses totalValueFormatted from EVMAccountInfo
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (isBalanceHidden) {
                        "••••••"
                    } else {
                        account?.totalValueFormatted ?: "\$0.00"
                    },
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = WalletThemeV2.Colors.primaryText,
                    fontFamily = getInterFontFamily(),
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .basicMarquee()
                        .mangalaWalletPlaceholder(account?.totalValuePlaceholderEnabled == true)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "USD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily(),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // PnL Display - uses formattedPnl from EVMAccountInfo
            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingMedium))

            EVMPnlDisplay(
                pnlAmountFormatted = account?.formattedPnl,
                pnlColor = account?.pnlColor ?: WalletThemeV2.Colors.secondaryText,
                isBalanceHidden = isBalanceHidden,
                isLoading = account?.formattedPnlPlaceholderEnabled == true
            )
        }
    }
}

@Composable
private fun EVMPnlDisplay(
    pnlAmountFormatted: String?,
    pnlColor: Color,
    isBalanceHidden: Boolean = false,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val hiddenPlaceholder = "+*.**%"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "24h: ",
            fontSize = WalletThemeV2.Typography.fontSizeBody,
            color = WalletThemeV2.Colors.secondaryText,
            fontFamily = getInterFontFamily(),
            modifier = Modifier.mangalaWalletPlaceholder(isLoading)
        )

        Text(
            text = when {
                isBalanceHidden -> hiddenPlaceholder
                pnlAmountFormatted != null -> pnlAmountFormatted
                else -> "0%"
            },
            fontSize = WalletThemeV2.Typography.fontSizeBody,
            fontWeight = FontWeight.Medium,
            color = when {
                isBalanceHidden -> WalletThemeV2.Colors.secondaryText
                else -> pnlColor
            },
            fontFamily = getInterFontFamily(),
            modifier = Modifier.mangalaWalletPlaceholder(isLoading)
        )
    }
}
