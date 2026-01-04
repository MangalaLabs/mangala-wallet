package com.mangala.wallet.model.portfolio

import kotlinx.serialization.Serializable

/**
 * DTO for adding a wallet to an existing portfolio
 */
@Serializable
data class AddWalletToPortfolioRequestDto(
    val address: String,
    val label: String
)