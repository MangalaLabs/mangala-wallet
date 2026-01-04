package com.mangala.wallet.model.portfolio

import kotlinx.serialization.Serializable

/**
 * DTO for creating a new portfolio
 */
@Serializable
data class CreatePortfolioRequestDto(
    val name: String,
    val description: String,
    val networkId: Int,
    val initialWallet: InitialWalletDto
)

/**
 * DTO for initial wallet configuration
 */
@Serializable
data class InitialWalletDto(
    val address: String,
    val label: String
)