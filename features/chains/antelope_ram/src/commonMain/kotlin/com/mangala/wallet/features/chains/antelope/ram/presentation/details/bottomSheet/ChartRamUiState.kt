package com.mangala.wallet.features.chains.antelope.ram.presentation.details.bottomSheet

import com.mangala.wallet.ui.utils.WrappedStringResource

sealed interface ChartRamUiState {
    data object Loading : ChartRamUiState
    data class Success(val chartRamUiModel: ChartRamUiModel) : ChartRamUiState
    data class Error(val message: WrappedStringResource) : ChartRamUiState
}