package com.mangala.wallet.features.chains.antelope.create_account.presentation.iap

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.GetFirstUnassignedPurchaseUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.ContinueCreateInAppPurchaseAccountUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAccountWithInAppPurchaseUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAndSaveAccountWithInAppPurchaseUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.GetAccountInAppPurchaseStatusUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.NoPurchaseAssociatedWithAccountException
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.IapProductAlreadyOwnedDialog
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.mapToErrorMessageStringResource
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountNameHashUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.MANGALA_SUPPORT_EMAIL
import com.mangala.wallet.utils.MailToFactory
import com.wallet.iap.purchases.BillingResult
import com.wallet.iap.purchases.device.PurchaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.utils.executeWithMinDelay
import com.wallet.iap.purchases.IapProduct
import com.wallet.iap.purchases.PurchaseState
import com.wallet.iap.purchases.domain.PurchaseStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlin.time.Duration.Companion.milliseconds

class IapCreateAccountScreenModel(
    private val accountNameWithSuffix: String,
    accountNameTypeString: String,
    private val skipToCreateAccountStep: Boolean,
    private val retryCreateAccountName: Boolean,
    private val purchaseToken: String?,
    private val purchaseId: String?,
    private val mailToFactory: MailToFactory,
    val purchaseManager: PurchaseManager,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val createAndSaveAccountWithInAppPurchaseUseCase: CreateAndSaveAccountWithInAppPurchaseUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountNameHashUseCase: GetAccountNameHashUseCase,
    private val continueCreateInAppPurchaseAccountUseCase: ContinueCreateInAppPurchaseAccountUseCase,
    private val getAccountInAppPurchaseStatusUseCase: GetAccountInAppPurchaseStatusUseCase,
    private val getFirstUnassignedPurchaseUseCase: GetFirstUnassignedPurchaseUseCase,
    getIsPinSetupUseCase: GetIsPinSetupUseCase
) : BaseScreenModel() {

    private val accountNameType = AccountNameType.valueOf(accountNameTypeString)
    private val isPinSetUp = getIsPinSetupUseCase()

    private val _uiModel: MutableStateFlow<IapCreateAccountUiModel> = MutableStateFlow(
        IapCreateAccountUiModel(
            accountNameType,
            blockchainType = BlockchainType.Eos,
            currentStep = if (skipToCreateAccountStep) {
                CreateAccountStep.CreateAccount()
            } else {
                CreateAccountStep.Payment(isPaymentPending = true)
            },
            isPinSetUp = isPinSetUp
        )
    )
    val uiModel: StateFlow<IapCreateAccountUiModel> get() = _uiModel.asStateFlow()

    private var product: IapProduct? = null

    private val _onStartPurchaseFlow = MutableSharedFlow<IapProduct?>(replay = 0)
    val onStartPurchaseFlow: SharedFlow<IapProduct?> = _onStartPurchaseFlow.asSharedFlow()

    lateinit var blockchainType: BlockchainType

    init {
        screenModelScope.launch {
            blockchainType = getSelectedNetworkUseCase().blockchainType

            _uiModel.update { it.copy(blockchainType = blockchainType) }

            println("IapCreateAccountScreenModel purchaseToken $purchaseToken purchaseId $purchaseId")

            if (purchaseToken != null || purchaseId.isNullOrEmpty().not()) {
                executeWithMinDelay(MINIMUM_STEP_DURATION_MS.milliseconds) {
                    val status = purchaseToken.let { purchaseManager.getPurchaseStatus(it.orEmpty(), purchaseId.orEmpty()) }

                    println("IapCreateAccountScreenModel payment status $status")

                    if (status == PurchaseStatus.SUCCESS) {
                        onCreateAccountWithInAppPurchase(purchaseToken.orEmpty(), purchaseId)
                    }
                }
            } else if (skipToCreateAccountStep) {
                onClickCreateWithExistingPurchase()
            } else if (retryCreateAccountName) {
                executeWithMinDelay(MINIMUM_STEP_DURATION_MS.milliseconds) {
                    getAccountInAppPurchaseStatusUseCase(
                        accountNameWithSuffix,
                        blockchainType
                    ).fold(
                        onSuccess = {
                            handleRetryCreateAccountByPurchaseStatus(it)
                        },
                        onFailure = {
                            if (it is NoPurchaseAssociatedWithAccountException) {
                                val purchase = getFirstUnassignedPurchaseUseCase(
                                    accountNameWithSuffix
                                ) ?: run {
                                    displayPaymentErrorMessage()
                                    return@fold
                                }
                                handleRetryCreateAccountByPurchaseStatus(purchase.getPurchaseStateEnum?.toPurchaseStatus() ?: PurchaseStatus.FAILURE, purchase.purchaseToken, purchase.orderId)
                            } else {
                                displayPaymentErrorMessage()
                            }
                        }
                    )
                }
            }
        }
        screenModelScope.launch {
            purchaseManager.purchasesFlow.collectLatest {
                when (it.getPurchaseStateEnum) {
                    PurchaseState.PURCHASED -> {
                        // We need to check this so that it won't consume another account's purchase
                        val purchaseToken = it.purchaseToken
                        val selectedProductId = product?.productId
                        val purchaseId = it.orderId

                        if (selectedProductId == it.productId && purchaseToken != null) {
                            onCreateAccountWithInAppPurchase(purchaseToken, purchaseId)
                        }
                    }

                    PurchaseState.PENDING -> {
                        _uiModel.update {
                            it.copy(
                                currentStep = CreateAccountStep.Payment(
                                    isPaymentPending = true
                                )
                            )
                        }

                        val purchase = it

                        saveAccountUseCase(
                            accountName = accountNameWithSuffix,
                            createAccountState = AntelopeAccount.CreateAccountState.IAP_PAYMENT_PENDING,
                            purchaseToken = purchase.purchaseToken,
                            isReplace = true,
                            purchaseId = purchaseId
                        )
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private suspend fun handleRetryCreateAccountByPurchaseStatus(
        it: PurchaseStatus,
        purchaseToken: String? = null,
        purchaseId: String? = null
    ) {
        when (it) {
            PurchaseStatus.PENDING -> {
                // initial state is already CreateAccountStep.Payment(isPaymentPending = true)
            }

            PurchaseStatus.SUCCESS -> {
                executeWithMinDelay(MINIMUM_STEP_DURATION_MS.milliseconds) {
                    continueCreateInAppPurchaseAccountUseCase.continueCreateAccount(
                        accountNameWithSuffix,
                        blockchainType,
                        purchaseToken,
                        purchaseId
                    ).fold(
                        onSuccess = {
                            onCreateAccountSuccess()
                        },
                        onFailure = ::onCreateAccountFromInAppPurchaseError
                    )
                }
            }

            PurchaseStatus.FAILURE -> {
                executeWithMinDelay(MINIMUM_STEP_DURATION_MS.milliseconds) {
                    continueCreateInAppPurchaseAccountUseCase.checkAccountCreated(
                        accountNameWithSuffix,
                        blockchainType
                    ).fold(
                        onSuccess = {
                            onCreateAccountSuccess()
                        },
                        onFailure = {
                            displayPaymentErrorMessage()
                        }
                    )
                }
            }
        }
    }

    private fun displayPaymentErrorMessage() {
        _uiModel.update {
            it.copy(
                currentStep = CreateAccountStep.Payment(
                    WrappedStringResource.StringRes(MR.strings.message_antelope_create_account_payment_issue)
                )
            )
        }
    }

    override fun doOnComposableStarted() {
        screenModelScope.launch {
            loadProduct(accountNameType)

            purchaseManager.billingResultFlow.collectLatest {
                when (it.responseCode) {
                    BillingResult.ResponseCode.OK -> {
                        _uiModel.update {
                            it.copy(currentStep = CreateAccountStep.CreateAccount())
                        }
                    }

                    BillingResult.ResponseCode.ITEM_ALREADY_OWNED -> {
                        _uiModel.update {
                            it.copy(
                                iapProductAlreadyOwnedDialog = IapProductAlreadyOwnedDialog(
                                    accountNameWithSuffix
                                )
                            )
                        }
                    }

                    BillingResult.ResponseCode.USER_CANCELED -> {

                    }

                    else -> {
                        _uiModel.update {
                            it.copy(
                                currentStep = CreateAccountStep.Payment(
                                    error = WrappedStringResource.StringRes(
                                        MR.strings.message_antelope_create_account_payment_cannot_process
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun onClickMainButtonPayment() {
        screenModelScope.launch {
            onRequestIapPurchase()
        }
    }

    fun onClickMainButtonCreateAccount() {
        when (uiModel.value.createAccountStepStatus) {
            CreateAccountStepStatus.FAILED -> {
                onClickCreateWithExistingPurchase()
            }

            else -> {
                // This should not happen, has we already switched to next state
            }
        }
    }

    fun getObfuscatedProfileId(): String {
        val profileId = getAccountNameHashUseCase(accountNameWithSuffix)

        return profileId
    }

    fun onRequestIapPurchaseInitiated() {
        screenModelScope.launch {
            _onStartPurchaseFlow.emit(null)
        }
    }

    fun onClickContactSupport() {
        mailToFactory.mailTo(
            MANGALA_SUPPORT_EMAIL,
            "Create account support", // TODO: Localization?
            ""
        )
    }

    fun onDismissProductAlreadyOwnedDialog() {
        _uiModel.update { it.copy(iapProductAlreadyOwnedDialog = null) }
    }

    private suspend fun onRequestIapPurchase() {
        val iapProduct = product ?: return
        val retryCreateAccountPaymentInfo = _uiModel.value.purchaseToken
        val purchaseId = _uiModel.value.purchaseId

        retryCreateAccountPaymentInfo?.let {
            // Allow retry create account if payment was successful
            onCreateAccountWithInAppPurchase(retryCreateAccountPaymentInfo, purchaseId)
        } ?: run {
            saveAccountUseCase(
                accountName = accountNameWithSuffix,
                createAccountState = AntelopeAccount.CreateAccountState.IAP_PAYMENT_INITIALIZED,
                purchaseToken = null,
                purchaseId = purchaseId
            )
            _onStartPurchaseFlow.emit(iapProduct)
        }
    }

    private suspend fun loadProduct(accountType: AccountNameType) {
        product = purchaseManager.loadProduct(accountType == AccountNameType.Premium)
    }

    fun onClickCreateWithExistingPurchase() {
        onCreateAccountWithInAppPurchase {
            executeWithMinDelay(MINIMUM_STEP_DURATION_MS.milliseconds) {
                createAndSaveAccountWithInAppPurchaseUseCase.createWithExistingPurchase(
                    accountName = accountNameWithSuffix,
                    blockchainType = blockchainType,
                    accountNameType == AccountNameType.Premium
                )
            }.fold(onSuccess = {
                onCreateAccountSuccess()
            }, onFailure = ::onCreateAccountFromInAppPurchaseError)
        }
    }

    private fun onCreateAccountWithInAppPurchase(purchaseToken: String, purchaseId: String?) {
        println("IapCreateAccountScreenModel onCreateAccountWithInAppPurchase $purchaseToken")
        onCreateAccountWithInAppPurchase { currentState ->
            executeWithMinDelay(MINIMUM_STEP_DURATION_MS.milliseconds) {
                createAndSaveAccountWithInAppPurchaseUseCase(
                    accountName = accountNameWithSuffix,
                    blockchainType = blockchainType,
                    purchaseToken = purchaseToken,
                    purchaseId = purchaseId.orEmpty()
                )
            }.fold(onSuccess = {
                onCreateAccountSuccess()
            }, onFailure = ::onCreateAccountFromInAppPurchaseError)
        }
    }

    private fun onCreateAccountWithInAppPurchase(triggerCreateAccount: suspend (currentState: IapCreateAccountUiModel) -> Unit) {
        screenModelScope.launch {
            val currentState = _uiModel.updateAndGet {
                it.copy(currentStep = CreateAccountStep.CreateAccount(), purchaseToken = purchaseToken)
            }

            triggerCreateAccount(currentState)
        }
    }

    private fun onCreateAccountSuccess() {
        screenModelScope.launch {
            _uiModel.update {
                it.copy(currentStep = CreateAccountStep.ExploreMangalaOrSetupPin())
            }

            delay(DELAY_ENABLE_PROCEED)

            _uiModel.update {
                it.copy(currentStep = CreateAccountStep.ExploreMangalaOrSetupPin(enableProceed = true))
            }
        }
    }

    private fun onCreateAccountFromInAppPurchaseError(error: Throwable) {
        val uiModel = _uiModel.value
        val iapError = error as? CreateAccountWithInAppPurchaseUseCase.CreateAccountError
        val errorMessage = iapError?.mapToErrorMessageStringResource()
            ?: MR.strings.message_antelope_create_account_payment_unknown_error

        val newState = when (iapError) {
            CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseAlreadyConsumed,
            CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseCancelled -> {
                uiModel.copy(
                    currentStep = CreateAccountStep.CreateAccount(
                        error = WrappedStringResource.StringRes(errorMessage)
                    ),
                    purchaseToken = null // Reset payment info because voided
                )
            }

            else -> {
                uiModel.copy(
                    currentStep = CreateAccountStep.CreateAccount(
                        error = WrappedStringResource.StringRes(errorMessage)
                    )
                )
            }
        }

        _uiModel.update { newState }
    }

    companion object {
        private const val DELAY_ENABLE_PROCEED = 1000L
        private const val MINIMUM_STEP_DURATION_MS = 3000L
    }
}