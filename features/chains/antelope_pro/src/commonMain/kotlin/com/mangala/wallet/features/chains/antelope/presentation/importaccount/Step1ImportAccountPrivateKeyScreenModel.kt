package com.mangala.wallet.features.chains.antelope.presentation.importaccount

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.createimport.GetAccountsByAuthorizersUseCase
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.memtrip.eos.core.crypto.EosPrivateKey.Companion.toEosPrivateKeyOrNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

class Step1ImportAccountPrivateKeyScreenModel(
    private val privateKey: String?,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountsByAuthorizersUseCase: GetAccountsByAuthorizersUseCase,
) : BaseScreenModel() {

    private lateinit var blockchainType: BlockchainType

    init {
        screenModelScope.launch {
            blockchainType = getSelectedNetworkUseCase().blockchainType
            privateKey?.let {
                onPrivateKeyChange(it)
            }
        }
    }

    private val _uiState: MutableStateFlow<Step1ImportAccountPrivateKeyScreenUiState> = MutableStateFlow(
        Step1ImportAccountPrivateKeyScreenUiState.NotImported(
            privateKey = "",
        )
    )
    val uiState: StateFlow<Step1ImportAccountPrivateKeyScreenUiState> = _uiState.asStateFlow()

    fun onPrivateKeyChange(privateKey: String) {
        _uiState.update {
            val state = (it as? Step1ImportAccountPrivateKeyScreenUiState.NotImported) ?: return@update it
            val error =
                if (privateKey.isNotBlank() && privateKey.toEosPrivateKeyOrNull() == null) "Invalid private key" else null
            state.copy(privateKey = privateKey, error = error)
        }
    }

    fun onNavigateToNextStep() {
        _uiState.value = Step1ImportAccountPrivateKeyScreenUiState.NotImported(
            privateKey = ""
        ) // Resets the value to prevent navigation loop
    }

    fun onImportAccount() {
        screenModelScope.launch {
            val state = (_uiState.value as? Step1ImportAccountPrivateKeyScreenUiState.NotImported) ?: return@launch
            if (state.isValid.not()) {
                return@launch
            }

            _uiState.update { state.copy(isLoading = true) }
            val account: Result<List<AntelopeAccountByAuthorizer>>
            val timeTaken = measureTime {
                account = getAccountsByAuthorizersUseCase(
                    state.privateKey,
                    blockchainType
                )
            }
            val minDuration = LOADING_SCREEN_MIN_DURATION_MILLIS.milliseconds
            val remainingTime = minDuration.minus(timeTaken)

            if (remainingTime > Duration.ZERO) {
                delay(remainingTime)
            }

            account.fold(
                onSuccess = { data ->
                    _uiState.update {
                        Step1ImportAccountPrivateKeyScreenUiState.Imported(
                            accountsByAuthorizers = data,
                            privateKey = (it as? Step1ImportAccountPrivateKeyScreenUiState.NotImported)?.privateKey.orEmpty()
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        state.copy(error = error.message.orEmpty())
                    }
                }
            )
        }
    }

    companion object {
        private const val LOADING_SCREEN_MIN_DURATION_MILLIS = 1000
    }
}