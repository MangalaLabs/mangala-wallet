package com.mangala.wallet.features.chains.antelope.presentation.importaccount

import cafe.adriel.voyager.core.model.ScreenModel
import com.linh.antelope_qr.domain.model.ImportAccountRequest
import com.linh.antelope_qr.domain.usecase.EncodeRequestToQrCodeUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ImportAccountScreenModel(
    private val encodeRequestToQrCodeUseCase: EncodeRequestToQrCodeUseCase,
    private val saveAccountUseCase: SaveAccountUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<ImportAccountScreenUiState> = MutableStateFlow(ImportAccountScreenUiState.NotImported())
    val uiState: StateFlow<ImportAccountScreenUiState> = _uiState.asStateFlow()

    fun onPrivateKeyChange(privateKey: String) {
        _uiState.update {
            val state = (it as? ImportAccountScreenUiState.NotImported) ?: return@update it
            try {
                val eosPrivateKey = EosPrivateKey(privateKey)
                state.copy(privateKey = privateKey, publicKey = eosPrivateKey.publicKey.toString(), error = null)
            } catch (e: Exception) {
                state.copy(privateKey = privateKey, error = e.message)
            }
        }
    }

    fun onImportAccount() {
        val state = (_uiState.value as? ImportAccountScreenUiState.NotImported) ?: return
        val publicKey = state.publicKey ?: return
        val eosPublicKey = try {
            EosPublicKey(publicKey)
        } catch (e: Exception) {
            return
        }

        val request = ImportAccountRequest(eosPublicKey.bytes, null, null)
        val encodedRequest = encodeRequestToQrCodeUseCase(request)

        _uiState.update {
            ImportAccountScreenUiState.GeneratedKeyPair(
                privateKey = state.privateKey,
                publicKey = publicKey,
                encodedRequest = encodedRequest
            )
        }
    }

    fun onScanImportAccount(importAccountRequest: ImportAccountRequest) {
        if (importAccountRequest.accountName == null) {
            _uiState.update {
                val oldUiState = it as? ImportAccountScreenUiState.GeneratedKeyPair ?: return@update it
                oldUiState.copy(error = WrappedStringResource.StringRes(MR.strings.message_import_account_screen_model_missing_account_name))
            }
        }

        val importRequestPublicKey = EosPublicKey(importAccountRequest.publicKey)
        val state = _uiState.value as? ImportAccountScreenUiState.GeneratedKeyPair ?: return
        val permissionName = importAccountRequest.permissionName

        if (importRequestPublicKey.toString() == state.publicKey) {
            val permissionType = AntelopePermissionType.fromName(permissionName.orEmpty())
            // TODO: Support for key types other than active or owner
            val eosPrivateKey = EosPrivateKey(state.privateKey)
            saveAccountUseCase(
                accountName = importAccountRequest.accountName,
                activePrivateKey = if (permissionType is AntelopePermissionType.Active) eosPrivateKey else null,
                ownerPrivateKey = if (permissionType is AntelopePermissionType.Owner) eosPrivateKey else null
            )
            _uiState.value = ImportAccountScreenUiState.Imported(
                accountName = importAccountRequest.accountName.orEmpty(),
                publicKey = importRequestPublicKey.toString(),
                privateKey = state.privateKey
            )
        } else {
            _uiState.update {
                val oldUiState = it as? ImportAccountScreenUiState.GeneratedKeyPair ?: return@update it
                oldUiState.copy(error = WrappedStringResource.StringRes(MR.strings.all_public_key_not_match))
            }
        }
    }
}