package com.mangala.wallet.features.chains.antelope.presentation.importaccount

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.linh.antelope_qr.domain.model.ImportAccountRequest
import com.linh.antelope_qr.domain.usecase.EncodeRequestToQrCodeUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckPublicKeyLinkedToAccountNameUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.memtrip.eos.core.crypto.EosPublicKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImportAccountScreenModel(
    private val checkPublicKeyLinkedToAccountNameUseCase: CheckPublicKeyLinkedToAccountNameUseCase,
    private val encodeRequestToQrCodeUseCase: EncodeRequestToQrCodeUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<ImportAccountScreenUiState> = MutableStateFlow(ImportAccountScreenUiState.NotScanned)
    val uiState: StateFlow<ImportAccountScreenUiState> = _uiState.asStateFlow()

    fun onScan(importAccountRequest: ImportAccountRequest) {
        val eosPublicKey = EosPublicKey(importAccountRequest.publicKey)

        _uiState.value = ImportAccountScreenUiState.Scanned(
            publicKey = eosPublicKey.toString(),
            accountName = ""
        )
    }

    fun onAccountNameChange(accountName: String) {
        // TODO: Can use debounce to check for valid account name instead of using a submit button
        // TODO: Validate account name entered similar to the create wallet flow
        val state = (_uiState.value as? ImportAccountScreenUiState.Scanned) ?: return
        _uiState.value = state.copy(accountName = accountName)
    }

    fun onImportAccount() {
        screenModelScope.launch {
            val state = (_uiState.value as? ImportAccountScreenUiState.Scanned) ?: return@launch

            checkPublicKeyLinkedToAccountNameUseCase(EosPublicKey(state.publicKey), state.accountName).fold(
                onSuccess = { account ->
                    val request = ImportAccountRequest(
                        EosPublicKey(state.publicKey).bytes,
                        state.accountName,
                        permissionName = account.permissions.find { it.requiredAuth.keys.any { it.key == state.publicKey } }?.permissionType?.permissionName
                    )

                    _uiState.value = ImportAccountScreenUiState.Imported(
                        publicKey = state.publicKey,
                        accountName = state.accountName,
                        encodedImportedAccountRequest = encodeRequestToQrCodeUseCase(request)
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        ImportAccountScreenUiState.Scanned(
                            publicKey = state.publicKey,
                            accountName = state.accountName,
                            error = error.message
                        )
                    }
                }
            )
        }
    }
}