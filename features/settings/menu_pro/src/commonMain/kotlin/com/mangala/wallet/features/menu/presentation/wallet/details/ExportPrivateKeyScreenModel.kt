package com.mangala.wallet.features.menu.presentation.wallet.details

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.account.usecases.GetEvmAccountPrivateKeyUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExportPrivateKeyScreenModel(
    private val walletId: String,
    private val accountId: String,
    private val getEvmAccountPrivateKeyUseCase: GetEvmAccountPrivateKeyUseCase
) : BaseScreenModel() {

    val uiModel = MutableStateFlow(ExportPrivateKeyScreenUiModel())

    init {
        loadPrivateKey()
    }

    fun onTogglePrivateKeyVisibility() {
        uiModel.update { current ->
            current.copy(isPrivateKeyVisible = current.isPrivateKeyVisible.not())
        }
    }

    private fun loadPrivateKey() {
        screenModelScope.launch {
            uiModel.update { it.copy(isLoading = true, isError = false) }
            runCatching {
                getEvmAccountPrivateKeyUseCase(walletId = walletId, accountId = accountId)
            }.onSuccess { privateKey ->
                uiModel.update {
                    it.copy(
                        privateKey = privateKey,
                        isLoading = false,
                        isError = false
                    )
                }
            }.onFailure {
                uiModel.update {
                    it.copy(
                        privateKey = "",
                        isLoading = false,
                        isError = true
                    )
                }
            }
        }
    }
}
