package com.mangala.wallet.model.portfolio

import kotlinx.serialization.Serializable

/**
 * DTO for portfolio creation response
 */
@Serializable
data class CreatePortfolioResponseDto(
    val id: String,
    val name: String,
    val description: String,
    val networkId: Int,
    val createdAt: String
)