package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.createbyfriend

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CheckCreateByFriendAccountCreatedUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.GenerateCreateByFriendQrUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.memtrip.eos.core.crypto.EosPrivateKey.Companion.toEosPrivateKeyOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateByFriendBottomSheetScreenModel(
    private val accountName: String,
    private val eosOwnerPrivateKey: String?,
    private val eosActivePrivateKey: String?,
    private val generateCreateByFriendQrUseCase: GenerateCreateByFriendQrUseCase,
    private val checkCreateByFriendAccountCreatedUseCase: CheckCreateByFriendAccountCreatedUseCase
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<CreateByFriendBottomSheetUiState> =
        MutableStateFlow(CreateByFriendBottomSheetUiState.Loading)
    val uiState: StateFlow<CreateByFriendBottomSheetUiState> = _uiState.asStateFlow()

    init {
        var accountKeyPairs: AccountKeyPairs? = null
        val detachedEosOwnerPrivateKey = eosOwnerPrivateKey?.toEosPrivateKeyOrNull()
        val detachedEosActivePrivateKey = eosActivePrivateKey?.toEosPrivateKeyOrNull()
        if (detachedEosOwnerPrivateKey != null && detachedEosActivePrivateKey != null) {
            accountKeyPairs = AccountKeyPairs(
                ownerKeyPair = detachedEosOwnerPrivateKey,
                activeKeyPair = detachedEosActivePrivateKey
            )
        }
        screenModelScope.launch {
            val accountCreationRequest = generateCreateByFriendQrUseCase(accountName, accountKeyPairs)
            _uiState.value = CreateByFriendBottomSheetUiState.Ready(accountCreationRequest)
        }
    }

    fun onClickSaveAccount() {
        screenModelScope.launch {
            val currentState =
                _uiState.value as? CreateByFriendBottomSheetUiState.Ready ?: return@launch

            _uiState.value = currentState.copy(isCheckingAccountCreated = true)

            if (checkCreateByFriendAccountCreatedUseCase(accountName = accountName)) {
                _uiState.value = CreateByFriendBottomSheetUiState.AccountCreated
            } else {
                _uiState.value = currentState.copy(
                    isCheckingAccountCreated = false,
                    checkAccountCreatedError = Throwable("Account not created")
                )
            }
        }
    }
}