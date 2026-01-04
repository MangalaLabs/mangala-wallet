package com.mangala.wallet.features.swap_base.presentation.preview


sealed class PreviewSwapUiState {
    data object Loading : PreviewSwapUiState()
    data class Success(val previewSwapUiModel: PreviewSwapUiModel) : PreviewSwapUiState()
    data class NeedApprove(val previewSwapApproveUiModel: PreviewSwapApproveUiModel) : PreviewSwapUiState()
}