package com.mangala.wallet.features.portfolio.presentation

sealed class PortfolioScreenUiState() {
    data class Data(val portfolioScreenUiModel: PortfolioScreenUiModel) : PortfolioScreenUiState()
}
