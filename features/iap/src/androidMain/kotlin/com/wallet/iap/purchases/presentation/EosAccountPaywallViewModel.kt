//package com.wallet.iap.purchases.presentation
//
//import android.app.Activity
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
//import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
//import com.soywiz.krypto.sha256
//import com.wallet.iap.purchases.IapProduct
//import com.wallet.iap.purchases.device.IapManager
//import com.wallet.iap.purchases.device.getFirebaseInstallationId
//import kotlinx.coroutines.launch
//import org.koin.core.component.KoinComponent
//import org.koin.core.component.inject
//
//class EosAccountPaywallViewModel : ViewModel(), KoinComponent {
//
//    private val saveAccountUseCase: SaveAccountUseCase by inject()
//
//    val iapProductDetailsResult = mutableStateOf(listOf<IapProduct>())
//    val isLoading = mutableStateOf(false)
//
//    fun init(isPremiumAccount: Boolean) {
//        viewModelScope.launch {
//            isLoading.value = true
//
//            iapProductDetailsResult.value = IapManager.loadProducts(isPremiumAccount)
//
//            isLoading.value = false
//        }
//    }
//
//    fun onPurchase(activity: Activity, productDetails: IapProduct) {
//        viewModelScope.launch {
//            val firebaseInstallationId = getFirebaseInstallationId()
//            val obfuscatedId = firebaseInstallationId?.toByteArray()?.sha256()?.hex
//
//            IapManager.launchPurchaseFlow(
//                activity,
//                productDetails,
//                obfuscatedId,
//                obfuscatedId
//            )
//        }
//    }
//
//    fun onPurchasePending(accountName: String, purchaseToken: String) {
//        viewModelScope.launch {
//            saveAccountUseCase(
//                accountName = accountName,
//                createAccountState = AntelopeAccount.CreateAccountState.IAP_PAYMENT_PENDING,
//                purchaseToken = purchaseToken
//            )
//        }
//    }
//}