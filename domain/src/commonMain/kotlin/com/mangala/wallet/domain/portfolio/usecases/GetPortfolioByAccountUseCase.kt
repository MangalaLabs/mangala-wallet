package com.mangala.wallet.domain.portfolio.usecases

import com.mangala.wallet.domain.portfolio.model.Portfolio
import com.mangala.wallet.domain.portfolio.model.PortfolioAccount
import com.mangala.wallet.domain.portfolio.repository.PortfolioRepository

class GetPortfolioByAccountUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    
    suspend operator fun invoke(address: String): Portfolio? {
        return try {
            val portfolio = portfolioRepository.getPortfolioByAccountAddress(address)
            if (portfolio != null) {
                logPortfolioFound(address, portfolio)
            } else {
                logPortfolioNotFound(address)
            }
            portfolio
        } catch (e: Exception) {
            logSearchError(address, e)
            null
        }
    }
    
    suspend fun getAccountDetails(address: String): PortfolioAccount? {
        val portfolio = invoke(address)
        return portfolio?.accounts?.find { it.address == address }
    }
    
    /**
     * Find all accounts across all cached portfolios
     * @return List of all portfolio accounts for the current user
     */
    suspend fun getAllAccountsFromPortfolios(): List<PortfolioAccount> {
        return try {
            val portfolios = portfolioRepository.getCachedPortfolios()
            val allAccounts = portfolios.flatMap { it.accounts }
            logAccountsFound(allAccounts.size, portfolios.size)
            allAccounts
        } catch (e: Exception) {
            logSearchError("all accounts", e)
            emptyList()
        }
    }
    
    /**
     * Check if an account exists in any portfolio
     * @param address Account address to check
     * @return True if account exists in any portfolio, false otherwise
     */
    suspend fun hasAccount(address: String): Boolean {
        return invoke(address) != null
    }
    
    /**
     * Get portfolio and account details together
     * @param address Account address
     * @return Pair of Portfolio and PortfolioAccount or null if not found
     */
    suspend fun getPortfolioAndAccount(address: String): Pair<Portfolio, PortfolioAccount>? {
        val portfolio = invoke(address) ?: return null
        val account = portfolio.accounts.find { it.address == address } ?: return null
        return portfolio to account
    }
    
    /**
     * Find accounts by label/name pattern
     * @param labelPattern Pattern to match against account labels
     * @param ignoreCase Whether to ignore case in pattern matching
     * @return List of matching portfolio accounts
     */
    suspend fun findAccountsByLabel(labelPattern: String, ignoreCase: Boolean = true): List<PortfolioAccount> {
        val allAccounts = getAllAccountsFromPortfolios()
        return allAccounts.filter { account ->
            if (ignoreCase) {
                account.label.contains(labelPattern, ignoreCase = true)
            } else {
                account.label.contains(labelPattern)
            }
        }
    }
    
    private fun logPortfolioFound(address: String, portfolio: Portfolio) {
        val accountCount = portfolio.accounts.size
        val totalBalance = portfolio.totals.balanceUsdt
        println("Portfolio found for account $address: userId=${portfolio.userId}, accounts=$accountCount, balance=$totalBalance USDT")
    }
    
    private fun logPortfolioNotFound(address: String) {
        println("No portfolio found for account: $address")
    }
    
    private fun logAccountsFound(accountCount: Int, portfolioCount: Int) {
        println("Found $accountCount accounts across $portfolioCount portfolios")
    }
    
    private fun logSearchError(searchTarget: String, error: Exception) {
        println("Portfolio search failed for $searchTarget: ${error.message}")
    }
}