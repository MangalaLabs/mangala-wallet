package com.mangala.wallet.features.nft_base.presentation.details

import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.ui.utils.WrappedStringResource

sealed interface NftDetailsScreenUiState {
    data object Loading : NftDetailsScreenUiState
    data class Success(val uiModel: NftDetailsScreenUiModel) : NftDetailsScreenUiState
    data class Error(val message: WrappedStringResource) : NftDetailsScreenUiState
}

data class NftDetailsScreenUiModel(
    val nftCollection: NftCollection
)