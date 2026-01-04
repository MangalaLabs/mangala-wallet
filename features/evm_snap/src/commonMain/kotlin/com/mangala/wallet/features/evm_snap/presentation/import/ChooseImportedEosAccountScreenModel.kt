package com.mangala.wallet.features.evm_snap.presentation.import

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.createimport.GetAccountsByAuthorizersUseCase
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

class ChooseImportedEosAccountScreenModel(
    private val getAccountsByAuthorizersUseCase: GetAccountsByAuthorizersUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val saveSelectedNetworkUseCase: SaveSelectedNetworkUseCase
    ): BaseScreenModel() {
    private var _uiState: MutableStateFlow<ChooseImportedEosAccountUIState> = MutableStateFlow(
        ChooseImportedEosAccountUIState.Loading
    )
    val uiState: StateFlow<ChooseImportedEosAccountUIState> = _uiState.asStateFlow()

    init {
        _uiState.value = ChooseImportedEosAccountUIState.Loading
    }

    fun findEosAccountFromPrivateKey(privateKey: String) {
        screenModelScope.launch {
            val currentNetwork = getSelectedNetworkUseCase.invoke().blockchainType
            val accounts: Result<List<AntelopeAccountByAuthorizer>>
            val timeTaken = measureTime {
                accounts = getAccountsByAuthorizersUseCase.invoke(
                    privateKey = privateKey,
                    blockchainType = currentNetwork
                )
            }
            val minDuration = LOADING_SCREEN_MIN_DURATION_MILLIS.milliseconds
            val remainingTime = minDuration.minus(timeTaken)

            if (remainingTime > Duration.ZERO) {
                delay(remainingTime)
            }

            accounts.fold(
                onSuccess = { data ->
                    _uiState.value = ChooseImportedEosAccountUIState.Success(privateKey, data)
                },
                onFailure = { error ->
                    println("=== got error when importing account: $error")
                    _uiState.value = ChooseImportedEosAccountUIState.Error(
                        WrappedStringResource.StringRes(
                            MR.strings.error_get_account_from_private_key_failed
                        )
                    )
                }
            )
        }
    }

    companion object {
        private const val LOADING_SCREEN_MIN_DURATION_MILLIS = 1000
    }
}

sealed class ChooseImportedEosAccountUIState {
    data object Loading: ChooseImportedEosAccountUIState()
    data class Success(val privateKey: String, val accounts: List<AntelopeAccountByAuthorizer>): ChooseImportedEosAccountUIState()
    data class Error(val message: WrappedStringResource): ChooseImportedEosAccountUIState()
}