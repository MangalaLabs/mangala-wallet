package com.mangala.wallet.domain.portfolio.usecases

import com.mangala.wallet.domain.portfolio.model.CreatePortfolioRequest
import com.mangala.wallet.domain.portfolio.model.InitialWallet
import com.mangala.wallet.domain.portfolio.model.Portfolio
import com.mangala.wallet.domain.portfolio.repository.PortfolioRepository

class CreatePortfolioUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val portfolioWalletProviders: List<PortfolioWalletProvider>
) {

    suspend operator fun invoke(): Result<Portfolio> {
        val initialWallets = portfolioWalletProviders.flatMap { it.provide() }.ifEmpty {
            return Result.failure(Exception("No initial wallets provided"))
        }

        val request = CreatePortfolioRequest(
            name = "My Portfolio",
            description = "Cryptocurrency portfolio",
            networkId = 1,
            initialWallet = initialWallets.first()
        )
        return invoke(request)
    }
    
    suspend operator fun invoke(request: CreatePortfolioRequest): Result<Portfolio> {
        return try {
            val result = portfolioRepository.createPortfolio(request)
            if (result.isFailure) {
                // TODO: Based on the error, decide whether to add wallets or not
                val portfolio = portfolioRepository.getPortfolioByNetwork(
                    userId = "currentUserId", // Replace with actual user ID retrieval logic
                    networkId = request.networkId
                )
                portfolioRepository.addWalletToPortfolio(
                    portfolio.getOrNull()?.portfolio?.portfolioId.orEmpty(),
                    request.initialWallet.address,
                    request.initialWallet.label
                )
            }

            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createPortfolioForAccount(
        accountName: String,
        accountLabel: String,
        networkId: Int,
        portfolioName: String? = null
    ): Result<Portfolio> {
        val request = CreatePortfolioRequest(
            name = portfolioName ?: "$accountLabel Portfolio",
            description = "Cryptocurrency portfolio for $accountLabel",
            networkId = networkId,
            initialWallet = InitialWallet(
                address = accountName,
                label = accountLabel
            )
        )
        
        return invoke(request)
    }
}