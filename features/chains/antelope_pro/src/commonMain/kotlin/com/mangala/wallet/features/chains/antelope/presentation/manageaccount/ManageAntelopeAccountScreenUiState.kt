package com.mangala.wallet.features.chains.antelope.presentation.manageaccount

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount

sealed interface ManageAntelopeAccountScreenUiState {
    data object Loading : ManageAntelopeAccountScreenUiState
    data object NoAccount: ManageAntelopeAccountScreenUiState
    data class Success(val accounts: List<AntelopeAccount>) : ManageAntelopeAccountScreenUiState
}