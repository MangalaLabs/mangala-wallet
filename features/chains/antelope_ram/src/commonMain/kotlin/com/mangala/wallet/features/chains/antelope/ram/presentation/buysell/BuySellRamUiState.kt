package com.mangala.wallet.features.chains.antelope.ram.presentation.buysell

sealed class BuySellRamUiState {
    data class Success(val uiModel: BuySellRamUiModel) : BuySellRamUiState()
    data class Error(val message: String) : BuySellRamUiState()
    data class ExecuteBuySellSuccess(val txHash: String): BuySellRamUiState()
}
