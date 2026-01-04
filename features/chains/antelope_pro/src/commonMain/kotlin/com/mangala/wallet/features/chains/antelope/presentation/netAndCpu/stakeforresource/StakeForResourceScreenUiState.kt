package com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.stakeforresource

sealed class StakeForResourceScreenUiState {
    data class Error(val message : String): StakeForResourceScreenUiState()
    data object Loading : StakeForResourceScreenUiState()
    data class Success(val uiModel: StakeForResourceUiModel): StakeForResourceScreenUiState()
    data class ExecuteStakeForResourceSuccess(val txHash: String): StakeForResourceScreenUiState()
}