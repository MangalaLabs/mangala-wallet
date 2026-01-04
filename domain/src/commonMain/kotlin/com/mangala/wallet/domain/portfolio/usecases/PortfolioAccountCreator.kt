package com.mangala.wallet.domain.portfolio.usecases

import com.mangala.wallet.domain.portfolio.mapper.PortfolioNetworkMapper
import com.mangala.wallet.domain.portfolio.model.CreatePortfolioRequest
import com.mangala.wallet.domain.portfolio.model.InitialWallet
import com.mangala.wallet.domain.wallet.usecases.account.AccountCreator
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.wallet.domain.WalletModel


/**
 * AccountCreator implementation that automatically registers portfolios 
 * with the Mangala Portfolio API when accounts are created
 */
class PortfolioAccountCreator(
    private val createPortfolioUseCase: CreatePortfolioUseCase,
    private val portfolioNetworkMapper: PortfolioNetworkMapper
) : AccountCreator {
    
    override suspend fun createAccount(
        accountId: String,
        derivationPathIndex: Int,
        wallet: WalletModel
    ) {
        try {
            // Register portfolios for all supported network types
            portfolioNetworkMapper.getSupportedNetworkTypes().forEach { networkType ->
                registerPortfolioForNetwork(
                    accountId = accountId,
                    wallet = wallet,
                    networkType = networkType
                )
            }
        } catch (e: Exception) {
            println("PortfolioAccountCreator: Failed to register portfolios for account $accountId: ${e.message}")
            // Don't throw - portfolio registration failure shouldn't block account creation
        }
    }
    
    private suspend fun registerPortfolioForNetwork(
        accountId: String,
        wallet: WalletModel,
        networkType: NetworkType
    ) {
        try {
            val networkId = portfolioNetworkMapper.mapNetworkTypeToNetworkId(networkType)
            
            val request = CreatePortfolioRequest(
                name = "${wallet.name} - ${networkType.name} Portfolio",
                description = "Auto-created portfolio for ${networkType.name} network",
                networkId = networkId,
                initialWallet = InitialWallet(
                    address = accountId,
                    label = wallet.name
                )
            )
            
            createPortfolioUseCase(request)
            println("PortfolioAccountCreator: Successfully registered portfolio for account $accountId on network ${networkType.name}")
            
        } catch (e: Exception) {
            println("PortfolioAccountCreator: Failed to register portfolio for account $accountId on network ${networkType.name}: ${e.message}")
            // Continue with other networks even if one fails
        }
    }
}