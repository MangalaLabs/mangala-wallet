package com.mangala.wallet.model.portfolio

import kotlinx.serialization.Serializable

/**
 * DTO for the response when adding a wallet to a portfolio
 */
@Serializable
data class AddWalletToPortfolioResponseDto(
    val id: Int,
    val portfolioId: Int,
    val address: String,
    val label: String,
    val isActive: Boolean,
    val lastSync: String,
    val createdAt: String,
    val updatedAt: String
)