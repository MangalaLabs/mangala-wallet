package com.mangala.wallet.domain.portfolio.usecases

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.wallet.domain.account.usecases.GetAccountBalanceUseCase
import com.mangala.wallet.domain.portfolio.model.AccountPortfolio
import com.mangala.wallet.domain.portfolio.model.PortfolioData
import com.mangala.wallet.domain.wallet.usecases.GetAllWalletsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletAccountsUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine

/**
 * UseCase that fetches portfolio data from ALL wallets (seedphrase + imported)
 * and ALL HD accounts from each wallet.
 *
 * This UseCase contains the business logic for:
 * - Aggregating accounts from multiple wallets
 * - Fetching balances for each account
 * - Calculating total portfolio value and PnL
 *
 * In the future, this can be refactored to delegate calculation to backend
 * for advanced features (caching, secure API keys, etc.)
 */
class GetAllWalletsPortfolioUseCase(
    private val getAllWalletsUseCase: GetAllWalletsUseCase,
    private val getWalletAccountsUseCase: GetWalletAccountsUseCase,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase
) {
    companion object {
        private const val MAX_TOKENS_PER_ACCOUNT = 10
    }

    operator fun invoke(
        forceReload: Boolean,
        network: BlockchainNetworkData
    ): Flow<Resource<PortfolioData>> = channelFlow {
        // Step 1: Get all wallets
        val allWallets = getAllWalletsUseCase()

        if (allWallets.isEmpty()) {
            send(Resource.Success(createEmptyPortfolio()))
            return@channelFlow
        }

        // Step 2: Get all accounts from all wallets
        val accountsInfo = mutableListOf<AccountInfo>()
        allWallets.forEach { wallet ->
            val accounts = getWalletAccountsUseCase(
                filterHiddenAccounts = true,
                walletId = wallet.id
            )
            accounts?.forEach { accountBlockchainModel ->
                accountsInfo.add(
                    AccountInfo(
                        accountId = accountBlockchainModel.account.id,
                        accountName = accountBlockchainModel.account.name,
                        address = accountBlockchainModel.bip44Address,
                        walletId = wallet.id
                    )
                )
            }
        }

        if (accountsInfo.isEmpty()) {
            send(Resource.Success(createEmptyPortfolio()))
            return@channelFlow
        }

        // Send loading state
        send(Resource.Loading(createEmptyPortfolio()))

        // Step 3: Create balance flows for each account
        val balanceFlows = accountsInfo.map { info ->
            getAccountBalanceUseCase.invokeFlowResource(
                forceReload = forceReload,
                address = info.address,
                blockchainType = network.blockchainType,
                accountId = info.accountId
            )
        }

        // Step 4: Combine all balance flows and calculate portfolio
        combine(balanceFlows) { balanceResults ->
            balanceResults.toList()
        }.collect { allBalances ->
            val isLoading = allBalances.any { it is Resource.Loading }
            val hasError = allBalances.all { it is Resource.Error }

            // Step 5: Calculate portfolio data (BUSINESS LOGIC)
            val portfolioData = calculatePortfolio(accountsInfo, allBalances)

            when {
                isLoading -> send(Resource.Loading(portfolioData))
                hasError -> send(Resource.Error(Exception("Failed to load balances"), portfolioData))
                else -> send(Resource.Success(portfolioData))
            }
        }
    }

    /**
     * Core business logic: Calculate portfolio totals from account balances.
     * This is where the calculation happens - can be replaced with backend call later.
     */
    private fun calculatePortfolio(
        accountsInfo: List<AccountInfo>,
        allBalances: List<Resource<List<TokenBalanceModel>>>
    ): PortfolioData {
        var totalPortfolioValue = BigDecimal.ZERO
        var totalPortfolioPnl = BigDecimal.ZERO
        var totalYesterdayValue = BigDecimal.ZERO

        val accountPortfolios = accountsInfo.mapIndexed { index, info ->
            val balanceResource = allBalances.getOrNull(index)
            val balances = balanceResource?.data
                ?.filter { it.isCoin || it.balance.toDouble() > 0 }
                ?.take(MAX_TOKENS_PER_ACCOUNT)
                ?: emptyList()

            // Calculate account totals
            val accountTotalValue = calculateAccountTotalValue(balances)
            val accountYesterdayValue = calculateAccountYesterdayValue(balances)
            val accountPnl = accountTotalValue - accountYesterdayValue

            // Accumulate portfolio totals
            totalPortfolioValue += accountTotalValue
            totalYesterdayValue += accountYesterdayValue
            totalPortfolioPnl += accountPnl

            AccountPortfolio(
                accountId = info.accountId,
                accountName = info.accountName,
                address = info.address,
                walletId = info.walletId,
                balances = balances,
                totalValueUsd = accountTotalValue,
                pnl = accountPnl,
                yesterdayTotalValue = accountYesterdayValue
            )
        }

        // Calculate PnL percentage
        val pnlPercentage = if (totalYesterdayValue != BigDecimal.ZERO) {
            totalPortfolioPnl.divide(
                totalYesterdayValue,
                DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 10)
            ).multiply(BigDecimal.fromInt(100))
        } else {
            BigDecimal.ZERO
        }

        return PortfolioData(
            accounts = accountPortfolios,
            totalValueUsd = totalPortfolioValue,
            totalPnl = totalPortfolioPnl,
            totalPnlPercentage = pnlPercentage
        )
    }

    private fun calculateAccountTotalValue(balances: List<TokenBalanceModel>): BigDecimal {
        return balances.fold(BigDecimal.ZERO) { acc, token ->
            acc + (token.todaysValue ?: BigDecimal.ZERO)
        }
    }

    private fun calculateAccountYesterdayValue(balances: List<TokenBalanceModel>): BigDecimal {
        return balances.fold(BigDecimal.ZERO) { acc, token ->
            acc + (token.yesterdaysValue ?: BigDecimal.ZERO)
        }
    }

    private fun createEmptyPortfolio(): PortfolioData {
        return PortfolioData(
            accounts = emptyList(),
            totalValueUsd = BigDecimal.ZERO,
            totalPnl = BigDecimal.ZERO,
            totalPnlPercentage = BigDecimal.ZERO
        )
    }

    /**
     * Internal data class for holding account info before balance is loaded
     */
    private data class AccountInfo(
        val accountId: String,
        val accountName: String,
        val address: String,
        val walletId: String
    )
}
