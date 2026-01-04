package com.mangala.wallet.domain.portfolio.usecases

import com.mangala.wallet.domain.portfolio.repository.PortfolioRepository

class AddAddressToPortfolioUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    suspend operator fun invoke(
        portfolioId: String,
        address: String,
        label: String
    ) {
        portfolioRepository.addWalletToPortfolio(portfolioId = portfolioId, address = address, label = label)
    }
}