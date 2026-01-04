package com.mangala.wallet.domain.portfolio.model

data class CreatePortfolioRequest(
    val name: String,
    val description: String,
    val networkId: Int,
    val initialWallet: InitialWallet
)

data class InitialWallet(
    val address: String,
    val label: String
)