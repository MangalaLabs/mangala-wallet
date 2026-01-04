package com.mangala.wallet.wallet.presentation.reset

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.reset.model.ResetResult
import com.mangala.wallet.domain.reset.usecases.ResetWalletUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.wallet.presentation.reset.model.ResetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResetWalletScreenModel(
    private val resetWalletUseCase: ResetWalletUseCase
): BaseScreenModel() {

    private val _resetState = MutableStateFlow<ResetState>(ResetState.Idle)
    val resetState: StateFlow<ResetState> = _resetState.asStateFlow()

    fun resetWallet() {
        screenModelScope.launch {
            _resetState.value = ResetState.Loading

            try {
                val result = resetWalletUseCase()
                
                _resetState.value = when (result) {
                    is ResetResult.Success -> ResetState.Success
                    is ResetResult.Error -> ResetState.Error(result.message)
                }
            } catch (e: Exception) {
                _resetState.value = ResetState.Error(
                    message = e.message ?: "Unknown error occurred during reset"
                )
            }
        }
    }
}