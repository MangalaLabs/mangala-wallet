package com.mangala.wallet.features.chains.antelope.presentation.createkeypair

import cafe.adriel.voyager.core.model.screenModelScope
import com.linh.antelope_qr.domain.model.SignedTransactionResponse
import com.linh.antelope_qr.domain.model.SyncAccountRequest
import com.linh.antelope_qr.domain.usecase.EncodeRequestToQrCodeUseCase
import com.linh.antelope_qr.domain.usecase.EncodeSyncPublicKeyPairsRequestUseCase
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.antelope_key_manager.domain.usecase.GenerateAccountKeyPairsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignTransactionUseCase
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateKeyPairScreenModel(
    private val generateAccountKeyPairsUseCase: GenerateAccountKeyPairsUseCase,
    private val encodeSyncPublicKeyPairsRequestUseCase: EncodeSyncPublicKeyPairsRequestUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val signTransactionUseCase: SignTransactionUseCase,
    private val encodeRequestToQrCodeUseCase: EncodeRequestToQrCodeUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<CreateKeyPairScreenUiState> = MutableStateFlow(
        CreateKeyPairScreenUiState.KeyNotGenerated
    )
    val uiState: StateFlow<CreateKeyPairScreenUiState> = _uiState.asStateFlow()

    private lateinit var keyPair: AccountKeyPairs

    fun createKeyPair()  {
        screenModelScope.launch {
            val keyPair = generateAccountKeyPairsUseCase()
            this@CreateKeyPairScreenModel.keyPair = keyPair

            _uiState.value = CreateKeyPairScreenUiState.KeyGenerated(
                ownerPublicKey = keyPair.ownerKeyPair.publicKey.toString(),
                ownerPrivateKey = keyPair.ownerKeyPair.toString(),
                activePublicKey = keyPair.activeKeyPair.publicKey.toString(),
                activePrivateKey = keyPair.activeKeyPair.toString(),
                encodedQrCode = encodeSyncPublicKeyPairsRequestUseCase(keyPair)
            )
        }
    }

    fun onSyncAccount(syncAccountRequest: SyncAccountRequest) {
        screenModelScope.launch {
            if (syncAccountRequest.ownerPublicKey.contentEquals(keyPair.ownerKeyPair.publicKey.bytes) &&
                syncAccountRequest.activePublicKey.contentEquals(keyPair.activeKeyPair.publicKey.bytes)) {
                saveAccountUseCase(
                    accountName = syncAccountRequest.accountName,
                    activePrivateKey = keyPair.activeKeyPair,
                    ownerPrivateKey = keyPair.ownerKeyPair
                )
                _uiState.value = CreateKeyPairScreenUiState.AccountSynced(
                    accountName = syncAccountRequest.accountName,
                    ownerPublicKey = keyPair.ownerKeyPair.publicKey.toString(),
                    activePublicKey = keyPair.activeKeyPair.publicKey.toString()
                )
            } else {
                _uiState.update {
                    val oldUiState = it as? CreateKeyPairScreenUiState.KeyGenerated ?: return@update it
                    oldUiState.copy(syncAccountError = WrappedStringResource.StringRes(MR.strings.all_public_key_not_match))
                }
            }
        }
    }

    fun onSignTransaction(signTransactionRequest: SignTransactionRequest) {
        val (actor, permissionName) = signTransactionRequest.authorization.firstOrNull() ?: return

        val signature = signTransactionUseCase(
            signTransactionRequest.chainId,
            transactionAbi = signTransactionRequest.toTransactionAbi(),
            actor = actor,
            permissionName = permissionName
        )
        val signedTransactionResponse = SignedTransactionResponse(
            signature = signature,
            signTransactionRequest = signTransactionRequest
        )
        val encodedResponse = encodeRequestToQrCodeUseCase(signedTransactionResponse)
        _uiState.update {
            val oldUiState = it as? CreateKeyPairScreenUiState.KeyGenerated ?: return@update it
            oldUiState.copy(encodedQrCode = encodedResponse)
        }
    }
}