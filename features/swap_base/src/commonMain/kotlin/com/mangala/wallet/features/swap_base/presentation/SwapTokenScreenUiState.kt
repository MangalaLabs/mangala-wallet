package com.mangala.wallet.features.swap_base.presentation

import com.mangala.wallet.ui.utils.WrappedStringResource


sealed class SwapTokenScreenUiState {
        data object Loading : SwapTokenScreenUiState()
        data class Success(val swapTokenScreenUiModel: SwapTokenUiModel) : SwapTokenScreenUiState()
        data class Error(val message: WrappedStringResource) : SwapTokenScreenUiState()
}