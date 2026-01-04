package com.mangala.wallet.features.chains.antelope.presentation.permission.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PermissionDetailScreenModel(
    private val accountName: String,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountUseCase: GetAccountInfoUseCase
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<PermissionDetailScreenUiState> = MutableStateFlow(
        PermissionDetailScreenUiState.Loading
    )

    val uiState: StateFlow<PermissionDetailScreenUiState> = _uiState.asStateFlow()

    fun getPermissionDetail() {
        screenModelScope.launch {
            _uiState.value = PermissionDetailScreenUiState.Loading
            try {
                val blockchainType = getSelectedNetworkUseCase.invoke().blockchainType

                val data = getAccountUseCase.invoke(
                    blockchainType,
                    accountName
                )

                println("getPermissionDetail: $data")
                if (data != null) {
                    _uiState.value = PermissionDetailScreenUiState.Success(data.permissions!!)
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
                _uiState.value =
                    PermissionDetailScreenUiState.Error(e.message ?: "An error occurred")
            }
        }
    }
}