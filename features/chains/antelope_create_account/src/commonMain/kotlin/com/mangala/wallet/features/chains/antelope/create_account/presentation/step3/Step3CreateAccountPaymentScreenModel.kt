package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.ChainException
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.antelope_key_manager.domain.usecase.GenerateAccountKeyPairsUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAccountWithInAppPurchaseUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAccountWithOwnAccountUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAndSaveAccountWithInAppPurchaseUseCase
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.mapToErrorMessageStringResource
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.CreateAccountRamOption
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountNameHashUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.BaseAntelopeTransactScreenModel
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.memtrip.eos.core.crypto.EosPrivateKey.Companion.toEosPrivateKeyOrNull
import com.wallet.iap.purchases.BillingResult
import com.wallet.iap.purchases.IapProduct
import com.wallet.iap.purchases.PaymentInfo
import com.wallet.iap.purchases.PurchaseState
import com.wallet.iap.purchases.device.PurchaseManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Step3CreateAccountPaymentScreenModel(
    initialAccountName: String,
    initialAccountSuffix: String?,
    initialAccountNameType: AccountNameType,
    val eosOwnerPrivateKey: String? = null,
    val eosActivePrivateKey: String? = null,
    private val createAndSaveAccountWithInAppPurchaseUseCase: CreateAndSaveAccountWithInAppPurchaseUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAntelopeAccountsUseCase: GetAccountsUseCase,
    private val validateAccountUseCase: ValidateAccountUseCase,
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase,
    // Temp for create account with own account
    private val createAndSaveAccountWithOwnAccount: CreateAccountWithOwnAccountUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val generateAccountKeyPairsUseCase: GenerateAccountKeyPairsUseCase,
    val purchaseManager: PurchaseManager,
    private val getAccountNameHashUseCase: GetAccountNameHashUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
): BaseAntelopeTransactScreenModel(
    transactUseCase = createAndSaveAccountWithOwnAccount,
    blockchainUid = ""
) {

    private val _uiState: MutableStateFlow<Step3CreateAccountPaymentUiState> = MutableStateFlow(
        Step3CreateAccountPaymentUiState.Loading
    )
    val uiState: StateFlow<Step3CreateAccountPaymentUiState> = _uiState.asStateFlow()

    private val _onStartPurchaseFlow = MutableSharedFlow<IapProduct?>(replay = 0)
    val onStartPurchaseFlow: SharedFlow<IapProduct?> = _onStartPurchaseFlow.asSharedFlow()

    private val _navigateToCreateIapScreen = MutableSharedFlow<PaymentInfo?>(replay = 0)
    val navigateToCreateIapScreen: SharedFlow<PaymentInfo?> = _navigateToCreateIapScreen.asSharedFlow()

    private var product: IapProduct? = null

    private var newAccountKeyPairs: AccountKeyPairs? = null

    private var eosAccountKeyPairs: AccountKeyPairs? = null
    
    init {
        screenModelScope.launch {
            // Convert eos private keys to account key pairs
            val detachedEosOwnerPrivateKey = eosOwnerPrivateKey?.toEosPrivateKeyOrNull()
            val detachedEosActivePrivateKey = eosActivePrivateKey?.toEosPrivateKeyOrNull()
            if (detachedEosOwnerPrivateKey != null && detachedEosActivePrivateKey != null) {
                eosAccountKeyPairs = AccountKeyPairs(
                    ownerKeyPair = detachedEosOwnerPrivateKey,
                    activeKeyPair = detachedEosActivePrivateKey
                )
            }


            blockchainUid = getSelectedNetworkUseCase().blockChainUid
            val accounts = getAntelopeAccountsUseCase()

            loadProduct(initialAccountNameType)

            _uiState.value = Step3CreateAccountPaymentUiState.Ready(
                accountName = initialAccountName,
                accountNameSuffix = initialAccountSuffix.orEmpty(),
                accountNameType = initialAccountNameType,
                eosOwnerPrivateKey = eosOwnerPrivateKey,
                eosActivePrivateKey = eosActivePrivateKey,
                selectedAccountIndex = 0,
                accounts = accounts,
                accountNameError = false,
                selectedPaymentOption = null,
                availablePaymentOptions = getAvailablePaymentOptions(initialAccountNameType),
                blockchainType = blockchainType,
                isPinSetup = getIsPinSetupUseCase(),
                promptConfirmTransaction = false,
                iapProduct = product
            )

            purchaseManager.purchasesFlow.collectLatest {
                println("Step3CreateAccountPayment purchase $it")
                when (it.getPurchaseStateEnum) {
                    PurchaseState.PURCHASED -> {
                        // We need to check this so that it won't consume another account's purchase
                        val purchaseToken = it.purchaseToken
                        val selectedProductId = product?.productId

                        if (selectedProductId == it.productId && purchaseToken != null) {
                            onCreateAccountWithInAppPurchase(it)
                        }
                    }

                    PurchaseState.PENDING -> {
                        val currentState =
                            (uiState.value as? Step3CreateAccountPaymentUiState.Ready)
                        val purchase = it

                        currentState?.let {
                            // Save account so that user can retry creating account/ auto retry when reopening app
                            saveAccountUseCase(
                                accountName = currentState.accountNameWithSuffix,
                                createAccountState = AntelopeAccount.CreateAccountState.IAP_PAYMENT_PENDING,
                                purchaseToken = purchase.purchaseToken,
                                purchaseId = purchase.orderId,
                                isReplace = true
                            )

                            _navigateToCreateIapScreen.emit(purchase)
                        }
                    }

                    PurchaseState.UNSPECIFIED_STATE -> {
                        val currentState =
                            (uiState.value as? Step3CreateAccountPaymentUiState.Ready)

                        currentState?.let {
                            _uiState.update { currentState.copy(isLoading = false) }
                        }
                    }

                    else -> {
                    }
                }
            }
        }
        screenModelScope.launch {
            purchaseManager.billingResultFlow.collectLatest {
                when (it.responseCode) {
                    BillingResult.ResponseCode.OK -> {

                    }

                    BillingResult.ResponseCode.ITEM_ALREADY_OWNED -> {
                        val currentState =
                            (uiState.value as? Step3CreateAccountPaymentUiState.Ready)

                        currentState?.let {
                            _uiState.update {
                                currentState.copy(
                                    iapProductAlreadyOwnedDialog = IapProductAlreadyOwnedDialog(
                                        currentState.accountNameWithSuffix
                                    )
                                )
                            }
                        }
                    }

                    else -> {
                        val currentState =
                            (uiState.value as? Step3CreateAccountPaymentUiState.Ready)

                        currentState?.let {
                            _uiState.update { currentState.copy(isLoading = false) }
                        }
                    }
                }
            }
        }
    }

    fun onDismissProductAlreadyOwnedDialog() {
        val currentState =
            (uiState.value as? Step3CreateAccountPaymentUiState.Ready) ?: return

        _uiState.update { currentState.copy(iapProductAlreadyOwnedDialog = null) }
    }

    private suspend fun loadProduct(accountType: AccountNameType) {
        product = purchaseManager.loadProduct(accountType == AccountNameType.Premium)
    }

    fun onClickPaymentOption(paymentOption: PaymentOption) {
        screenModelScope.launch {
            _uiState.value = (uiState.value as Step3CreateAccountPaymentUiState.Ready).copy(selectedPaymentOption = paymentOption)
        }
    }

    fun onClickCreateAccount() {
        val currentState = _uiState.value as? Step3CreateAccountPaymentUiState.Ready ?: return
        _uiState.update {
            currentState.copy(createAccountState = CreateAccountState.NotCreated)
        }
    }

    fun onRequestIapPurchase() {
        screenModelScope.launch {
            val currentState =
                (uiState.value as? Step3CreateAccountPaymentUiState.Ready) ?: return@launch
            val iapProduct = product ?: return@launch
            val retryCreateAccountPaymentInfo = currentState.paymentInfo

            retryCreateAccountPaymentInfo?.let {
                // Allow retry create account if payment was successful
                onCreateAccountWithInAppPurchase(retryCreateAccountPaymentInfo)
            } ?: run {
                saveAccountUseCase(
                    accountName = currentState.accountNameWithSuffix,
                    createAccountState = AntelopeAccount.CreateAccountState.IAP_PAYMENT_INITIALIZED,
                    purchaseToken = null,
                    purchaseId = null
                )
                _onStartPurchaseFlow.emit(iapProduct)
            }
        }
    }

    fun onRequestIapPurchaseInitiated() {
        screenModelScope.launch {
            _onStartPurchaseFlow.emit(null)
        }
    }

    fun getObfuscatedProfileId(): String {
        val currentState = (uiState.value as? Step3CreateAccountPaymentUiState.Ready)
        val accountName = currentState?.accountNameWithSuffix ?: return ""

        val profileId = getAccountNameHashUseCase(accountName)

        println("Step3CreateAccountPaymentScreenModel accountName $accountName profileId $profileId")

        return profileId
    }

    fun onAccountTypeChange(newAccountType: AccountNameType) {
        val currentState = _uiState.value as Step3CreateAccountPaymentUiState.Ready

        if (currentState.accountNameType == newAccountType) return

        screenModelScope.launch {
            loadProduct(newAccountType)

            val availablePaymentOptions = getAvailablePaymentOptions(newAccountType)

            _uiState.value = currentState.copy(
                accountNameType = newAccountType,
                accountNameSuffix = if (newAccountType == AccountNameType.Premium) AntelopeAccount.getPremiumAccountSuffix(blockchainType) else "",
                accountNameError = validateAccountUseCase.validateAccountName(
                    currentState.accountNameWithSuffix,
                    newAccountType,
                    checkForSuffix = true
                ).isValid.not(),
                availablePaymentOptions = availablePaymentOptions,
                iapProduct = product
            )
        }
    }

    fun onAccountNameChange(accountName: String) {
        screenModelScope.launch {
            val currentState = _uiState.value as Step3CreateAccountPaymentUiState.Ready
            val validationResult = validateAccountUseCase.validateAccountName(accountName + currentState.accountNameSuffix, currentState.accountNameType, checkForSuffix = true)
            _uiState.value = currentState.copy(
                accountName = accountName,
                accountNameError = !validationResult.isValid
            )
        }
    }

    fun onPaymentAccountChange(accountName: String) {
        screenModelScope.launch {
            val accounts = (uiState.value as? Step3CreateAccountPaymentUiState.Ready)?.accounts ?: return@launch
            val selectedAccountIndex = accounts.indexOfFirst { it.accountName == accountName }
            _uiState.value = (uiState.value as Step3CreateAccountPaymentUiState.Ready).copy(selectedAccountIndex = selectedAccountIndex)
        }
    }

    private fun onCreateAccountWithInAppPurchase(paymentInfo: PaymentInfo) {
        screenModelScope.launch {
            _navigateToCreateIapScreen.emit(paymentInfo)
        }
    }

    override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
        val uiState = (_uiState.value as? Step3CreateAccountPaymentUiState.Ready) ?: return

        _uiState.value =
            uiState.copy(
                resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                resourceRequiredTotal = resourceProviderResponse.fee,
                createAccountState = CreateAccountState.Creating
            )
    }

    override fun onRequestTransactionInvalidRequest() {
        onCreateAccountError(Exception("Failed to create account"))
    }

    override fun onRequestTransactionResourceCovered() {
        val currentState =
            _uiState.value as? Step3CreateAccountPaymentUiState.Ready ?: return

        _uiState.value = currentState.copy(promptConfirmTransaction = true, createAccountState = CreateAccountState.Creating)
    }

    override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
        val uiState = uiState.value as? Step3CreateAccountPaymentUiState.Ready ?: return Result.failure(Exception("Invalid state"))
        val account = uiState.selectedAccount ?: return Result.failure(Exception("Invalid state"))

        val keyPairs = eosAccountKeyPairs ?: generateAccountKeyPairsUseCase()
        newAccountKeyPairs = keyPairs

        return createAndSaveAccountWithOwnAccount.requestCreateAccount(
            newAccountName = uiState.accountNameWithSuffix,
            account = account,
            createAccountRamOption = CreateAccountRamOption.BUY_RAM, // TODO: Allow toggle
            blockchainType,
            keyPairs
        )
    }

    override fun onDismissTransactionFeeBreakdown() {
        val uiState = (_uiState.value as? Step3CreateAccountPaymentUiState.Ready) ?: return

        _uiState.update {
            uiState.copy(resourceRequiredBreakdown = null, resourceRequiredTotal = null, createAccountState = CreateAccountState.NotCreated)
        }
    }

    override fun onPinPromptShown() {
        val uiState = (_uiState.value as? Step3CreateAccountPaymentUiState.Ready) ?: return

        _uiState.update {
            uiState.copy(promptConfirmTransaction = false, resourceRequiredBreakdown = null, resourceRequiredTotal = null)
        }
    }

    override fun onConfirmResourceProviderFee() {
        val currentState = _uiState.value as? Step3CreateAccountPaymentUiState.Ready ?: return

        _uiState.value = currentState.copy(promptConfirmTransaction = true, createAccountState = CreateAccountState.Creating)
    }

    override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
        val uiState = uiState.value as? Step3CreateAccountPaymentUiState.Ready ?: return Result.failure(Exception("Invalid state"))
        val account = uiState.selectedAccount ?: return Result.failure(Exception("Invalid state"))

        return createAndSaveAccountWithOwnAccount.pushCreateAccount(
            newAccountName = uiState.accountNameWithSuffix,
            account = account,
            createAccountRamOption = CreateAccountRamOption.BUY_RAM, // TODO: Allow toggle
            blockchainType,
            newAccountKeyPairs ?: return Result.failure(Exception("Invalid state"))
        )
    }

    override fun showLoadingState() {
        val uiState = (_uiState.value as? Step3CreateAccountPaymentUiState.Ready) ?: return

        _uiState.update {
            uiState.copy(createAccountState = CreateAccountState.Creating)
        }
    }

    override fun onPushTransactionSuccess(txHash: String) {
        onCreateAccountSuccess()
    }

    override fun onPushTransactionFail(throwable: Throwable) {
        onCreateAccountError(throwable)
    }

    fun onConsumeAccountCreatedState() {
        val uiState = uiState.value as? Step3CreateAccountPaymentUiState.Ready ?: return

        _uiState.value = uiState.copy(createAccountState = CreateAccountState.NotCreated)
    }

    private fun getAvailablePaymentOptions(accountNameType: AccountNameType): List<PaymentOption> {
        return when (accountNameType) {
            AccountNameType.Standard -> {
                val items = mutableListOf(PaymentOption.EXISTING_IMPORTED_ACCOUNT)

                val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()

                if (isDevelopmentEnvironment && blockchainType == BlockchainType.EosJungleTestnet) {
                    items.add(PaymentOption.IN_APP_PURCHASE)
                }

                if (blockchainType == BlockchainType.Eos) {
                    items.add(PaymentOption.IN_APP_PURCHASE)
                }

                if (isDevelopmentEnvironment) {
                    items.add(PaymentOption.ASK_A_FRIEND_TO_CREATE)
                    items.add(PaymentOption.PAY_WITH_CRYPTO)
                }

                items
            }

            AccountNameType.Premium -> {
                listOf(
                    PaymentOption.IN_APP_PURCHASE,
                    PaymentOption.PAY_WITH_CRYPTO
                )
            }

            else -> return emptyList()
        }
    }

    private fun onCreateAccountSuccess(saveAccount: Boolean = true) {
        screenModelScope.launch {
            val uiState = uiState.value as? Step3CreateAccountPaymentUiState.Ready ?: return@launch

            if (saveAccount) {
                val keyPairs = newAccountKeyPairs ?: return@launch
                saveAccountUseCase(
                    accountName = uiState.accountNameWithSuffix,
                    activePrivateKey = keyPairs.activeKeyPair,
                    ownerPrivateKey = keyPairs.ownerKeyPair,
                    createAccountState = AntelopeAccount.CreateAccountState.DONE,
                )
            }

            _uiState.update {
                uiState.copy(createAccountState = CreateAccountState.Created, paymentInfo = null)
            }
        }
    }

    private fun onCreateAccountError(data: Throwable) {
        val uiState = uiState.value as? Step3CreateAccountPaymentUiState.Ready ?: return
        _uiState.update {
            val errorMessageString = if (data is ChainException) {
                when (data.chainError.error.name) {
                    "account_name_exists_exception" -> MR.strings.message_antelope_create_account_already_exist
                    else -> MR.strings.message_antelope_create_account_error
                }
            } else {
                MR.strings.message_antelope_create_account_error
            }

            uiState.copy(
                createAccountState = CreateAccountState.Error(errorMessageString),
                isLoading = false
            )
        }
    }

    private fun onCreateAccountFromInAppPurchaseError(error: Throwable) {
        val uiState = uiState.value as? Step3CreateAccountPaymentUiState.Ready ?: return

        val iapError = error as? CreateAccountWithInAppPurchaseUseCase.CreateAccountError
        val errorMessage = iapError?.mapToErrorMessageStringResource()
            ?: MR.strings.message_antelope_create_account_payment_unknown_error

        val newState = when (iapError) {
            CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseAlreadyConsumed,
            CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseCancelled -> {
                uiState.copy(
                    createAccountState = CreateAccountState.Error(errorMessage),
                    isLoading = false,
                    paymentInfo = null // Reset payment info because voided
                )
            }

            else -> {
                uiState.copy(
                    createAccountState = CreateAccountState.Error(errorMessage),
                    isLoading = false,
                )
            }
        }

        _uiState.update { newState }
    }
}