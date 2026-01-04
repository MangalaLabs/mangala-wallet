package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.GetFirstUnassignedPurchaseUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountNameHashUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
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

class Step3AccountReadyToClaimScreenModel(
    private val initialAccountName: String,
    private val initialAccountSuffix: String,
    private val initialAccountType: AccountNameType,
    val purchaseManager: PurchaseManager,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val getAccountNameHashUseCase: GetAccountNameHashUseCase,
    private val getFirstUnassignedPurchaseUseCase: GetFirstUnassignedPurchaseUseCase
) : BaseScreenModel() {
    
    private val _uiState = MutableStateFlow(
        Step3AccountReadyToClaimUiState(
            accountName = initialAccountName,
            accountSuffix = initialAccountSuffix,
            accountType = initialAccountType,
            isLoading = false
        )
    )
    val uiState: StateFlow<Step3AccountReadyToClaimUiState> = _uiState.asStateFlow()
    
    private val _onStartPurchaseFlow = MutableSharedFlow<IapProduct?>(replay = 0)
    val onStartPurchaseFlow: SharedFlow<IapProduct?> = _onStartPurchaseFlow.asSharedFlow()
    
    private val _navigateToCreatingAccountScreen = MutableSharedFlow<Unit>(replay = 0)
    val navigateToCreatingAccountScreen: SharedFlow<Unit> = _navigateToCreatingAccountScreen.asSharedFlow()
    
    private val _showProductAlreadyOwnedDialog = MutableSharedFlow<String>(replay = 0)
    val showProductAlreadyOwnedDialog: SharedFlow<String> = _showProductAlreadyOwnedDialog.asSharedFlow()
    
    private var product: IapProduct? = null
    private var blockchainUid: String = ""
    
    init {
        screenModelScope.launch {
            blockchainUid = getSelectedNetworkUseCase().blockChainUid
            loadProduct()
            checkForUnconsumedPurchases()
            observePurchaseFlow()
            observeBillingResults()
        }
    }
    
    private suspend fun loadProduct() {
        product = purchaseManager.loadProduct(initialAccountType == AccountNameType.Premium)
        _uiState.update { currentState ->
            currentState.copy(iapProduct = product)
        }
    }
    
    private suspend fun checkForUnconsumedPurchases() {
        try {
            val unassignedPurchase = getFirstUnassignedPurchaseUseCase(
                accountNameWithSuffix = "$initialAccountName$initialAccountSuffix"
            )
            
            if (unassignedPurchase != null) {
                // User has an unconsumed purchase, allow direct account creation
                _uiState.update { currentState ->
                    currentState.copy(
                        hasUnconsumedPurchase = true,
                        existingPurchaseInfo = unassignedPurchase
                    )
                }
            }
        } catch (e: Exception) {
            println("Error checking unconsumed purchases: ${e.message}")
        }
    }
    
    private suspend fun observePurchaseFlow() {
        purchaseManager.purchasesFlow.collectLatest { paymentInfo ->
            println("Step3AccountReadyToClaim purchase: $paymentInfo")
            when (paymentInfo.getPurchaseStateEnum) {
                PurchaseState.PURCHASED -> {
                    val purchaseToken = paymentInfo.purchaseToken
                    val selectedProductId = product?.productId
                    
                    if (selectedProductId == paymentInfo.productId && purchaseToken != null) {
                        onPurchaseSuccessful(paymentInfo)
                    }
                }
                
                PurchaseState.PENDING -> {
                    // Save account with pending state
                    saveAccountUseCase(
                        accountName = "$initialAccountName$initialAccountSuffix",
                        createAccountState = AntelopeAccount.CreateAccountState.IAP_PAYMENT_PENDING,
                        purchaseToken = paymentInfo.purchaseToken,
                        purchaseId = paymentInfo.orderId,
                        isReplace = true
                    )
                    
                    _uiState.update { it.copy(isLoading = false, purchaseState = PurchaseState.PENDING) }
                }
                
                PurchaseState.UNSPECIFIED_STATE -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
                
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
    
    private suspend fun observeBillingResults() {
        purchaseManager.billingResultFlow.collectLatest { billingResult ->
            when (billingResult.responseCode) {
                BillingResult.ResponseCode.OK -> {
                    // Purchase flow completed successfully
                }
                
                BillingResult.ResponseCode.ITEM_ALREADY_OWNED -> {
                    _showProductAlreadyOwnedDialog.emit("$initialAccountName$initialAccountSuffix")
                }
                
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
    
    private suspend fun onPurchaseSuccessful(paymentInfo: PaymentInfo) {
        // Save account state and navigate to creating account screen
        saveAccountUseCase(
            accountName = "$initialAccountName$initialAccountSuffix",
            createAccountState = AntelopeAccount.CreateAccountState.IAP_PAYMENT_PENDING,
            purchaseToken = paymentInfo.purchaseToken,
            purchaseId = paymentInfo.orderId,
            isReplace = true
        )
        
        _navigateToCreatingAccountScreen.emit(Unit)
    }
    
    fun onPurchaseClick() {
        screenModelScope.launch {
            val currentState = _uiState.value
            
            if (currentState.hasUnconsumedPurchase && currentState.existingPurchaseInfo != null) {
                // User has an existing purchase, proceed directly to account creation
                onPurchaseSuccessful(currentState.existingPurchaseInfo)
            } else {
                // Start new purchase flow
                _uiState.update { it.copy(isLoading = true) }
                
                val iapProduct = product ?: return@launch
                
                // Save account with initialized state
                saveAccountUseCase(
                    accountName = "$initialAccountName$initialAccountSuffix",
                    createAccountState = AntelopeAccount.CreateAccountState.IAP_PAYMENT_INITIALIZED,
                    purchaseToken = null,
                    purchaseId = null,
                    isReplace = true
                )
                
                _onStartPurchaseFlow.emit(iapProduct)
            }
        }
    }
    
    fun onPurchaseFlowInitiated() {
        screenModelScope.launch {
            _onStartPurchaseFlow.emit(null)
        }
    }
    
    fun getObfuscatedProfileId(): String {
        val accountName = "$initialAccountName$initialAccountSuffix"
        return getAccountNameHashUseCase(accountName)
    }
    
    fun onDismissProductAlreadyOwnedDialog() {
        // Dialog dismissed, user can try again or proceed with existing purchase
    }
    
    fun onConfirmProductAlreadyOwned() {
        // User confirmed they want to use existing purchase for account creation
        _navigateToCreatingAccountScreen.tryEmit(Unit)
    }
    
}

data class Step3AccountReadyToClaimUiState(
    val accountName: String,
    val accountSuffix: String,
    val accountType: AccountNameType,
    val isLoading: Boolean,
    val iapProduct: IapProduct? = null,
    val hasUnconsumedPurchase: Boolean = false,
    val existingPurchaseInfo: PaymentInfo? = null,
    val purchaseState: PurchaseState? = null,
    val error: String? = null
) {
    val accountNameWithSuffix: String
        get() = "$accountName$accountSuffix"
    
        
    val displayPrice: String
        get() = iapProduct?.formattedPrice ?: "Unknown"
}