package com.mangala.wallet.domain.portfolio.usecases

import com.mangala.wallet.core.auth.SessionManager
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.portfolio.repository.PortfolioRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.firstOrNull

/**
 * Use case that ensures an account is added to the user's portfolio.
 * If no portfolio exists for the user/network, creates one.
 * If account already exists in portfolio, does nothing.
 */
class EnsureAccountInPortfolioUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val createPortfolioUseCase: CreatePortfolioUseCase,
    private val addAddressToPortfolioUseCase: AddAddressToPortfolioUseCase,
    private val sessionManager: SessionManager,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) {
    
    suspend operator fun invoke(
        accountName: String,
        customLabel: String? = null,
        blockchainType: BlockchainType? = null
    ): Result<Unit> {
        return try {
            // Step 1: Validate user authentication
            val userId = sessionManager.getCurrentUserId()
            if (userId == null || !sessionManager.isSessionActive()) {
                // User not logged in - skip portfolio integration but don't fail
                println("User not logged in, skipping portfolio integration for account: $accountName")
                return Result.success(Unit)
            }
            
            // Step 2: Determine network ID from blockchain type
            val resolvedBlockchainType = blockchainType ?: getSelectedNetworkUseCase().blockchainType
            val networkId = resolvedBlockchainType.getNetworkId()
            
            // Step 3: Check for existing portfolio
            val existingPortfolio = portfolioRepository.getPortfolioByNetworkFlow(
                userId = userId,
                networkId = networkId,
                forceRefresh = false
            ).firstOrNull()?.data
            
            when (existingPortfolio) {
                null -> {
                    // No portfolio exists, create one with this account
                    val label = customLabel ?: generateDefaultLabel(accountName)
                    val portfolioName = generatePortfolioName(resolvedBlockchainType)
                    
                    createPortfolioUseCase.createPortfolioForAccount(
                        accountName = accountName,
                        accountLabel = label,
                        networkId = networkId,
                        portfolioName = portfolioName
                    )
                    
                    println("Created new portfolio for account: $accountName")
                    Result.success(Unit)
                }
                else -> {
                    // Portfolio exists, check if account is already in it
                    val accountExists = existingPortfolio.portfolio.accounts.any { it.address == accountName }
                    
                    if (accountExists) {
                        println("Account $accountName already exists in portfolio")
                        Result.success(Unit)
                    } else {
                        // Add account to existing portfolio
                        val label = customLabel ?: generateDefaultLabel(accountName)
                        addAddressToPortfolioUseCase(
                            portfolioId = existingPortfolio.portfolio.portfolioId,
                            address = accountName,
                            label = label
                        )
                        
                        println("Added account $accountName to existing portfolio")
                        Result.success(Unit)
                    }
                }
            }
        } catch (e: Exception) {
            // Log error but don't fail the account operation
            println("Portfolio integration failed for account $accountName: ${e.message}")
            
            // TODO: Queue for background retry
            // For now, we'll just log and continue
            Result.success(Unit)
        }
    }
    
    private fun generateDefaultLabel(accountName: String): String {
        return if (accountName.length > 10) {
            "${accountName.take(8)}..."
        } else {
            accountName
        }
    }
    
    private fun generatePortfolioName(blockchainType: BlockchainType): String {
        return when (blockchainType.name.uppercase()) {
            "EOS" -> "EOS Portfolio"
            "JUNGLE" -> "Jungle Testnet Portfolio"
            "WAX" -> "WAX Portfolio" 
            "TELOS" -> "Telos Portfolio"
            else -> "${blockchainType.name.replaceFirstChar { it.uppercase() }} Portfolio"
        }
    }
}

/**
 * Extension to get network ID from blockchain type
 */
private fun BlockchainType.getNetworkId(): Int {
    return when (this.name.uppercase()) {
        "EOS" -> 1
        "JUNGLE" -> 2
        "WAX" -> 3
        "TELOS" -> 4
        else -> 1 // Default to mainnet
    }
}