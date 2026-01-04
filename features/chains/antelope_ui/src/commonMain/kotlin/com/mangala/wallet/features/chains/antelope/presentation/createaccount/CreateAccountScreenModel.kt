package com.mangala.wallet.features.chains.antelope.presentation.createaccount

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.linh.antelope_qr.domain.model.SignedTransactionResponse
import com.linh.antelope_qr.domain.usecase.EncodeRequestToQrCodeUseCase
import com.linh.antelope_qr.domain.usecase.EncodeSyncAccountRequestUseCase
import com.mangala.wallet.features.chains.antelope.domain.usecase.PushSignedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.model.CreateAccountRamOption
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GenerateRandomAccountNameUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.memtrip.eos.core.crypto.EosPublicKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateAccountScreenModel(
    private val ownerPublicKeyBytes: ByteArray,
    private val activePublicKeyBytes: ByteArray,
    private val validateAccountUseCase: ValidateAccountUseCase,
    private val generateCreateAccountSignRequestUseCase: GenerateCreateAccountSignRequestUseCase,
    private val createAccountWithInAppPurchaseUseCase: CreateAccountWithInAppPurchaseUseCase,
    private val generateRandomAccountNameUseCase: GenerateRandomAccountNameUseCase,
    private val encodeSyncAccountRequestUseCase: EncodeSyncAccountRequestUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountPermissionUseCase: GetAccountPermissionsUseCase,
    private val encodeRequestToQrCodeUseCase: EncodeRequestToQrCodeUseCase,
    private val pushSignedTransactionUseCase: PushSignedTransactionUseCase
): ScreenModel {

    private val ownerPublicKey: EosPublicKey = EosPublicKey(ownerPublicKeyBytes)
    private val activePublicKey: EosPublicKey = EosPublicKey(activePublicKeyBytes)
    
    private val _uiState: MutableStateFlow<CreateAccountScreenUiState> = MutableStateFlow(CreateAccountScreenUiState.AccountNameNotConfirmed(
        ownerPublicKey = ownerPublicKey.toString(),
        activePublicKey = activePublicKey.toString(),
        accountName = "",
        isLoading = false
    ))
    val uiState: StateFlow<CreateAccountScreenUiState> = _uiState.asStateFlow()

    fun onAccountNameChanged(newAccountName: String) {
        _uiState.update {
            val oldUiState = it as? CreateAccountScreenUiState.AccountNameNotConfirmed ?: return
            val validationResult = if (newAccountName.isBlank()) {
                null
            } else {
                validateAccountUseCase.validateAccountName(newAccountName)
            }
            oldUiState.copy(accountName = newAccountName, accountCharacterValidationResult = validationResult, error = null)
        }
    }

    fun onGenerateRandomAccountName() {
        val randomAccountName = generateRandomAccountNameUseCase()
        onAccountNameChanged(randomAccountName)
    }

    fun onConfirmAccountName() {
        screenModelScope.launch {
            val uiState = (uiState.value as? CreateAccountScreenUiState.AccountNameNotConfirmed) ?: return@launch

            _uiState.value = uiState.copy(isLoading = true)

            val isNewAccount = validateAccountUseCase.isNewAccount(uiState.accountName)
            if (!isNewAccount) {
                _uiState.value = uiState.copy(error = WrappedStringResource.StringRes(MR.strings.message_create_account_screen_model_account_already_exists))
                return@launch
            }

            _uiState.value = CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption(
                ownerPublicKey = ownerPublicKey.toString(),
                activePublicKey = activePublicKey.toString(),
                accountName = uiState.accountName,
                accountCreationPaymentType = AccountCreationPaymentType.InAppPurchase
            )
        }
    }

    fun onSelectIapResourcePaymentOption() {
        _uiState.update {
            val oldUiState = it as? CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption ?: return
            oldUiState.copy(accountCreationPaymentType = AccountCreationPaymentType.InAppPurchase)
        }
    }

    fun onSelectFromOwnAccountResourcePaymentOption() {
        _uiState.update {
            val oldUiState = it as? CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption ?: return
            oldUiState.copy(accountCreationPaymentType = AccountCreationPaymentType.FromOwnAccount(CreateAccountRamOption.BUY_RAM))
        }
    }

    fun onSelectBuyRamResourcePaymentOption() {
        _uiState.update {
            val oldUiState = it as? CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption ?: return
            oldUiState.copy(accountCreationPaymentType = AccountCreationPaymentType.FromOwnAccount(CreateAccountRamOption.BUY_RAM))
        }
    }

    fun onSelectTransferRamResourcePaymentOption() {
        _uiState.update {
            val oldUiState = it as? CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption ?: return
            oldUiState.copy(accountCreationPaymentType = AccountCreationPaymentType.FromOwnAccount(CreateAccountRamOption.TRANSFER_RAM))
        }
    }

    fun onCreateAccount() {
        screenModelScope.launch {
            val uiState = (uiState.value as? CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption) ?: return@launch

            _uiState.update {
                (it as? CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption)?.copy(isLoading = true) ?: it
            }

            when (uiState.accountCreationPaymentType) {
                AccountCreationPaymentType.InAppPurchase -> {
                    createFreeJungleTestnetAccountUseCase(
                        accountName = uiState.accountName,
                        ownerPublicKey = uiState.ownerPublicKey,
                        activePublicKey = uiState.activePublicKey
                    ).fold(
                        onSuccess = {
                            onCreateAccountSuccess(uiState)
                        },
                        onFailure = { error ->
                            onCreateAccountError(error)
                        }
                    )
                }
                is AccountCreationPaymentType.FromOwnAccount -> {
                    val accounts = getAccountsUseCase()
                    val firstAccount = accounts.firstOrNull() ?: return@launch // TODO: Allow users to choose account
                    val permissions = getAccountPermissionUseCase(firstAccount.accountName)
                    val activePermission = permissions.firstOrNull { it.permissionType is AntelopePermissionType.Active } ?: return@launch // TODO: Allow users to choose permission to sign
                    val createAccountTransaction = generateCreateAccountSignRequestUseCase(
                        creatorAccountName = firstAccount.accountName,
                        signingPermissionName = activePermission.permissionType.permissionName,
                        accountName = uiState.accountName,
                        newAccountOwnerPublicKey = uiState.ownerPublicKey,
                        newAccountActivePublicKey = uiState.activePublicKey,
                        type = uiState.accountCreationPaymentType.ramOption
                    ) ?: return@launch
                    val encodedRequest = encodeRequestToQrCodeUseCase(createAccountTransaction)
                    _uiState.update {
                        (it as? CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption)?.copy(isLoading = false, encodedSignRequest = encodedRequest) ?: it
                    }
                }
                else -> {}
            }
        }
    }

    fun onPushSignedTransaction(signedTransactionResponse: SignedTransactionResponse) {
        screenModelScope.launch {
            pushSignedTransactionUseCase(signedTransactionResponse)
        }
    }

    private fun onCreateAccountError(error: Throwable) {
        _uiState.update {
            (it as? CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption)?.copy(
                isLoading = false,
                error = error.message.orEmpty()
            ) ?: it
        }
    }

    private fun onCreateAccountSuccess(uiState: CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption) {
        val encodedSyncAccountRequest = encodeSyncAccountRequestUseCase(
            ownerPublicKey = ownerPublicKeyBytes,
            activePublicKey = activePublicKeyBytes,
            accountName = uiState.accountName
        )

        saveAccountUseCase(
            accountName = uiState.accountName,,
        )

        _uiState.update {
            CreateAccountScreenUiState.AccountCreated(
                ownerPublicKey = ownerPublicKey.toString(),
                activePublicKey = activePublicKey.toString(),
                accountName = uiState.accountName,
                encodedSyncAccountRequest = encodedSyncAccountRequest
            )
        }
    }
}