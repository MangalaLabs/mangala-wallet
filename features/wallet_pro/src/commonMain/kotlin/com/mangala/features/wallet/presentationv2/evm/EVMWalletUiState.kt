package com.mangala.features.wallet.presentationv2.evm

import androidx.compose.ui.graphics.Color
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.utils.PNL_DECIMAL_PLACES
import com.mangala.wallet.utils.ext.formatCompact
import com.mangala.wallet.utils.formattedAddress

/**
 * UI State for EVM Wallet Screen V2
 *
 * This state holds pre-calculated values from the UseCase.
 * Only presentation logic (formatting, visibility, colors) is handled here.
 * Business logic (portfolio calculation) is in GetAllWalletsPortfolioUseCase.
 */
data class EVMWalletUiState(
    val accounts: List<EVMAccountInfo>,
    val isPortfolioBalanceHidden: Boolean,
    val selectedAccountIndex: Int = 0,
    val fiatSymbol: String,
    val selectedNetwork: BlockchainNetworkData? = null,
    val isLoadingWallets: Boolean = true,
    val isDevelopmentEnvironment: Boolean = false,
    // Pre-calculated portfolio totals from UseCase
    val portfolioTotalUsd: BigDecimal = BigDecimal.ZERO,
    val portfolioPnl: BigDecimal = BigDecimal.ZERO,
    val portfolioPnlPercentage: BigDecimal = BigDecimal.ZERO,
    // Search state
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",
) {
    val hasWallet: Boolean = accounts.isNotEmpty() || isLoadingWallets

    val isSingleAccountMode: Boolean = accounts.size <= 1

    val activeAccount: EVMAccountInfo? = accounts.getOrNull(selectedAccountIndex)

    // Presentation: Format portfolio total for display
    val portfolioTotalUsdFormatted: String
        get() = if (isPortfolioBalanceHidden) {
            HIDDEN_BALANCE_STRING
        } else {
            fiatSymbol + portfolioTotalUsd.formatCompact(PNL_DECIMAL_PLACES)
        }

    // Presentation: Format PnL percentage for display
    val portfolioPnlFormatted: String
        get() {
            if (accounts.isEmpty()) return "0%"
            if (isPortfolioBalanceHidden) return HIDDEN_BALANCE_STRING

            val pnlSign = if (portfolioPnlPercentage > BigDecimal.ZERO) "+" else ""
            return pnlSign + portfolioPnlPercentage.scale(PNL_DECIMAL_PLACES).toStringExpanded() + "%"
        }

    // Presentation: Determine PnL color based on value
    val portfolioPnlColor: Color
        get() {
            if (isPortfolioBalanceHidden) return Colors.gray
            return when {
                portfolioPnl == BigDecimal.ZERO -> Colors.gray
                portfolioPnl > BigDecimal.ZERO -> Color(0xFF00A699)
                else -> Colors.coral
            }
        }
}

/**
 * EVM Account info for UI display.
 *
 * Receives pre-calculated values from the domain layer (AccountPortfolio).
 * Only handles presentation concerns (formatting, visibility, colors).
 */
data class EVMAccountInfo(
    val accountId: String,
    val accountName: String,
    val address: String,
    val walletId: String = "",
    val balances: List<TokenBalanceModel>? = null,
    val isActive: Boolean = false,
    val isBalanceVisible: Boolean = true,
    val currencySymbol: String = "$",
    // Pre-calculated values from UseCase
    val totalValueUsd: BigDecimal = BigDecimal.ZERO,
    val pnl: BigDecimal = BigDecimal.ZERO,
    val yesterdayTotalValue: BigDecimal = BigDecimal.ZERO,
) {
    val totalValuePlaceholderEnabled: Boolean = balances == null

    // Presentation: Format total value for display
    val totalValueFormatted: String
        get() = if (!isBalanceVisible) {
            HIDDEN_BALANCE_STRING
        } else {
            currencySymbol + totalValueUsd.formatCompact(PNL_DECIMAL_PLACES)
        }

    val formattedPnlPlaceholderEnabled: Boolean = balances == null

    // Presentation: Format PnL for display
    val formattedPnl: String
        get() {
            if (balances == null) return "0%"
            if (!isBalanceVisible) return HIDDEN_BALANCE_STRING

            val pnlSign = if (pnl > BigDecimal.ZERO) "+" else ""
            val pnlPercentage = if (yesterdayTotalValue != BigDecimal.ZERO) {
                pnl.divide(
                    yesterdayTotalValue,
                    DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 10)
                )
                    .multiply(BigDecimal.fromInt(100))
            } else {
                BigDecimal.ZERO
            }
            return pnlSign + pnlPercentage.scale(PNL_DECIMAL_PLACES).toStringExpanded() + "%"
        }

    // Presentation: Determine PnL color
    val pnlColor: Color
        get() {
            if (!isBalanceVisible) return Colors.gray
            return when {
                pnl == BigDecimal.ZERO -> Colors.gray
                pnl > BigDecimal.ZERO -> Color(0xFF00A699)
                else -> Colors.coral
            }
        }

    // Presentation: Truncated address for display (e.g., 0x1234AB...cdef)
    val displayAddress: String = address.formattedAddress(leadingCharsCount = 8, trailingCharsCount = 4)
}
