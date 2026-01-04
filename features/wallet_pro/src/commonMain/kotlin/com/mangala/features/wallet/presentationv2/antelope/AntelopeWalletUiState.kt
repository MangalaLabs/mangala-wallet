package com.mangala.features.wallet.presentationv2.antelope

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.features.wallet.presentationv2.antelope.components.AccountInfo
import com.mangala.features.wallet.presentationv2.antelope.components.AggregatedTokenUiState
import com.mangala.features.wallet.presentationv2.antelope.components.TokenAccountBreakdown
import com.mangala.features.wallet.presentationv2.antelope.components.FilterOptions
import com.mangala.features.wallet.presentationv2.antelope.components.TokenSortBy
import com.mangala.features.wallet.presentationv2.antelope.components.ViewMode
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.provider.coingecko.CoinGeckoTokenPriceModel
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.ui.sumOf
import com.mangala.wallet.utils.average
import com.mangala.wallet.utils.ext.format
import com.mangala.wallet.utils.ext.formatCompact
import com.mangala.wallet.utils.ext.formatFiat
import io.github.aakira.napier.Napier
import kotlinx.datetime.Instant

data class AntelopeWalletUiState(
    val portfolioAccounts: List<AccountInfo>,
    val isPortfolioBalanceHidden: Boolean,

    val accountName: String,
    val isConnected: Boolean = false,
    val notificationCount: Int = 3,

    val totalBalance: BigDecimal = BigDecimal.ZERO,
    val usdEquivalent: BigDecimal = BigDecimal.ZERO,
    val pnlPercentage: BigDecimal = BigDecimal.ZERO,
    val pnlAmount: BigDecimal = BigDecimal.ZERO,
    val isBalanceHidden: Boolean = false,

    // Antelope specific balances
    val liquidBalance: Double = 0.0,
    val stakedBalance: Double = 0.0,
    val rexBalance: Double = 0.0,

    // Resources
    val cpuUsed: Int = 0,
    val cpuMax: Int = 100,
    val netUsed: Int = 0,
    val netMax: Int = 100,
    val ramUsed: Int = 0,
    val ramMax: Int = 100,
    val ramPrice: Double = 0.0,
    val ramPriceChange: Double = 0.0,

    // REX info
    val rexMaturityDate: Instant? = null,
    val rexEstimatedApr: Double = 0.0,

    // Search state
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",

    val filterOptions: FilterOptions = FilterOptions(),
    // Temporary toggle for testing adaptive UI
    val isSingleAccountMode: Boolean,
    val selectedNetwork: BlockchainNetworkData? = null,

    val portfolioSyncStatus: PortfolioSyncStatus = PortfolioSyncStatus.Idle,
    val portfolioError: String? = null,
    val isPortfolioDataAvailable: Boolean = false,
    val fiatSymbol: String,
    val selectedCoreBalanceUnit: AntelopeAccountBalanceUnit = AntelopeAccountBalanceUnit.NativeCoin,
    val coinGeckoExchangeRate: CoinGeckoTokenPriceModel?,
    val isLoadingWallets: Boolean = true,
    val isDevelopmentEnvironment: Boolean = false,
) {
    private val activeAccount = portfolioAccounts.find { it.isActive }
    val hasWallet: Boolean = portfolioAccounts.isNotEmpty() || isLoadingWallets

    val isAggregatedTokenView = filterOptions.viewMode == ViewMode.ALL_ACCOUNTS

    val currentAccountBalance: BigDecimal? = activeAccount?.nativeTokenValue
    val portfolioTotalBalance: BigDecimal? = portfolioAccounts.mapNotNull { it.nativeTokenValue }.ifEmpty { null }?.sumOf()
    val portfolioTotalUsd: BigDecimal? = portfolioAccounts.mapNotNull { it.usdValue }.ifEmpty { null }?.sumOf()
    val portfolioPnlAmount: BigDecimal?
        get() {
            return portfolioAccounts.mapNotNull { it.calculatedPnlAmount }.ifEmpty { null }?.sumOf()
        }

    fun getPortfolioPnlAmountInSelectedUnit(
        selectedCurrency: AntelopeAccountBalanceUnit,
        exchangeRateData: Map<String, BigDecimal>?
    ): BigDecimal? {
        return portfolioAccounts.mapNotNull {
            it.getCalculatedPnlAmountInSelectedUnit(selectedCurrency, exchangeRateData)
        }.ifEmpty { null }?.sumOf()
    }
    val portfolioPnlColor = if (portfolioPnlAmount != null && portfolioPnlAmount!! >= BigDecimal.ZERO) {
        WalletThemeV2.Colors.positiveGain
    } else {
        WalletThemeV2.Colors.negativeLoss
    }
    val portfolioPnlPercentage: BigDecimal
        get() {
            val portfolioAccountsWithPnl = portfolioAccounts.mapNotNull { it.calculatedPnlPercentage }

            return if (portfolioAccountsWithPnl.isNotEmpty()) {
                portfolioAccountsWithPnl.average()
            } else BigDecimal.ZERO
        }
    val portfolioPnlAmountFormatted = "${if (pnlAmount >= 0) "+" else ""}${pnlAmount.formatFiat("")} USDT (${if (pnlPercentage >= 0) "+" else ""}${pnlPercentage.format(2)}%)"

    val accountValueInSelectedUnit: BigDecimal? = when (selectedCoreBalanceUnit) {
        AntelopeAccountBalanceUnit.NativeCoin -> portfolioTotalBalance
        AntelopeAccountBalanceUnit.USDT -> portfolioTotalUsd
        else -> {
            val exchangeRate = coinGeckoExchangeRate?.data?.get(selectedCoreBalanceUnit.currencySymbol)
            if (exchangeRate != null && exchangeRate > BigDecimal.ZERO && portfolioTotalBalance != null) {
                portfolioTotalBalance.times(exchangeRate)
            } else {
                BigDecimal.ZERO
            }
        }
    }

    private val activeAccountTokens: List<TokenUiState>? = activeAccount?.tokens
    val filteredActiveAccountTokens: List<TokenUiState>? = filterAndSortTokens(activeAccountTokens)

    private val aggregatedTokens: List<AggregatedTokenUiState> = portfolioAccounts
        .flatMap { account ->
            account.tokens.orEmpty().map { token ->
                token to account.accountName
            }
        }
        .groupBy { (token, _) -> token.symbol }
        .map { (symbol, tokenAccountPairs) ->
            val tokens = tokenAccountPairs.map { it.first }
            val firstToken = tokens.first()
            val totalBalance = tokens.mapNotNull { it.balance }.sumOf()
            val totalUsdValue = tokens.mapNotNull {it.usdValue }.sumOf()
            val accountBreakdown = tokenAccountPairs.map { (token, accountName) ->
                TokenAccountBreakdown(accountName, token.balance, token.usdValue)
            }
            
            AggregatedTokenUiState(
                symbol = symbol,
                name = firstToken.name,
                decimals = firstToken.decimal,
                totalBalance = totalBalance,
                totalUsdValue = totalUsdValue,
                change24h = firstToken.change24h, // Use the first token's change24h as they should be the same for the same symbol
                accountCount = accountBreakdown.size,
                accountBreakdown = accountBreakdown,
                iconResource = firstToken.iconResource,
                priceInFiat = firstToken.priceInFiat
            )
        }
    val filteredAggregatedTokens: List<AggregatedTokenUiState> = filterAndSortAggregatedTokens(aggregatedTokens)

    private fun filterAndSortTokens(tokens: List<TokenUiState>?): List<TokenUiState>? {
        val filteredTokens = if (searchQuery.isNotBlank()) {
            tokens?.filter { it.name.contains(searchQuery, ignoreCase = true) || it.symbol.contains(searchQuery, ignoreCase = true) }
        } else {
            tokens
        }

        return when (filterOptions.sortBy) {
            TokenSortBy.NAME -> filteredTokens?.sortedBy { it.name }
            TokenSortBy.BALANCE -> filteredTokens?.sortedByDescending { it.balance }
            TokenSortBy.VALUE -> filteredTokens?.sortedByDescending { it.usdValue }
        }
    }
    
    private fun filterAndSortAggregatedTokens(tokens: List<AggregatedTokenUiState>): List<AggregatedTokenUiState> {
        val filteredTokens = if (searchQuery.isNotBlank()) {
            tokens.filter { it.name.contains(searchQuery, ignoreCase = true) || it.symbol.contains(searchQuery, ignoreCase = true) }
        } else {
            tokens
        }
        
        return when (filterOptions.sortBy) {
            TokenSortBy.NAME -> filteredTokens.sortedBy { it.name }
            TokenSortBy.BALANCE -> filteredTokens.sortedByDescending { it.totalBalance }
            TokenSortBy.VALUE -> filteredTokens.sortedByDescending { it.totalUsdValue }
        }
    }
}

data class TokenUiState(
    val symbol: String,
    val name: String,
    val decimal: Long = 4,
    val balance: BigDecimal?,
    val change24h: BigDecimal,
    val iconResource: ImageSource?,
    val contractAddress: String,
    private val nativeCoinPrice: BigDecimal?,
    val priceInNativeCoin: BigDecimal
) {
    val priceInFiat = nativeCoinPrice?.times(priceInNativeCoin)
    val usdValue: BigDecimal? = if (priceInFiat != null && balance != null) {
        balance * priceInFiat
    } else null
    val balanceFormatted = balance?.formatCompact(decimal)
    val usdValueFormatted = usdValue?.formatFiat("")
    val pnlAmount: BigDecimal? = if (usdValue != null) {
        usdValue * (change24h / 100)
    } else null
    val pnlColor = if (change24h >= BigDecimal.ZERO) {
        WalletThemeV2.Colors.positiveGain
    } else {
        WalletThemeV2.Colors.negativeLoss
    }

    fun getValueInSelectedUnit(
        selectedCurrency: AntelopeAccountBalanceUnit,
        exchangeRateData: Map<String, BigDecimal>?
    ): BigDecimal? {
        return when (selectedCurrency) {
            AntelopeAccountBalanceUnit.NativeCoin -> balance?.times(priceInNativeCoin)
            AntelopeAccountBalanceUnit.USDT -> usdValue
            else -> {
                val exchangeRate = exchangeRateData?.get(selectedCurrency.currencySymbol)
                if (exchangeRate != null && exchangeRate > BigDecimal.ZERO && usdValue != null) {
                    usdValue.times(exchangeRate)
                } else {
                    BigDecimal.ZERO
                }
            }
        }
    }

    fun getPnlAmountInSelectedUnit(
        selectedCurrency: AntelopeAccountBalanceUnit,
        exchangeRateData: Map<String, BigDecimal>?
    ): BigDecimal? {
        val valueInSelectedUnit = getValueInSelectedUnit(selectedCurrency, exchangeRateData)
        return if (valueInSelectedUnit != null) {
            valueInSelectedUnit * (change24h / 100)
        } else null
    }

    fun getPnlAmountFormattedInSelectedUnit(
        selectedCurrency: AntelopeAccountBalanceUnit,
        exchangeRateData: Map<String, BigDecimal>?
    ): String? {
        val pnlAmount = getPnlAmountInSelectedUnit(selectedCurrency, exchangeRateData) ?: return null

        return "${if (pnlAmount >= 0) "+" else if (pnlAmount < 0) "-" else ""}${pnlAmount.abs().formatFiat("")} ${selectedCurrency.symbol} (${if (change24h > 0) "+" else ""}${
            change24h.format(2)}%)"
    }
}

enum class PortfolioSyncStatus {
    Idle,
    Syncing,
    Success,
    Error,
    NetworkError,
    AuthError,
    PortfolioNotFound,
    Retrying,
    FallbackMode;
}
