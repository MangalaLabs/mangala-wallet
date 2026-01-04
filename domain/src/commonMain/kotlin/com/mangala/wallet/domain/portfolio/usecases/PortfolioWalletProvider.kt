package com.mangala.wallet.domain.portfolio.usecases

import com.mangala.wallet.domain.portfolio.model.InitialWallet

interface PortfolioWalletProvider {
    suspend fun provide(): List<InitialWallet>
}